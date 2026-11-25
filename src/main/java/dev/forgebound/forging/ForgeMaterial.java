package dev.forgebound.forging;

import dev.forgebound.item.ModArmorMaterials;
import dev.forgebound.item.ModItems;
import dev.forgebound.item.ModToolMaterials;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.function.Supplier;

/**
 * Schmiedbare Materialien. Bessere Materialien erhoehen die Chancen auf
 * hohe Seltenheiten leicht und geben mehr Erfahrung (gegen Grinden mit
 * billigem Material). Zutaten und Ruestungsmaterial sind Supplier, damit
 * eigene Objekte erst nach ihrer Registrierung abgefragt werden.
 * Stein hat keine Ruestung.
 */
public enum ForgeMaterial {
	STONE("stone", () -> Items.COBBLESTONE, 3, ToolMaterials.STONE, null, 0, null, 0.00, 0.6),
	IRON("iron", () -> Items.IRON_INGOT, 3, ToolMaterials.IRON, () -> ArmorMaterials.IRON, 15, new int[]{5, 8, 7, 4}, 0.05, 1.0),
	GOLD("gold", () -> Items.GOLD_INGOT, 3, ToolMaterials.GOLD, () -> ArmorMaterials.GOLD, 7, new int[]{5, 8, 7, 4}, 0.03, 0.9),
	DIAMOND("diamond", () -> Items.DIAMOND, 3, ToolMaterials.DIAMOND, () -> ArmorMaterials.DIAMOND, 33, new int[]{5, 8, 7, 4}, 0.10, 1.4),
	NETHERITE("netherite", () -> Items.NETHERITE_INGOT, 2, ToolMaterials.NETHERITE, () -> ArmorMaterials.NETHERITE, 37, new int[]{2, 4, 3, 2}, 0.15, 2.0),
	AURORIT("aurorit", () -> ModItems.AURORIT_INGOT, 2, ModToolMaterials.AURORIT, () -> ModArmorMaterials.AURORIT, 42, new int[]{2, 4, 3, 2}, 0.22, 3.0);

	private final String id;
	private final Supplier<Item> ingredient;
	private final int amount;
	private final ToolMaterial toolMaterial;
	private final Supplier<RegistryEntry<ArmorMaterial>> armorMaterial;
	private final int armorDurability;
	private final int[] armorCosts;
	private final double qualityBonus;
	private final double xpMultiplier;

	ForgeMaterial(String id, Supplier<Item> ingredient, int amount, ToolMaterial toolMaterial,
				  Supplier<RegistryEntry<ArmorMaterial>> armorMaterial, int armorDurability, int[] armorCosts,
				  double qualityBonus, double xpMultiplier) {
		this.id = id;
		this.ingredient = ingredient;
		this.amount = amount;
		this.toolMaterial = toolMaterial;
		this.armorMaterial = armorMaterial;
		this.armorDurability = armorDurability;
		this.armorCosts = armorCosts;
		this.qualityBonus = qualityBonus;
		this.xpMultiplier = xpMultiplier;
	}

	public String id() {
		return id;
	}

	public Item ingredient() {
		return ingredient.get();
	}

	/** Kosten fuer das Schwert. */
	public int amount() {
		return amount;
	}

	public ToolMaterial toolMaterial() {
		return toolMaterial;
	}

	public boolean supports(Gear gear) {
		return !gear.isArmor() || armorMaterial != null;
	}

	public RegistryEntry<ArmorMaterial> armorMaterial() {
		return armorMaterial == null ? null : armorMaterial.get();
	}

	public int armorDurability() {
		return armorDurability;
	}

	/** Materialkosten fuer eine Ausruestungsart. */
	public int cost(Gear gear) {
		if (!gear.isArmor()) {
			return amount;
		}
		if (armorCosts == null) {
			return 0;
		}
		return armorCosts[gear.ordinal() - 1];
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
