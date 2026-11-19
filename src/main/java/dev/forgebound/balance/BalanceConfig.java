package dev.forgebound.balance;

/**
 * Zentrale Prototypwerte. Alle Zahlen zum Balancing stehen hier.
 * Die Reihenfolge der Arrays entspricht der Reihenfolge in ItemQuality:
 * COMMON, RARE, EPIC, LEGENDARY, UNIQUE.
 */
public final class BalanceConfig {
	private BalanceConfig() {
	}

	// --- Minigame ---
	/** Anzahl der Hammerschlaege pro Schmiedevorgang. */
	public static final int STRIKES = 5;
	/** Punkte pro Schlag: Daneben 0, Okay 1, Gut 2, Perfekt 3. */
	public static final int MAX_POINTS = STRIKES * 3;
	/** Ticks, die der Marker fuer eine Strecke von links nach rechts braucht. */
	public static final int SWEEP_TICKS = 45;
	/** Mindestabstand zwischen zwei Schlaegen (gegen Dauerklicken). */
	public static final int MIN_STRIKE_INTERVAL_TICKS = 5;
	/** Zonenradien in Prozent der Leiste (Perfekt, Gut, Okay). */
	public static final double PERFECT_RADIUS = 7.0;
	public static final double GOOD_RADIUS = 16.0;
	public static final double OK_RADIUS = 30.0;
	/** Pro Schmiedelevel werden die Zonen um diesen Anteil breiter. */
	public static final double LEVEL_ZONE_BONUS = 0.03;

	// --- Seltenheit ---
	public static final double[] BASE_WEIGHTS = {60.0, 25.0, 10.0, 4.0, 1.0};
	/** Einfluss der Minigame-Punkte, des Levels und des Materials auf die Chancen. */
	public static final double SCORE_POWER = 0.8;
	public static final double LEVEL_POWER = 0.03;
	public static final double GROWTH = 1.0;
	/** Zusaetzlicher Grundschaden pro Seltenheit. */
	public static final int[] DAMAGE_BONUS = {0, 1, 2, 3, 4};

	// --- Traits ---
	/** Anzahl der Traits und ihre Stufe (1 bis 5) pro Seltenheit. */
	public static final int[] TRAIT_COUNT = {1, 1, 2, 3, 4};
	public static final int[] TRAIT_LEVEL = {1, 2, 3, 4, 5};

	// --- Fortschritt ---
	public static final int MAX_LEVEL = 20;
	public static final double[] RARITY_XP = {10.0, 18.0, 30.0, 50.0, 80.0};

	public static int xpForLevel(int level) {
		return 40 + 20 * (level - 1);
	}

	// --- Material ---
	public static final int STICKS = 1;
}
