package dev.forgebound.forging;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.item.ModComponents;
import dev.forgebound.item.ModItems;
import dev.forgebound.quality.ItemQuality;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Serverseitige Hilfen: Materialverbrauch, Ergebnis-Gegenstand, Traits, Erfahrung. */
public final class ForgingService {
	private ForgingService() {
	}

	/** Prueft und verbraucht die Materialien. Im Kreativmodus kostenlos. */
	public static boolean consumeMaterials(PlayerEntity player, ForgeMaterial material) {
		if (player.isCreative()) {
			return true;
		}
		PlayerInventory inventory = player.getInventory();
		if (count(inventory, material.ingredient()) < material.amount() || count(inventory, Items.STICK) < BalanceConfig.STICKS) {
			return false;
		}
		remove(inventory, material.ingredient(), material.amount());
		remove(inventory, Items.STICK, BalanceConfig.STICKS);
		return true;
	}

	public static ItemStack createSword(ForgeMaterial material, ItemQuality quality) {
		ItemStack stack = new ItemStack(ModItems.sword(material));
		stack.set(ModComponents.QUALITY, quality);
		stack.set(ModComponents.TRAITS, rollTraits(quality, Random.create()));
		stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS,
				SwordItem.createAttributeModifiers(material.toolMaterial(), 3 + quality.damageBonus(), -2.4F));
		return stack;
	}

	/** Wuerfelt verschiedene Traits aus. Anzahl und Stufe haengen von der Seltenheit ab. */
	public static List<String> rollTraits(ItemQuality quality, Random random) {
		List<ForgeTrait> pool = new ArrayList<>(Arrays.asList(ForgeTrait.values()));
		Collections.shuffle(pool, new java.util.Random(random.nextLong()));
		int count = Math.min(BalanceConfig.TRAIT_COUNT[quality.ordinal()], pool.size());
		int level = BalanceConfig.TRAIT_LEVEL[quality.ordinal()];
		List<String> result = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			result.add(new ForgeTrait.Entry(pool.get(i), level).encode());
		}
		return result;
	}

	public static int xpReward(ForgeMaterial material, ItemQuality quality) {
		return (int) Math.round(BalanceConfig.RARITY_XP[quality.ordinal()] * material.xpMultiplier());
	}

	private static int count(PlayerInventory inventory, Item item) {
		int total = 0;
		for (int slot = 0; slot < inventory.size(); slot++) {
			ItemStack stack = inventory.getStack(slot);
			if (stack.isOf(item)) {
				total += stack.getCount();
			}
		}
		return total;
	}

	private static void remove(PlayerInventory inventory, Item item, int amount) {
		int remaining = amount;
		for (int slot = 0; slot < inventory.size() && remaining > 0; slot++) {
			ItemStack stack = inventory.getStack(slot);
			if (stack.isOf(item)) {
				int take = Math.min(stack.getCount(), remaining);
				stack.decrement(take);
				remaining -= take;
			}
		}
	}
}
