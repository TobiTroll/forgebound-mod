package dev.forgebound.character;

import dev.forgebound.ForgeboundMod;
import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.classes.ClassManager;
import dev.forgebound.classes.ForgeClass;
import dev.forgebound.forging.ForgeProgressState;
import dev.forgebound.network.ModPayloads.StatsSyncPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Charakter-Level, Stats (Staerke, Durability, Speed, Mana) und Mana-Pool.
 * Alles wird serverseitig entschieden; der Client bekommt nur Anzeigedaten.
 * Mana ist fuer spaetere Skills vorbereitet (spendMana).
 */
public final class CharacterManager {
	private CharacterManager() {
	}

	public static final int STR = 0;
	public static final int DUR = 1;
	public static final int SPD = 2;
	public static final int MANA = 3;

	private static final Identifier ID_STR = ForgeboundMod.id("stat_str");
	private static final Identifier ID_DUR_HEALTH = ForgeboundMod.id("stat_dur_health");
	private static final Identifier ID_DUR_ARMOR = ForgeboundMod.id("stat_dur_armor");
	private static final Identifier ID_SPD_MOVE = ForgeboundMod.id("stat_spd_move");
	private static final Identifier ID_SPD_ATTACK = ForgeboundMod.id("stat_spd_attack");

	public static CharacterState.Data data(ServerPlayerEntity player) {
		return CharacterState.get(player.getServer()).data(player.getUuid());
	}

	public static void markDirty(ServerPlayerEntity player) {
		CharacterState.get(player.getServer()).markDirty();
	}

	public static int maxMana(CharacterState.Data data) {
		return BalanceConfig.BASE_MANA + BalanceConfig.MANA_PER_POINT * data.mana;
	}

	public static double manaRegen(CharacterState.Data data) {
		return BalanceConfig.BASE_MANA_REGEN + BalanceConfig.MANA_REGEN_PER_POINT * data.mana;
	}

	public static int statValue(CharacterState.Data data, int stat) {
		return switch (stat) {
			case STR -> data.str;
			case DUR -> data.dur;
			case SPD -> data.spd;
			default -> data.mana;
		};
	}

	private static void setStat(CharacterState.Data data, int stat, int value) {
		int clamped = Math.max(0, Math.min(BalanceConfig.STAT_CAP, value));
		switch (stat) {
			case STR -> data.str = clamped;
			case DUR -> data.dur = clamped;
			case SPD -> data.spd = clamped;
			default -> data.mana = clamped;
		}
	}

