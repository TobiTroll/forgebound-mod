package dev.forgebound.forging;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.classes.ForgeClass;
import net.minecraft.item.ItemStack;

/**
 * Kosten fuer Reparieren und Verbessern. Reine Rechnung ohne Serverzugriff, damit das
 * Menue auf dem Client dieselben Zahlen anzeigt, die der Server verlangt.
 */
public final class GearCosts {
	private GearCosts() {
	}

	/** Reparatur: Anteil des Schadens mal Herstellungskosten, mindestens 1, mit Klassenrabatt. 0, wenn nichts zu reparieren ist. */
	public static int repairCost(ItemStack stack, GearInfo info, ForgeClass cls) {
		if (!stack.isDamaged() || stack.getMaxDamage() <= 0) {
			return 0;
		}
		double fraction = stack.getDamage() / (double) stack.getMaxDamage();
		int base = (int) Math.ceil(info.material().cost(info.gear()) * fraction);
		return discounted(Math.max(1, base), cls);
	}

	/** Verbesserung auf Stufe nextLevel: halbe Herstellungskosten mal Stufe, mit Klassenrabatt. */
	public static int upgradeCost(GearInfo info, int nextLevel, ForgeClass cls) {
		int base = (int) Math.ceil(info.material().cost(info.gear()) * nextLevel / 2.0);
		return discounted(Math.max(1, base), cls);
	}

	/** Noetiges Schmiedelevel fuer die Verbesserung auf Stufe nextLevel. */
	public static int requiredForgeLevel(int nextLevel) {
		return BalanceConfig.UPGRADE_LEVEL_STEP * nextLevel;
	}

	private static int discounted(int base, ForgeClass cls) {
		double discount = cls == null ? 0.0 : cls.repairDiscount();
		return Math.max(1, (int) Math.ceil(base * (1.0 - discount)));
	}
}
