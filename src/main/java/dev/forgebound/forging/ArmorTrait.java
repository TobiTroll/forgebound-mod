package dev.forgebound.forging;

import dev.forgebound.ForgeboundMod;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;

/**
 * Ruestungs-Traits: dauerhafte Attributboni, die beim Schmieden nach Seltenheit
 * ausgewuerfelt werden (Anzahl und Stufe wie bei Waffen-Traits). Gespeichert als
 * Text "id:stufe" in forgebound:traits und als Attributmodifikator am Gegenstand.
 */
public enum ArmorTrait {
	VITALITY("vitality"),
	SWIFTNESS("swiftness"),
	BULWARK("bulwark"),
	TENACITY("tenacity"),
	MIGHT("might");

	public static final int MAX_LEVEL = 5;

	private final String id;

	ArmorTrait(String id) {
		this.id = id;
	}

	public String id() {
		return id;
	}

	public String translationKey() {
		return "trait.forgebound." + id;
	}

	public record Entry(ArmorTrait trait, int level) {
		public String encode() {
			return trait.id + ":" + level;
		}
	}

	public static Entry parse(String raw) {
		int split = raw.indexOf(':');
		if (split < 0) {
			return null;
		}
		ArmorTrait trait = byId(raw.substring(0, split));
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

	public static ArmorTrait byId(String id) {
		for (ArmorTrait trait : values()) {
			if (trait.id.equals(id)) {
				return trait;
			}
		}
		return null;
	}

	/** Haengt den Attributmodifikator dieses Traits an. slotName macht die ID pro Ruestungsteil eindeutig. */
	public void addModifier(AttributeModifiersComponent.Builder builder, AttributeModifierSlot slot, String slotName, int level) {
		Identifier modifierId = ForgeboundMod.id("trait_" + id + "_" + slotName);
		switch (this) {
			case VITALITY -> builder.add(EntityAttributes.GENERIC_MAX_HEALTH,
					new EntityAttributeModifier(modifierId, 0.5 * level, EntityAttributeModifier.Operation.ADD_VALUE), slot);
			case SWIFTNESS -> builder.add(EntityAttributes.GENERIC_MOVEMENT_SPEED,
					new EntityAttributeModifier(modifierId, 0.01 * level, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE), slot);
			case BULWARK -> builder.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
					new EntityAttributeModifier(modifierId, 0.02 * level, EntityAttributeModifier.Operation.ADD_VALUE), slot);
			case TENACITY -> builder.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
					new EntityAttributeModifier(modifierId, 0.25 * level, EntityAttributeModifier.Operation.ADD_VALUE), slot);
			case MIGHT -> builder.add(EntityAttributes.GENERIC_ATTACK_DAMAGE,
					new EntityAttributeModifier(modifierId, 0.25 * level, EntityAttributeModifier.Operation.ADD_VALUE), slot);
		}
	}
}
