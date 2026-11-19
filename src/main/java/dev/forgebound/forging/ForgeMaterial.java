package dev.forgebound.forging;

import dev.forgebound.item.ModItems;
import dev.forgebound.item.ModToolMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;

import java.util.function.Supplier;

/**
 * Schmiedbare Materialien. Bessere Materialien erhoehen die Chancen auf
 * hohe Seltenheiten leicht und geben mehr Erfahrung (gegen Grinden mit
 * billigem Material). Die Zutat ist ein Supplier, damit eigene Items erst
 * nach ihrer Registrierung abgefragt werden.
 */
public enum ForgeMaterial {
	STONE("stone", () -> Items.COBBLESTONE, 3, ToolMaterials.STONE, 0.00, 0.6),
	IRON("iron", () -> Items.IRON_INGOT, 3, ToolMaterials.IRON, 0.05, 1.0),
	GOLD("gold", () -> Items.GOLD_INGOT, 3, ToolMaterials.GOLD, 0.03, 0.9),
	DIAMOND("diamond", () -> Items.DIAMOND, 3, ToolMaterials.DIAMOND, 0.10, 1.4),
	NETHERITE("netherite", () -> Items.NETHERITE_INGOT, 2, ToolMaterials.NETHERITE, 0.15, 2.0),
	AURORIT("aurorit", () -> ModItems.AURORIT_INGOT, 2, ModToolMaterials.AURORIT, 0.22, 3.0);

	private final String id;
	private final Supplier<Item> ingredient;
	private final int amount;
	private final ToolMaterial toolMaterial;
	private final double qualityBonus;
	private final double xpMultiplier;

	ForgeMaterial(String id, Supplier<Item> ingredient, int amount, ToolMaterial toolMaterial, double qualityBonus, double xpMultiplier) {
		this.id = id;
		this.ingredient = ingredient;
		this.amount = amount;
		this.toolMaterial = toolMaterial;
		this.qualityBonus = qualityBonus;
		this.xpMultiplier = xpMultiplier;
	}

	public String id() {
		return id;
	}

	public Item ingredient() {
		return ingredient.get();
	}

	public int amount() {
		return amount;
	}

	public ToolMaterial toolMaterial() {
		return toolMaterial;
	}

	public double qualityBonus() {
		return qualityBonus;
	}

	public double xpMultiplier() {
		return xpMultiplier;
	}

	public String translationKey() {
		return "forgebound.material." + id;
	}

	public static ForgeMaterial byIndex(int index) {
		ForgeMaterial[] all = values();
		if (index < 0 || index >= all.length) {
			return IRON;
		}
		return all[index];
	}
}