	/** Wendet die Stat-Modifikatoren an (idempotent). */
	public static void apply(ServerPlayerEntity player) {
		CharacterState.Data data = data(player);
		setModifier(player, EntityAttributes.GENERIC_ATTACK_DAMAGE, ID_STR,
				BalanceConfig.STR_DAMAGE_PER_POINT * data.str, EntityAttributeModifier.Operation.ADD_VALUE);
		setModifier(player, EntityAttributes.GENERIC_MAX_HEALTH, ID_DUR_HEALTH,
				BalanceConfig.DUR_HEALTH_PER_POINT * data.dur, EntityAttributeModifier.Operation.ADD_VALUE);
		setModifier(player, EntityAttributes.GENERIC_ARMOR, ID_DUR_ARMOR,
				BalanceConfig.DUR_ARMOR_PER_POINT * data.dur, EntityAttributeModifier.Operation.ADD_VALUE);
		setModifier(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, ID_SPD_MOVE,
				BalanceConfig.SPD_SPEED_PER_POINT * data.spd, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		setModifier(player, EntityAttributes.GENERIC_ATTACK_SPEED, ID_SPD_ATTACK,
				BalanceConfig.SPD_SPEED_PER_POINT * data.spd, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		if (player.getHealth() > player.getMaxHealth()) {
			player.setHealth(player.getMaxHealth());
		}
		data.curMana = Math.min(data.curMana, maxMana(data));
	}

	private static void setModifier(ServerPlayerEntity player, RegistryEntry<EntityAttribute> attribute, Identifier id,
								   double value, EntityAttributeModifier.Operation operation) {
		EntityAttributeInstance instance = player.getAttributeInstance(attribute);
		if (instance == null) {
			return;
		}
		instance.removeModifier(id);
		if (value != 0.0) {
			instance.addTemporaryModifier(new EntityAttributeModifier(id, value, operation));
		}
	}

	/** Fuegt Charakter-Erfahrung hinzu, loest Level-Ups aus und synchronisiert. */
	public static void addXp(ServerPlayerEntity player, int amount) {
		if (amount <= 0) {
			return;
		}
		CharacterState.Data data = data(player);
		int gained = 0;
		data.xp += amount;
		while (data.level < BalanceConfig.CHAR_MAX_LEVEL && data.xp >= BalanceConfig.charXpForLevel(data.level)) {
			data.xp -= BalanceConfig.charXpForLevel(data.level);
			data.level++;
			data.points += BalanceConfig.POINTS_PER_LEVEL;
			gained++;
		}
		if (data.level >= BalanceConfig.CHAR_MAX_LEVEL) {
			data.xp = 0;
		}
		markDirty(player);
		if (gained > 0) {
			ServerWorld world = player.getServerWorld();
			world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getBodyY(0.6), player.getZ(), 20, 0.5, 0.8, 0.5, 0.1);
			world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
			player.sendMessage(Text.translatable("message.forgebound.char_level_up", data.level, gained * BalanceConfig.POINTS_PER_LEVEL), false);
		}
		sync(player);
	}

	/** Gibt Stat-Punkte aus. Gibt true zurueck, wenn etwas vergeben wurde. */
	public static boolean allocate(ServerPlayerEntity player, int stat, int amount) {
		if (stat < STR || stat > MANA || amount <= 0) {
			return false;
		}
		CharacterState.Data data = data(player);
		int n = Math.min(amount, data.points);
		n = Math.min(n, BalanceConfig.STAT_CAP - statValue(data, stat));
		if (n <= 0) {
			return false;
		}
		setStat(data, stat, statValue(data, stat) + n);
		data.points -= n;
		markDirty(player);
		apply(player);
		sync(player);
		return true;
	}

	/** Setzt das Charakter-Level (Operator-Menue). Punkte werden passend neu berechnet. */
	public static void setLevel(ServerPlayerEntity player, int level) {
		CharacterState.Data data = data(player);
		int clamped = Math.max(1, Math.min(BalanceConfig.CHAR_MAX_LEVEL, level));
		int total = (clamped - 1) * BalanceConfig.POINTS_PER_LEVEL;
		if (data.allocated() > total) {
			data.str = 0;
			data.dur = 0;
			data.spd = 0;
			data.mana = 0;
		}
		data.level = clamped;
		data.xp = 0;
		data.points = total - data.allocated();
	}

	/** Gibt alle verteilten Stat-Punkte zurueck. */
	public static void resetStats(ServerPlayerEntity player) {
		CharacterState.Data data = data(player);
		data.points += data.allocated();
		data.str = 0;
		data.dur = 0;
		data.spd = 0;
		data.mana = 0;
	}

	/** Setzt einen Stat direkt (Operator-Menue), ohne Punkte zu verbrauchen. */
	public static void adminStat(ServerPlayerEntity player, int stat, int delta) {
		if (stat < STR || stat > MANA) {
			return;
		}
		CharacterState.Data data = data(player);
		setStat(data, stat, statValue(data, stat) + delta);
	}

	/** Einmal pro Sekunde: Mana regenerieren. */
	public static void tickMana(ServerPlayerEntity player) {
		CharacterState.Data data = data(player);
		int max = maxMana(data);
		if (data.curMana < max) {
			data.curMana = Math.min(max, data.curMana + manaRegen(data));
			markDirty(player);
		}
	}

	/** Fuer spaetere Skills: zieht Mana ab, wenn genug da ist. */
	public static boolean spendMana(ServerPlayerEntity player, int cost) {
		CharacterState.Data data = data(player);
		if (data.curMana < cost) {
			return false;
		}
		data.curMana -= cost;
		markDirty(player);
		sync(player);
		return true;
	}

	/** Schickt dem Spieler alle Anzeigedaten fuer HUD und Menues. */
	public static void sync(ServerPlayerEntity player) {
		MinecraftServer server = player.getServer();
		if (server == null) {
			return;
		}
		CharacterState.Data data = data(player);
		ForgeProgressState forge = ForgeProgressState.get(server);
		ForgeClass cls = ClassManager.get(player);
		int[] values = new int[StatsSyncPayload.COUNT];
		values[StatsSyncPayload.LEVEL] = data.level;
		values[StatsSyncPayload.XP] = data.xp;
		values[StatsSyncPayload.XP_NEEDED] = data.level >= BalanceConfig.CHAR_MAX_LEVEL ? 0 : BalanceConfig.charXpForLevel(data.level);
		values[StatsSyncPayload.POINTS] = data.points;
		values[StatsSyncPayload.STR] = data.str;
		values[StatsSyncPayload.DUR] = data.dur;
		values[StatsSyncPayload.SPD] = data.spd;
		values[StatsSyncPayload.MANA_STAT] = data.mana;
		values[StatsSyncPayload.MANA] = (int) Math.floor(data.curMana);
		values[StatsSyncPayload.MAX_MANA] = maxMana(data);
		values[StatsSyncPayload.FORGE_LEVEL] = forge.level(player.getUuid());
		values[StatsSyncPayload.FORGE_XP] = forge.xp(player.getUuid());
		values[StatsSyncPayload.CLASS] = cls == null ? 0 : cls.propertyValue();
		values[StatsSyncPayload.ADMIN] = player.hasPermissionLevel(2) ? 1 : 0;
		ServerPlayNetworking.send(player, new StatsSyncPayload(values));
	}
}
