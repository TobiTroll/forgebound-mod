package dev.forgebound.forging;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Benannte Relikt-Waffen: eigene Namen, eine kleine Geschichte und eine besondere Faehigkeit.
 * Alle Namen, Geschichten und Faehigkeiten sind eigene Erfindungen. Texte stehen in den
 * Sprachdateien (relic.forgebound.<id>, .lore.1 bis .lore.3, .ability). Die Faehigkeit wirkt
 * bei jedem Treffer und nur serverseitig.
 */
public enum Relic {
	ASHFEATHER("ashfeather", Formatting.GOLD),
	THUNDERTOOTH("thundertooth", Formatting.YELLOW),
	MISTCLEAVER("mistcleaver", Formatting.DARK_AQUA),
	SOULTHIRST("soulthirst", Formatting.DARK_PURPLE),
	FROSTTONGUE("frosttongue", Formatting.AQUA),
	KINGSFALL("kingsfall", Formatting.RED),
	GALECUTTER("galecutter", Formatting.GREEN);

	private static final Map<UUID, Integer> MIST_HITS = new HashMap<>();

	private final String id;
	private final Formatting color;

	Relic(String id, Formatting color) {
		this.id = id;
		this.color = color;
	}

	public String id() {
		return id;
	}

	public Formatting color() {
		return color;
	}

	public String translationKey() {
		return "relic.forgebound." + id;
	}

	public static Relic byId(String id) {
		if (id == null) {
			return null;
		}
		for (Relic relic : values()) {
			if (relic.id.equals(id)) {
				return relic;
			}
		}
		return null;
	}

	/** Wirkung bei einem Treffer. */
	public void onHit(LivingEntity target, LivingEntity attacker) {
		if (!(target.getWorld() instanceof ServerWorld world)) {
			return;
		}
		Random random = attacker.getRandom();
		switch (this) {
			case ASHFEATHER -> {
				if (random.nextFloat() < 0.30F) {
					world.spawnParticles(ParticleTypes.FLAME, target.getX(), target.getBodyY(0.5), target.getZ(), 25, 0.6, 0.6, 0.6, 0.05);
					world.playSound(null, target.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
					for (LivingEntity near : world.getEntitiesByClass(LivingEntity.class, target.getBoundingBox().expand(3.0),
							entity -> entity != attacker && entity.isAlive())) {
						near.setFireTicks(80);
					}
				}
			}
			case THUNDERTOOTH -> {
				if (random.nextFloat() < 0.15F) {
					LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
					if (bolt != null) {
						bolt.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(target.getBlockPos()));
						bolt.setCosmetic(true);
						world.spawnEntity(bolt);
					}
					extraDamage(world, attacker, target, 6.0F);
				}
			}
			case MISTCLEAVER -> {
				int hits = MIST_HITS.merge(attacker.getUuid(), 1, Integer::sum);
				if (hits >= 5) {
					MIST_HITS.put(attacker.getUuid(), 0);
					world.spawnParticles(ParticleTypes.SMOKE, target.getX(), target.getBodyY(0.5), target.getZ(), 30, 0.4, 0.6, 0.4, 0.05);
					target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0), attacker);
					extraDamage(world, attacker, target, 5.0F);
				}
			}
			case SOULTHIRST -> attacker.heal(1.5F);
			case FROSTTONGUE -> {
				target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 1), attacker);
				target.setFrozenTicks(160);
				world.spawnParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getBodyY(0.5), target.getZ(), 15, 0.4, 0.6, 0.4, 0.03);
			}
			case KINGSFALL -> extraDamage(world, attacker, target, (float) Math.min(10.0, target.getMaxHealth() * 0.06));
			case GALECUTTER -> {
				attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 1));
				if (random.nextFloat() < 0.15F) {
					target.addVelocity(0.0, 0.6, 0.0);
					target.velocityModified = true;
				}
			}
		}
	}

	/** Zusatzschaden direkt nach dem Treffer; setzt die Schutzzeit des Ziels kurz zurueck, damit er ankommt. */
	private static void extraDamage(ServerWorld world, LivingEntity attacker, LivingEntity target, float amount) {
		if (amount <= 0.0F) {
			return;
		}
		target.timeUntilRegen = 0;
		target.damage(world.getDamageSources().indirectMagic(attacker, attacker), amount);
	}
}
