package dev.forgebound.screen;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.block.ForgingStationBlockEntity;
import dev.forgebound.block.ModBlocks;
import dev.forgebound.classes.ClassManager;
import dev.forgebound.classes.ForgeClass;
import dev.forgebound.forging.ForgeMaterial;
import dev.forgebound.forging.ForgeProgressState;
import dev.forgebound.forging.ForgingMath;
import dev.forgebound.forging.ForgingService;
import dev.forgebound.forging.Gear;
import dev.forgebound.quality.ItemQuality;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

/**
 * Server-autoritatives Schmiedemenue. Der Client schickt nur Knopfdruecke;
 * Treffer, Punkte, Seltenheit und Gegenstand werden ausschliesslich auf dem
 * Server berechnet. Das fertige Stueck wird nicht ins Inventar gegeben, sondern
 * auf dem Amboss enthuellt und dort eingesammelt. Die Klasse des Spielers wirkt
 * ueber effektive Schmiedelevel und einen Erfahrungsfaktor.
 */
public class ForgingScreenHandler extends ScreenHandler {
	// Property-Indizes (Werte muessen in einen Short passen)
	public static final int P_STATE = 0;
	public static final int P_MATERIAL = 1;
	public static final int P_STRIKES = 2;
	public static final int P_POINTS = 3;
	public static final int P_PHASE = 4;
	public static final int P_TARGET = 5;
	public static final int P_LEVEL = 6;
	public static final int P_XP = 7;
	public static final int P_RESULT = 8;
	public static final int P_LAST = 9;
	public static final int P_GEAR = 10;
	public static final int P_CLASS = 11;
	private static final int PROPERTY_COUNT = 12;

	public static final int STATE_IDLE = 0;
	public static final int STATE_FORGING = 1;
	public static final int STATE_DONE = 2;

	// Knopf-IDs: 0 bis 5 waehlen das Material, 20 bis 24 die Ausruestungsart
	public static final int BTN_START = 10;
	public static final int BTN_STRIKE = 11;
	public static final int BTN_RESET = 12;
	public static final int BTN_GEAR_BASE = 20;

	private final PropertyDelegate data = new ArrayPropertyDelegate(PROPERTY_COUNT);
	private final ScreenHandlerContext context;
	private final PlayerEntity player;
	private ForgeMaterial forgingMaterial;
	private Gear forgingGear;
	private long lastStrikeTick;

	/** Client-Konstruktor. */
	public ForgingScreenHandler(int syncId, PlayerInventory inventory) {
		this(syncId, inventory, ScreenHandlerContext.EMPTY);
	}

