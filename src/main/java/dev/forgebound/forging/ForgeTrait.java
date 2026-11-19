package dev.forgebound.forging;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

/**
 * Waffen-Traits. Sie werden beim Schmieden nach Seltenheit ausgewuerfelt
 * (Anzahl und Stufe steigen mit der Seltenheit) und wirken bei jedem Treffer.
 * Gespeichert werden sie als Text "id:stufe" in der Data Component
 * forgebound:traits. Wirkung nur serverseitig aufrufen.
 */
public enum ForgeTrait {
	EMBER("ember"),
	LEECH("leech"),
	FROST("frost"),
	DECAY("decay"),
	FORCE("force"),
	SWIFT("swift"),
	WARD("ward");

	public static final int MAX_LEVEL = 5;

	private final String id;

	ForgeTrait(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}

	public String translationKey() {
		return "trait.forgebound." + id;
	}

	/** Ein Trait mit Stufe, wie er auf dem Gegenstand steht. */
	public record Entry(ForgeTrait trait, int level) {
		public String encode() {
			return trait.id + ":" + level;
		}
	}

	public static Entry parse(String raw) {
		int split = raw.indexOf(':');
		if (split < 0) {
			return null;
		}
		ForgeTrait trait = byId(raw.substring(0, split));
		if (trait == null) {
			return null;
		}
		try {
			int level = Integer.parseInt(raw.substring(split + 1));
			return new Entry(trait, Math.max(1, Math.min(MAX_LEVEL, level)));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static ForgeTrait byId(String id) {
		for (ForgeTrait trait : values()) {
			if (trait.id.equals(id)) {
				return trait;
			}
		}
		return null;
	}

	public static String roman(int level) {
		return switch (level) {
			case 1 -> "I";
			case 2 -> "II";
			case 3 -> "III";
			case 4 -> "IV";
			default -> "V";
		};
	}

	/** Wirkung bei einem Treffer. Alle Werte wachsen mit der Stufe 1 bis 5. */
	public void onHit(int level, LivingEntity target, LivingEntity attacker) {
		switch (this) {
			case EMBER -> target.setFireTicks(20 * (1 + level));
			case LEECH -> attacker.heal(0.5F + 0.5F * level);
			case FROST -> target.addStatusEffect(
					new StatusEffectInstance(StatusEffects.SLOWNESS, 40 + 20 * level, level >= 4 ? 1 : 0), attacker);
			case DECAY -> target.addStatusEffect(
					new StatusEffectInstance(StatusEffects.WITHER, 40 + 20 * level, 0), attacker);
			case FORCE -> target.takeKnockback(0.3 + 0.2 * level, attacker.getX() - target.getX(), attacker.getZ() - target.getZ());
			case SWIFT -> attacker.addStatusEffect(
					new StatusEffectInstance(StatusEffects.SPEED, 60, level >= 4 ? 1 : 0));
			case WARD -> attacker.addStatusEffect(
					new StatusEffectInstance(StatusEffects.ABSORPTION, 100, level >= 4 ? 1 : 0));
		}
	}
}
