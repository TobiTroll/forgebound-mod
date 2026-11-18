package dev.forgebound.item;

import dev.forgebound.ForgeboundMod;
import dev.forgebound.block.ModBlocks;
import dev.forgebound.quality.ItemQuality;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModItems {
	private ModItems() {
	}

	public static final Item FORGING_STATION = Registry.register(
			Registries.ITEM,
			ForgeboundMod.id("forging_station"),
			new BlockItem(ModBlocks.FORGING_STATION, new Item.Settings())
	);

	public static final Item FORGED_IRON_SWORD = Registry.register(
			Registries.ITEM,
			ForgeboundMod.id("forged_iron_sword"),
			new ForgedSwordItem(
					ToolMaterials.IRON,
					new Item.Settings()
							.attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.IRON, 3, -2.4F))
							.component(ModComponents.QUALITY, ItemQuality.COMMON)
			)
	);

	public static void register() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(FORGING_STATION));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> entries.add(FORGED_IRON_SWORD));
	}
}