	/** Server-Konstruktor. */
	public ForgingScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
		super(ModScreens.FORGING_HANDLER, syncId);
		this.player = inventory.player;
		this.context = context;
		addProperties(data);
		data.set(P_MATERIAL, ForgeMaterial.IRON.ordinal());
		data.set(P_GEAR, Gear.SWORD.ordinal());
		data.set(P_RESULT, -1);
		data.set(P_LAST, -1);
		data.set(P_LEVEL, 1);
		if (!player.getWorld().isClient) {
			syncProgress();
		}
	}

	public int get(int index) {
		return data.get(index);
	}

	private void syncProgress() {
		MinecraftServer server = player.getServer();
		if (server == null) {
			return;
		}
		ForgeProgressState state = ForgeProgressState.get(server);
		data.set(P_LEVEL, state.level(player.getUuid()));
		data.set(P_XP, state.xp(player.getUuid()));
		ForgeClass cls = ClassManager.get(server, player.getUuid());
		data.set(P_CLASS, cls == null ? 0 : cls.propertyValue());
	}

	/** Schmiedelevel plus Klassenbonus. Bestimmt Zielzonen und Chancen. */
	private int effectiveLevel() {
		ForgeClass cls = ForgeClass.fromProperty(data.get(P_CLASS));
		return data.get(P_LEVEL) + (cls == null ? 0 : cls.forgeLevelBonus());
	}

	@Override
	public boolean onButtonClick(PlayerEntity clicker, int id) {
		if (clicker.getWorld().isClient) {
			return false;
		}
		if (id >= 0 && id < ForgeMaterial.values().length) {
			select(P_MATERIAL, id);
			return true;
		}
		if (id >= BTN_GEAR_BASE && id < BTN_GEAR_BASE + Gear.values().length) {
			select(P_GEAR, id - BTN_GEAR_BASE);
			return true;
		}
		switch (id) {
			case BTN_START -> start();
			case BTN_STRIKE -> strike();
			case BTN_RESET -> resetToIdle();
			default -> {
				return false;
			}
		}
		return true;
	}

	private void select(int property, int value) {
		int state = data.get(P_STATE);
		if (state == STATE_FORGING) {
			return;
		}
		if (state == STATE_DONE) {
			resetToIdle();
		}
		data.set(property, value);
	}

	private void resetToIdle() {
		if (data.get(P_STATE) == STATE_FORGING) {
			return;
		}
		data.set(P_STATE, STATE_IDLE);
		data.set(P_STRIKES, 0);
		data.set(P_POINTS, 0);
		data.set(P_RESULT, -1);
		data.set(P_LAST, -1);
	}

	private boolean stationHasOutput() {
		boolean[] busy = {false};
		context.run((world, pos) -> {
			if (world.getBlockEntity(pos) instanceof ForgingStationBlockEntity station && station.hasOutput()) {
				busy[0] = true;
			}
		});
		return busy[0];
	}

	private void start() {
		if (data.get(P_STATE) != STATE_IDLE) {
			return;
		}
		ForgeMaterial material = ForgeMaterial.byIndex(data.get(P_MATERIAL));
		Gear gear = Gear.byIndex(data.get(P_GEAR));
		if (!material.supports(gear)) {
			player.sendMessage(Text.translatable("message.forgebound.no_armor"), true);
			return;
		}
		if (stationHasOutput()) {
			player.sendMessage(Text.translatable("message.forgebound.station_busy"), true);
			return;
		}
		if (!ForgingService.consumeMaterials(player, material, gear)) {
			if (gear.isArmor()) {
				player.sendMessage(Text.translatable("message.forgebound.missing_materials_armor",
						material.cost(gear), material.ingredient().getName()), true);
			} else {
				player.sendMessage(Text.translatable("message.forgebound.missing_materials",
						material.cost(gear), material.ingredient().getName(), BalanceConfig.STICKS), true);
			}
			return;
		}
		forgingMaterial = material;
		forgingGear = gear;
		syncProgress();
		long now = player.getWorld().getTime();
		lastStrikeTick = now;
		data.set(P_STATE, STATE_FORGING);
		data.set(P_STRIKES, 0);
		data.set(P_POINTS, 0);
		data.set(P_RESULT, -1);
		data.set(P_LAST, -1);
		data.set(P_PHASE, (int) (now % (BalanceConfig.SWEEP_TICKS * 2)));
		data.set(P_TARGET, randomTarget());
	}

	private int randomTarget() {
		return player.getRandom().nextBetween(20, 80);
	}

	private void strike() {
		if (data.get(P_STATE) != STATE_FORGING) {
			return;
		}
		long now = player.getWorld().getTime();
		if (now - lastStrikeTick < BalanceConfig.MIN_STRIKE_INTERVAL_TICKS) {
			return;
		}
		lastStrikeTick = now;
		double marker = ForgingMath.markerPos(now, data.get(P_PHASE), 0.0F);
		double distance = Math.abs(marker - data.get(P_TARGET));
		int judgement = ForgingMath.judge(distance, effectiveLevel());
		data.set(P_POINTS, data.get(P_POINTS) + judgement);
		data.set(P_STRIKES, data.get(P_STRIKES) + 1);
		data.set(P_LAST, judgement);
		player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_ANVIL_USE,
				SoundCategory.BLOCKS, 0.6F, 0.8F + 0.15F * judgement);
		if (data.get(P_STRIKES) >= BalanceConfig.STRIKES) {
			finish();
		} else {
			data.set(P_TARGET, randomTarget());
		}
	}

	/** Schliesst den Vorgang ab: Seltenheit auswuerfeln, Stueck auf den Amboss legen, Erfahrung vergeben. */
	private void finish() {
		ForgeMaterial material = forgingMaterial != null ? forgingMaterial : ForgeMaterial.byIndex(data.get(P_MATERIAL));
		Gear gear = forgingGear != null ? forgingGear : Gear.byIndex(data.get(P_GEAR));
		double[] chances = ForgingMath.chances(material, effectiveLevel(), data.get(P_POINTS));
		ItemQuality quality = ForgingMath.roll(chances, player.getRandom());
		ItemStack stack = ForgingService.createGear(material, gear, quality);
		deliver(stack, quality);

		MinecraftServer server = player.getServer();
		if (server != null) {
			ForgeClass cls = ClassManager.get(server, player.getUuid());
			double classFactor = cls == null ? 1.0 : cls.forgeXpMultiplier();
			int xp = (int) Math.round(ForgingService.xpReward(material, quality) * classFactor);
			ForgeProgressState state = ForgeProgressState.get(server);
			int gained = state.addXp(player.getUuid(), xp);
			syncProgress();
			if (gained > 0) {
				player.sendMessage(Text.translatable("message.forgebound.level_up", state.level(player.getUuid())), false);
			}
		}
		data.set(P_RESULT, quality.ordinal());
		data.set(P_STATE, STATE_DONE);
		forgingMaterial = null;
		forgingGear = null;
	}

	/** Legt das Stueck auf den Amboss; nur als Notfall (kein Amboss) direkt ins Inventar. */
	private void deliver(ItemStack stack, ItemQuality quality) {
		boolean[] placed = {false};
		context.run((world, pos) -> {
			if (world.getBlockEntity(pos) instanceof ForgingStationBlockEntity station && !station.hasOutput()) {
				station.present(stack, quality, player.getUuid(), world.getTime());
				placed[0] = true;
			}
		});
		if (!placed[0]) {
			player.getInventory().offerOrDrop(stack);
		}
	}

	@Override
	public void onClosed(PlayerEntity closer) {
		super.onClosed(closer);
		// Wer mitten im Vorgang schliesst, verliert die Materialien nicht:
		// verbleibende Schlaege zaehlen als Fehlschlaege.
		if (!closer.getWorld().isClient && data.get(P_STATE) == STATE_FORGING) {
			finish();
		}
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(context, player, ModBlocks.FORGING_STATION);
	}
}
