package dev.forgebound.forging;

import net.minecraft.item.ArmorItem;

/** Schmiedbare Ausruestungsarten: Schwert und die vier Ruestungsteile. */
public enum Gear {
	SWORD("sword", null),
	HELMET("helmet", ArmorItem.Type.HELMET),
	CHESTPLATE("chestplate", ArmorItem.Type.CHESTPLATE),
	LEGGINGS("leggings", ArmorItem.Type.LEGGINGS),
	BOOTS("boots", ArmorItem.Type.BOOTS);

	private final String id;
	private final ArmorItem.Type armorType;

	Gear(String id, ArmorItem.Type armorType) {
		this.id = id;
		this.armorType = armorType;
	}

	public String id() {
		return id;
	}

	public ArmorItem.Type armorType() {
		return armorType;
	}

	public boolean isArmor() {
		return armorType != null;
	}

	public String translationKey() {
		return "forgebound.gear." + id;
	}

	public static Gear byIndex(int index) {
		Gear[] all = values();
		if (index < 0 || index >= all.length) {
			return SWORD;
		}
		return all[index];
	}
}
