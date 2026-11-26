package dev.forgebound.classes;

import dev.forgebound.quality.ItemQuality;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Die Klassen. Jede Klasse hat Seltenheit, Attribut-Vor- und Nachteile, optional
 * Schmiedeboni, Ausweichchance und einen besonderen Passiv-Effekt (siehe ClassEvents).
 * Alle Zahlen sind Prototypwerte.
 */
public enum ForgeClass {
	// --- Normal: oft, solide, mit kleinen Schwaechen ---
	WARRIOR("warrior", ItemQuality.COMMON, 0, 1.0, 0.0, true,
			add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0),
			add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0),
			add(EntityAttributes.GENERIC_ARMOR, 1.0),
			mul(EntityAttributes.GENERIC_MOVEMENT_SPEED, -0.06)),
	MINER("miner", ItemQuality.COMMON, 0, 1.0, 0.0, true,
			mul(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED, 0.25),
			add(EntityAttributes.GENERIC_LUCK, 1.0),
			add(EntityAttributes.GENERIC_MAX_HEALTH, -2.0),
			mul(EntityAttributes.GENERIC_ATTACK_DAMAGE, -0.10)),
	SCOUT("scout", ItemQuality.COMMON, 0, 1.0, 0.0, false,
			mul(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.10),
			mul(EntityAttributes.GENERIC_JUMP_STRENGTH, 0.10),
			add(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE, 2.0),
			add(EntityAttributes.GENERIC_MAX_HEALTH, -4.0),
			add(EntityAttributes.GENERIC_ATTACK_DAMAGE, -0.5)),
	SMITH("smith", ItemQuality.COMMON, 3, 1.25, 0.0, false,
			add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.5),
			mulLow(EntityAttributes.GENERIC_BURNING_TIME, -0.5),
			add(EntityAttributes.GENERIC_MAX_HEALTH, -2.0),
			mul(EntityAttributes.GENERIC_MOVEMENT_SPEED, -0.04)),

	// --- Selten: klare Spezialisierung ---
	GUARDIAN("guardian", ItemQuality.RARE, 0, 1.0, 0.0, true,
			add(EntityAttributes.GENERIC_ARMOR, 4.0),
			add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2.0),
			add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.3),
			add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0),
			mul(EntityAttributes.GENERIC_MOVEMENT_SPEED, -0.10),
			mul(EntityAttributes.GENERIC_ATTACK_SPEED, -0.10)),
	ASSASSIN("assassin", ItemQuality.RARE, 0, 1.0, 0.06, true,
			add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.5),
			mul(EntityAttributes.GENERIC_ATTACK_SPEED, 0.15),
			mul(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.08),
			add(EntityAttributes.GENERIC_MAX_HEALTH, -6.0)),
	EXPLORER("explorer", ItemQuality.RARE, 0, 1.0, 0.0, true,
			add(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE, 1.5),
			add(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE, 3.0),
			add(EntityAttributes.GENERIC_STEP_HEIGHT, 0.4),
			add(EntityAttributes.GENERIC_OXYGEN_BONUS, 1.0),
			add(EntityAttributes.GENERIC_ATTACK_DAMAGE, -1.0),
			add(EntityAttributes.GENERIC_MAX_HEALTH, -2.0)),

	// --- Episch: starke Stilrichtung mit deutlichem Preis ---
	BERSERKER("berserker", ItemQuality.EPIC, 0, 1.0, 0.0, true,
			add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0),
			mul(EntityAttributes.GENERIC_ATTACK_SPEED, 0.10),
			mul(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.04),
			add(EntityAttributes.GENERIC_MAX_HEALTH, -6.0)),
	DUELIST("duelist", ItemQuality.EPIC, 0, 1.0, 0.08, false,
			add(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE, 1.0),
			mul(EntityAttributes.GENERIC_ATTACK_SPEED, 0.20),
			mul(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.06),
			add(EntityAttributes.GENERIC_MAX_HEALTH, -4.0),
			add(EntityAttributes.GENERIC_ATTACK_DAMAGE, -0.5)),

	// --- Legendaer: praegen den Spielstil ---
	TITAN("titan", ItemQuality.LEGENDARY, 0, 1.0, 0.0, false,
			add(EntityAttributes.GENERIC_SCALE, 0.25),
			add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0),
			add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5),
			add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2.0),
			add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0),
			mul(EntityAttributes.GENERIC_MOVEMENT_SPEED, -0.12),
			mul(EntityAttributes.GENERIC_ATTACK_SPEED, -0.12)),
	PHOENIX("phoenix", ItemQuality.LEGENDARY, 0, 1.0, 0.0, true,
			mulLow(EntityAttributes.GENERIC_BURNING_TIME, -0.9),
			add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0),
			add(EntityAttributes.GENERIC_MAX_HEALTH, -4.0),
			mul(EntityAttributes.GENERIC_ATTACK_SPEED, -0.05)),

	// --- Einzigartig: extrem selten, mit grossen Staerken und Schwaechen ---
	FORGEKEEPER("forgekeeper", ItemQuality.UNIQUE, 8, 1.6, 0.0, false,
			add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0),
			add(EntityAttributes.GENERIC_ARMOR, 2.0),
			add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0),
			mulLow(EntityAttributes.GENERIC_BURNING_TIME, -0.9),
			mul(EntityAttributes.GENERIC_MOVEMENT_SPEED, -0.10),
			mul(EntityAttributes.GENERIC_ATTACK_SPEED, -0.08)),
	VOIDWALKER("voidwalker", ItemQuality.UNIQUE, 0, 1.0, 0.12, false,
			total(EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER, -1.0),
			mul(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.10),
			mul(EntityAttributes.GENERIC_JUMP_STRENGTH, 0.15),
			add(EntityAttributes.GENERIC_STEP_HEIGHT, 0.5),
			add(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE, 1.0),
			add(EntityAttributes.GENERIC_MAX_HEALTH, -8.0),
			add(EntityAttributes.GENERIC_ATTACK_DAMAGE, -1.0));

	/** Ein Attributmodifikator der Klasse. negativeIsGood: ein negativer Wert ist ein Vorteil (z. B. Brenndauer). */
	public record Mod(RegistryEntry<EntityAttribute> attribute, double value, Operation operation, boolean negativeIsGood) {
		public boolean isBenefit() {
			return (value >= 0.0) != negativeIsGood;
		}
	}

	private static Mod add(RegistryEntry<EntityAttribute> attribute, double value) {
		return new Mod(attribute, value, Operation.ADD_VALUE, false);
	}

	private static Mod mul(RegistryEntry<EntityAttribute> attribute, double value) {
		return new Mod(attribute, value, Operation.ADD_MULTIPLIED_BASE, false);
	}

	private static Mod mulLow(RegistryEntry<EntityAttribute> attribute, double value) {
		return new Mod(attribute, value, Operation.ADD_MULTIPLIED_BASE, true);
	}

	private static Mod total(RegistryEntry<EntityAttribute> attribute, double value) {
		return new Mod(attribute, value, Operation.ADD_MULTIPLIED_TOTAL, false);
	}

	private final String id;
	private final ItemQuality rarity;
	private final int forgeLevelBonus;
	private final double forgeXpMultiplier;
	private final double dodgeChance;
	private final boolean passive;
	private final List<Mod> mods;

	ForgeClass(String id, ItemQuality rarity, int forgeLevelBonus, double forgeXpMultiplier,
			   double dodgeChance, boolean passive, Mod... mods) {
		this.id = id;
		this.rarity = rarity;
		this.forgeLevelBonus = forgeLevelBonus;
		this.forgeXpMultiplier = forgeXpMultiplier;
		this.dodgeChance = dodgeChance;
		this.passive = passive;
		this.mods = List.of(mods);
	}

	public String id() {
		return id;
	}

	public ItemQuality rarity() {
		return rarity;
	}

	/** Zusaetzliche effektive Schmiedelevel (verbreitert die Zielzonen, verbessert die Chancen). */
	public int forgeLevelBonus() {
		return forgeLevelBonus;
	}

	public double forgeXpMultiplier() {
		return forgeXpMultiplier;
	}

	public double dodgeChance() {
		return dodgeChance;
	}

	/** Hat einen besonderen Passiv-Effekt mit eigenem Text (class.forgebound.<id>.passive). */
	public boolean hasPassive() {
		return passive;
	}

	public List<Mod> mods() {
		return mods;
	}

	public String translationKey() {
		return "class.forgebound." + id;
	}

	/** Wert fuer Menue-Properties: 0 = keine Klasse. */
	public int propertyValue() {
		return ordinal() + 1;
	}

	public static ForgeClass fromProperty(int value) {
		ForgeClass[] all = values();
		return value >= 1 && value <= all.length ? all[value - 1] : null;
	}

	public static ForgeClass byId(String id) {
		for (ForgeClass cls : values()) {
			if (cls.id.equals(id)) {
				return cls;
			}
		}
		return null;
	}

	public static List<ForgeClass> ofRarity(ItemQuality rarity) {
		List<ForgeClass> result = new ArrayList<>();
		for (ForgeClass cls : values()) {
			if (cls.rarity == rarity) {
				result.add(cls);
			}
		}
		return result;
	}

	public static List<String> ids() {
		return Arrays.stream(values()).map(ForgeClass::id).toList();
	}
}
