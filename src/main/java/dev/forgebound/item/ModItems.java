package dev.forgebound.item;

import dev.forgebound.ForgeboundMod;
import dev.forgebound.block.ModBlocks;
import dev.forgebound.forging.ForgeMaterial;
import dev.forgebound.quality.ItemQuality;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.EnumMap;
import java.util.Map;

public final class ModItems {
	private ModItems() {
	}

	private static final Map<ForgeMaterial, Item> SWORDS = new EnumMap<>(ForgeMaterial.class);

	public static final Item FORGING_STATION = Registry.register(
			Registries.ITEM,
			ForgeboundMod.id("forging_station"),
			new BlockItem(ModBlocks.FORGING_STATION, new Item.Settings())
	);

	static {
		for (ForgeMaterial material : ForgeMaterial.values()) {
			Item.Settings settings = new Item.Settings()
					.attributeModifiers(SwordItem.createAttributeModifiers(material.toolMaterial(), 3, -2.4F))
					.component(ModComponents.QUALITY, ItemQuality.COMMON);
			if (material == ForgeMaterial.NETHERITE) {
				settings.fireproof();
			}
			Item sword = Registry.register(
					Registries.ITEM,
					ForgeboundMod.id("forged_" + material.id() + "_sword"),
					new ForgedSwordItem(material.toolMaterial(), settings)
			);
			SWORDS.put(material, sword);
		}
	}

	public static Item sword(ForgeMaterial material) {
		return SWORDS.get(material);
	}

	public static void register() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(FORGING_STATION));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
			for (Item sword : SWORDS.values()) {
				entries.add(sword);
			}
		});
	}
}
