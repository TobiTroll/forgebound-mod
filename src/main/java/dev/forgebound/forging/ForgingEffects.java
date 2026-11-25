package dev.forgebound.forging;

import dev.forgebound.balance.BalanceConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

/**
 * Serverseitige Enthuellungs-Effekte ueber dem Amboss. Je seltener das Stueck,
 * desto groesser der Auftritt: Normal nur Funken, Legendaer mit Lichtstrahl und
 * Blitz aus dem Himmel, Einzigartig mit hohem Strahl und mehreren Blitzen.
 * Die Blitze sind rein kosmetisch (kein Schaden, kein Feuer).
 */
public final class ForgingEffects {
	private ForgingEffects() {
	}

	/** Strahlfarben je Seltenheit (RGB 0 bis 1). */
	private static final float[][] COLORS = {
			{0.85F, 0.85F, 0.85F},
			{0.30F, 0.90F, 1.00F},
			{0.80F, 0.30F, 1.00F},
			{1.00F, 0.80F, 0.20F},
			{1.00F, 0.25F, 0.20F}
	};
	/** Strahlhoehe in Bloecken je Seltenheit (0 = kein Strahl). */
	private static final int[] BEAM_HEIGHT = {0, 6, 12, 28, 52};

	/** t = Ticks seit Beginn der Enthuellung. */
	public static void tick(ServerWorld world, BlockPos pos, int rarity, long t) {
		double x = pos.getX() + 0.5;
		double y = pos.getY() + 1.0;
		double z = pos.getZ() + 0.5;
		int reveal = BalanceConfig.REVEAL_ITEM_TICKS;

		if (t == 0) {
			world.playSound(null, pos, startSound(rarity), SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
		if (rarity >= 3 && (t == 0 || (rarity == 4 && (t == 12 || t == 24)))) {
			spawnBolt(world, pos);
		}

		if (t < reveal) {
			if (BEAM_HEIGHT[rarity] > 0) {
				float[] c = COLORS[rarity];
				DustParticleEffect dust = new DustParticleEffect(new Vector3f(c[0], c[1], c[2]), 1.6F);
				for (int h = (int) (t % 2); h < BEAM_HEIGHT[rarity]; h += 2) {
					world.spawnParticles(dust, x, y + h, z, 1, 0.06, 0.0, 0.06, 0.0);
				}
				world.spawnParticles(ParticleTypes.END_ROD, x, y + (t % BEAM_HEIGHT[rarity]), z, 1, 0.05, 0.1, 0.05, 0.01);
			} else if (t % 4 == 0) {
				world.spawnParticles(ParticleTypes.CRIT, x, y + 0.3, z, 4, 0.2, 0.1, 0.2, 0.1);
			}
		} else if (t == reveal) {
			world.spawnParticles(ParticleTypes.FIREWORK, x, y + 0.6, z, 12 + 12 * rarity, 0.3, 0.3, 0.3, 0.12);
			world.playSound(null, pos, revealSound(rarity), SoundCategory.BLOCKS, 1.0F, 1.0F);
		} else if (t % 8 == 0) {
			float[] c = COLORS[rarity];
			DustParticleEffect dust = new DustParticleEffect(new Vector3f(c[0], c[1], c[2]), 1.0F);
			world.spawnParticles(dust, x, y + 0.5, z, 3 + rarity, 0.3, 0.2, 0.3, 0.0);
		}
	}

	private static SoundEvent startSound(int rarity) {
		return switch (rarity) {
			case 0 -> SoundEvents.BLOCK_ANVIL_USE;
			case 1 -> SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME;
			case 2 -> SoundEvents.BLOCK_BEACON_ACTIVATE;
			default -> SoundEvents.UI_TOAST_CHALLENGE_COMPLETE;
		};
	}

	private static SoundEvent revealSound(int rarity) {
		return switch (rarity) {
			case 0, 1 -> SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
			case 2 -> SoundEvents.ENTITY_PLAYER_LEVELUP;
			case 3 -> SoundEvents.ENTITY_FIREWORK_ROCKET_LARGE_BLAST;
			default -> SoundEvents.ITEM_TOTEM_USE;
		};
	}

	private static void spawnBolt(ServerWorld world, BlockPos pos) {
		LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
		if (bolt != null) {
			bolt.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(pos.up()));
			bolt.setCosmetic(true);
			world.spawnEntity(bolt);
		}
	}
}
