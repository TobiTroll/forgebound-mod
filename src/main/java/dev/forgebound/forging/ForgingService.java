package dev.forgebound.forging;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.item.ModComponents;
import dev.forgebound.item.ModItems;
import dev.forgebound.quality.ItemQuality;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

/**
 * Prototyp-Herstellung: Aus Eisenbarren und Stock wird ein geschmiedetes
 * Eisenschwert mit Qualitaet. Nur serverseitig aufrufen.
 */
public final class ForgingService {
	private ForgingService() {
	}

	public static boolean tryForgeIronSword(PlayerEntity player) {
		PlayerInventory inventory = player.getInventory();
		int needIngots = BalanceConfig.FORGED_IRON_SWORD_INGOTS;
		int needSticks = BalanceConfig.FORGED_IRON_SWORD_STICKS;
		boolean creative = player.isCreative();

		if (!creative && (count(inventory, Items.IRON_INGOT) < needIngots || count(inventory, Items.STICK) < needSticks)) {
			player.sendMessage(Text.translatable("message.forgebound.missing_materials", needIngots, needSticks), true);
			return false;
		}

		if (!creative) {
			remove(inventory, Items.IRON_INGOT, needIngots);
			remove(inventory, Items.STICK, needSticks);
		}

		ItemStack result = new ItemStack(ModItems.FORGED_IRON_SWORD);
		result.set(ModComponents.QUALITY, ItemQuality.COMMON);
		inventory.offerOrDrop(result);
		player.sendMessage(Text.translatable("message.forgebound.forged"), true);
		return true;
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
