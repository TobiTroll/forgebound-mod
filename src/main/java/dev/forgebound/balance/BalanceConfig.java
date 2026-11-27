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
	/** Zusaetzlicher Grundschaden pro Seltenheit (Waffen). */
	public static final int[] DAMAGE_BONUS = {0, 1, 2, 3, 4};
	/** Zusaetzliche Ruestungspunkte pro Seltenheit und Ruestungsteil. */
	public static final double[] ARMOR_BONUS = {0.0, 0.5, 1.0, 1.5, 2.0};

	// --- Traits ---
	/** Anzahl der Traits und ihre Stufe (1 bis 5) pro Seltenheit. */
	public static final int[] TRAIT_COUNT = {1, 1, 2, 3, 4};
	public static final int[] TRAIT_LEVEL = {1, 2, 3, 4, 5};

	// --- Benannte Relikt-Waffen ---
	/** Chance, dass ein geschmiedetes Schwert dieser Seltenheit ein benanntes Relikt wird. */
	public static final double[] RELIC_CHANCE = {0.0, 0.0, 0.0, 0.20, 1.0};

	// --- Reparieren und Verbessern ---
	/** Maximale Verbesserungsstufe und Schmiedelevel pro Stufe (Stufe n braucht Schmiedelevel n * Schritt). */
	public static final int UPGRADE_MAX = 3;
	public static final int UPGRADE_LEVEL_STEP = 4;
	/** Rabatt auf Reparaturen und Verbesserungen durch Schmiede-Klassen. */
	public static final double SMITH_DISCOUNT = 0.25;
	public static final double MASTER_DISCOUNT = 0.50;

	// --- Enthuellung auf dem Amboss ---
	/** Ticks, bis das fertige Stueck ueber dem Amboss erscheint (Lichtstrahl-Phase). */
	public static final int REVEAL_ITEM_TICKS = 40;
	/** So lange darf nur der Schmied das Stueck einsammeln (danach jeder). */
	public static final int OWNER_ONLY_TICKS = 1200;

	// --- Schmiede-Fortschritt ---
	public static final int MAX_LEVEL = 20;
	public static final double[] RARITY_XP = {10.0, 18.0, 30.0, 50.0, 80.0};

	public static int xpForLevel(int level) {
		return 40 + 20 * (level - 1);
	}

	// --- Klassen ---
	/** Wahrscheinlichkeit je Seltenheitsstufe beim Erwecken (Normal bis Einzigartig). Innerhalb einer Stufe gleichverteilt. */
	public static final double[] CLASS_WEIGHTS = {46.0, 28.0, 16.0, 7.0, 3.0};
	/** So lange gilt die Bestaetigung zum Neuwuerfeln mit dem Erweckungsstein. */
	public static final int AWAKEN_CONFIRM_TICKS = 200;
	/** Bergmann: Chance auf doppelte Erz-Drops. */
	public static final double MINER_DOUBLE_DROP_CHANCE = 0.15;
	/** Phoenix: Abklingzeit der Wiedergeburt (10 Minuten). */
	public static final int PHOENIX_COOLDOWN_TICKS = 12000;

	// --- Charakter-Level und Stats ---
	public static final int CHAR_MAX_LEVEL = 50;
	/** Stat-Punkte pro Charakter-Level. */
	public static final int POINTS_PER_LEVEL = 2;
	/** Anzahl der Stats: Staerke, Durability, Speed, Mana, Geschick, Widerstand, Regeneration, Glueck. */
	public static final int STAT_COUNT = 8;
	/** Obergrenze pro Stat. */
	public static final int STAT_CAP = 100;

	public static int charXpForLevel(int level) {
		return 80 + 40 * (level - 1);
	}

	/** Staerke: Angriffsschaden pro Punkt. */
	public static final double STR_DAMAGE_PER_POINT = 0.05;
	/** Durability: maximales Leben und Ruestung pro Punkt. */
	public static final double DUR_HEALTH_PER_POINT = 0.3;
	public static final double DUR_ARMOR_PER_POINT = 0.05;
	/** Speed: Lauftempo pro Punkt (Anteil, 0.003 = 0,3 %). */
	public static final double SPD_SPEED_PER_POINT = 0.003;
	/** Geschick: Angriffstempo pro Punkt. */
	public static final double DEX_ATTACK_SPEED_PER_POINT = 0.003;
	/** Widerstand: Rueckstossresistenz pro Punkt. */
	public static final double RES_KNOCKBACK_PER_POINT = 0.005;
	/** Regeneration: Leben pro Sekunde pro Punkt. */
	public static final double REG_HEAL_PER_POINT = 0.01;
	/** Glueck: Glueck-Attribut pro Punkt (wirkt auf Beute-Tabellen). */
	public static final double LUCK_PER_POINT = 0.1;

	// --- Mana (fuer spaetere Skills) ---
	public static final int BASE_MANA = 30;
	public static final int MANA_PER_POINT = 5;
	/** Mana-Regeneration pro Sekunde. */
	public static final double BASE_MANA_REGEN = 1.0;
	public static final double MANA_REGEN_PER_POINT = 0.05;

	// --- Charakter-Erfahrung ---
	/** Kill-Erfahrung: Anteil des maximalen Lebens eines feindlichen Mobs, begrenzt. */
	public static final double KILL_XP_FACTOR = 0.5;
	public static final int KILL_XP_CAP = 30;
	public static final int BOSS_XP = 500;
	/** Anteil der Schmiede-Erfahrung (nach Seltenheit), der auch dem Charakter zugutekommt. */
	public static final double FORGE_TO_CHAR_XP = 0.5;

	// --- Material ---
	public static final int STICKS = 1;
}
