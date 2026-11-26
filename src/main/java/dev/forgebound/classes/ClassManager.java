package dev.forgebound.classes;

import dev.forgebound.ForgeboundMod;
import dev.forgebound.balance.BalanceConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Zugriff auf Klassen: Lesen, Setzen, Attribute anwenden, Wuerfeln und Erweckungs-Effekte. */
public final class ClassManager {
	private ClassManager() {
	}

	/** Zeitpunkt (Weltzeit), ab dem die Phoenix-Wiedergeburt wieder bereit ist. Nur im Speicher. */
	private static final Map<UUID, Long> PHOENIX_READY = new HashMap<>();

	public static ForgeClass get(MinecraftServer server, UUID id) {
		if (server == null) {
			return null;
		}
		return ClassState.get(server).get(id);
	}

	public static ForgeClass get(ServerPlayerEntity player) {
		return get(player.getServer(), player.getUuid());
	}

	/** Setzt die Klasse (null = zuruecksetzen), speichert sie und wendet die Attribute an. */
	public static void set(ServerPlayerEntity player, ForgeClass cls) {
		MinecraftServer server = player.getServer();
		if (server == null) {
			return;
		}
		ClassState.get(server).set(player.getUuid(), cls);
		apply(player);
	}

	private static Identifier modifierId(ForgeClass cls, int index) {
		return ForgeboundMod.id("class_" + cls.id() + "_" + index);
	}

	/** Entfernt alle Klassen-Modifikatoren und setzt die der aktuellen Klasse. Idempotent. */
	public static void apply(ServerPlayerEntity player) {
		for (ForgeClass cls : ForgeClass.values()) {
			List<ForgeClass.Mod> mods = cls.mods();
			for (int i = 0; i < mods.size(); i++) {
				EntityAttributeInstance instance = player.getAttributeInstance(mods.get(i).attribute());
				if (instance != null) {
					instance.removeModifier(modifierId(cls, i));
				}
			}
		}
		ForgeClass current = get(player);
		if (current != null) {
			List<ForgeClass.Mod> mods = current.mods();
			for (int i = 0; i < mods.size(); i++) {
				ForgeClass.Mod mod = mods.get(i);
				EntityAttributeInstance instance = player.getAttributeInstance(mod.attribute());
				if (instance != null) {
					instance.addTemporaryModifier(new EntityAttributeModifier(modifierId(current, i), mod.value(), mod.operation()));
				}
			}
		}
		if (player.getHealth() > player.getMaxHealth()) {
			player.setHealth(player.getMaxHealth());
		}
	}

	/** Wuerfelt eine Klasse: erst die Seltenheitsstufe nach Gewicht, dann gleichverteilt innerhalb der Stufe. Die aktuelle Klasse wird ausgeschlossen. */
	public static ForgeClass roll(Random random, ForgeClass exclude) {
		double[] weights = BalanceConfig.CLASS_WEIGHTS;
		double sum = 0.0;
		for (double w : weights) {
			sum += w;
		}
		double x = random.nextDouble() * sum;
		int tier = weights.length - 1;
		double acc = 0.0;
		for (int i = 0; i < weights.length; i++) {
			acc += weights[i];
			if (x < acc) {
				tier = i;
				break;
			}
		}
		List<ForgeClass> pool = ForgeClass.ofRarity(dev.forgebound.quality.ItemQuality.values()[tier]);
		pool.remove(exclude);
		if (pool.isEmpty()) {
			pool = new java.util.ArrayList<>(List.of(ForgeClass.values()));
			pool.remove(exclude);
		}
		return pool.get(random.nextInt(pool.size()));
	}

	/** Chance in Prozent, diese Klasse beim Erwecken zu bekommen (ohne Ausschluss einer Klasse). */
	public static double chancePercent(ForgeClass cls) {
		double sum = 0.0;
		for (double w : BalanceConfig.CLASS_WEIGHTS) {
			sum += w;
		}
		int tier = cls.rarity().ordinal();
		return BalanceConfig.CLASS_WEIGHTS[tier] / sum * 100.0 / ForgeClass.ofRarity(cls.rarity()).size();
	}

	public static long phoenixReadyAt(UUID id) {
		return PHOENIX_READY.getOrDefault(id, 0L);
	}

	public static void setPhoenixReadyAt(UUID id, long time) {
		PHOENIX_READY.put(id, time);
	}

	/** Erweckungs-Auftritt: Text, Partikel, Ton, bei Legendaer und Einzigartig Blitz und Server-Nachricht. */
	public static void announce(ServerPlayerEntity player, ForgeClass cls) {
		ServerWorld world = player.getServerWorld();
		int rarity = cls.rarity().ordinal();

		world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getBodyY(0.6), player.getZ(),
				20 + 20 * rarity, 0.4, 0.8, 0.4, 0.3);
		world.playSound(null, player.getBlockPos(),
				rarity >= 3 ? SoundEvents.UI_TOAST_CHALLENGE_COMPLETE : SoundEvents.ENTITY_PLAYER_LEVELUP,
				SoundCategory.PLAYERS, 1.0F, 1.0F);

		if (rarity >= 3) {
			LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
			if (bolt != null) {
				bolt.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(player.getBlockPos()));
				bolt.setCosmetic(true);
				world.spawnEntity(bolt);
			}
			MinecraftServer server = player.getServer();
			if (server != null) {
				server.getPlayerManager().broadcast(Text.translatable("message.forgebound.class_broadcast",
						player.getDisplayName(),
						Text.translatable(cls.translationKey()).formatted(cls.rarity().color())), false);
			}
		}

		for (Text line : ClassInfo.describe(cls)) {
			player.sendMessage(line, false);
		}
	}
}
