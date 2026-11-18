package dev.forgebound.forging;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.quality.ItemQuality;
import net.minecraft.util.math.random.Random;

/**
 * Reine Rechenlogik fuer Minigame und Seltenheit. Wird von Server (verbindlich)
 * und Client (nur Anzeige) mit denselben Formeln benutzt.
 */
public final class ForgingMath {
	private ForgingMath() {
	}

	/** Position des Markers in Prozent (0 bis 100). */
	public static double markerPos(long worldTime, int startPhase, float partial) {
		int period = BalanceConfig.SWEEP_TICKS * 2;
		int diff = (int) (worldTime % period) - startPhase;
		double t = ((diff % period) + period) % period + partial;
		double phase = (t % period) / BalanceConfig.SWEEP_TICKS;
		double pos = phase <= 1.0 ? phase : 2.0 - phase;
		return pos * 100.0;
	}

	/** Radius der Zone fuer eine Wertung (3 Perfekt, 2 Gut, 1 Okay) in Prozent. */
	public static double radius(int judgement, int level) {
		double scale = 1.0 + BalanceConfig.LEVEL_ZONE_BONUS * (level - 1);
		return switch (judgement) {
			case 3 -> BalanceConfig.PERFECT_RADIUS * scale;
			case 2 -> BalanceConfig.GOOD_RADIUS * scale;
			default -> BalanceConfig.OK_RADIUS * scale;
		};
	}

	/** Wertung 0 (Daneben) bis 3 (Perfekt) anhand des Abstands zur Zielmitte. */
	public static int judge(double distance, int level) {
		for (int judgement = 3; judgement >= 1; judgement--) {
			if (distance <= radius(judgement, level)) {
				return judgement;
			}
		}
		return 0;
	}

	/** Chancen je Seltenheit (Summe 1.0) fuer Material, Level und aktuelle Punkte. */
	public static double[] chances(ForgeMaterial material, int level, int points) {
		double ratio = points / (double) BalanceConfig.MAX_POINTS;
		double q = BalanceConfig.SCORE_POWER * ratio
				+ BalanceConfig.LEVEL_POWER * (level - 1)
				+ material.qualityBonus();
		int count = ItemQuality.values().length;
		double[] weights = new double[count];
		double sum = 0.0;
		for (int i = 0; i < count; i++) {
			weights[i] = BalanceConfig.BASE_WEIGHTS[i] * Math.pow(1.0 + BalanceConfig.GROWTH * q, i);
			sum += weights[i];
		}
		for (int i = 0; i < count; i++) {
			weights[i] /= sum;
		}
		return weights;
	}

	public static ItemQuality roll(double[] chances, Random random) {
		double x = random.nextDouble();
		double acc = 0.0;
		ItemQuality[] all = ItemQuality.values();
		for (int i = 0; i < all.length; i++) {
			acc += chances[i];
			if (x < acc) {
				return all[i];
			}
		}
		return all[0];
	}
}
