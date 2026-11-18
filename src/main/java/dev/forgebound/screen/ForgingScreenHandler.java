package dev.forgebound.screen;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.block.ModBlocks;
import dev.forgebound.forging.ForgeMaterial;
import dev.forgebound.forging.ForgeProgressState;
import dev.forgebound.forging.ForgingMath;
import dev.forgebound.forging.ForgingService;
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
 * Server berechnet. Zustand wird per Properties zum Client gespiegelt.
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
	private static final int PROPERTY_COUNT = 10;

	public static final int STATE_IDLE = 0;
	public static final int STATE_FORGING = 1;
	public static final int STATE_DONE = 2;

	// Knopf-IDs: 0 bis 4 waehlen das Material
	public static final int BTN_START = 10;
	public static final int BTN_STRIKE = 11;
	public static final int BTN_RESET = 12;

	private final PropertyDelegate data = new ArrayPropertyDelegate(PROPERTY_COUNT);
	private final ScreenHandlerContext context;
	private final PlayerEntity player;
	private ForgeMaterial forgingMaterial;
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
	}

	@Override
	public boolean onButtonClick(PlayerEntity clicker, int id) {
		if (clicker.getWorld().isClient) {
			return false;
		}
		if (id >= 0 && id < ForgeMaterial.values().length) {
			selectMaterial(id);
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

	private void selectMaterial(int index) {
		int state = data.get(P_STATE);
		if (state == STATE_FORGING) {
			return;
		}
		if (state == STATE_DONE) {
			resetToIdle();
		}
		data.set(P_MATERIAL, index);
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

	private void start() {
		if (data.get(P_STATE) != STATE_IDLE) {
			return;
		}
		ForgeMaterial material = ForgeMaterial.byIndex(data.get(P_MATERIAL));
		if (!ForgingService.consumeMaterials(player, material)) {
			player.sendMessage(Text.translatable("message.forgebound.missing_materials",
					material.amount(), material.ingredient().getName(), BalanceConfig.STICKS), true);
			return;
		}
		forgingMaterial = material;
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
		int judgement = ForgingMath.judge(distance, data.get(P_LEVEL));
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

	/** Schliesst den Vorgang ab: Seltenheit auswuerfeln, Gegenstand geben, Erfahrung vergeben. */
	private void finish() {
		ForgeMaterial material = forgingMaterial != null ? forgingMaterial : ForgeMaterial.byIndex(data.get(P_MATERIAL));
		double[] chances = ForgingMath.chances(material, data.get(P_LEVEL), data.get(P_POINTS));
		ItemQuality quality = ForgingMath.roll(chances, player.getRandom());
		ItemStack stack = ForgingService.createSword(material, quality);
		player.getInventory().offerOrDrop(stack);

		MinecraftServer server = player.getServer();
		if (server != null) {
			ForgeProgressState state = ForgeProgressState.get(server);
			int gained = state.addXp(player.getUuid(), ForgingService.xpReward(material, quality));
			syncProgress();
			if (gained > 0) {
				player.sendMessage(Text.translatable("message.forgebound.level_up", state.level(player.getUuid())), false);
			}
		}
		data.set(P_RESULT, quality.ordinal());
		data.set(P_STATE, STATE_DONE);
		forgingMaterial = null;
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
