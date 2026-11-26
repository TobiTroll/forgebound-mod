package dev.forgebound.item;

import dev.forgebound.ForgeboundMod;
import dev.forgebound.block.ModBlocks;
import dev.forgebound.forging.ForgeMaterial;
import dev.forgebound.forging.Gear;
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

	private static final Map<ForgeMaterial, Map<Gear, Item>> GEAR = new EnumMap<>(ForgeMaterial.class);

	public static final Item FORGING_STATION = Registry.register(
			Registries.ITEM,
			ForgeboundMod.id("forging_station"),
			new BlockItem(ModBlocks.FORGING_STATION, new Item.Settings())
	);

	public static final Item AURORIT_ORE = Registry.register(
			Registries.ITEM,
			ForgeboundMod.id("aurorit_ore"),
			new BlockItem(ModBlocks.AURORIT_ORE, new Item.Settings())
	);

	public static final Item DEEPSLATE_AURORIT_ORE = Registry.register(
			Registries.ITEM,
			ForgeboundMod.id("deepslate_aurorit_ore"),
			new BlockItem(ModBlocks.DEEPSLATE_AURORIT_ORE, new Item.Settings())
	);

	public static final Item RAW_AURORIT = Registry.register(
			Registries.ITEM,
			ForgeboundMod.id("raw_aurorit"),
			new Item(new Item.Settings())
	);

	public static final Item AURORIT_INGOT = Registry.register(
			Registries.ITEM,
			ForgeboundMod.id("aurorit_ingot"),
			new Item(new Item.Settings())
	);

	public static final Item AWAKENING_STONE = Registry.register(
			Registries.ITEM,
			ForgeboundMod.id("awakening_stone"),
			new AwakeningStoneItem(new Item.Settings().maxCount(16))
	);

	static {
		for (ForgeMaterial material : ForgeMaterial.values()) {
			Map<Gear, Item> items = new EnumMap<>(Gear.class);
			for (Gear gear : Gear.values()) {
				if (!material.supports(gear)) {
					continue;
				}
				Item.Settings settings = new Item.Settings().component(ModComponents.QUALITY, ItemQuality.COMMON);
				if (material == ForgeMaterial.NETHERITE || material == ForgeMaterial.AURORIT) {
					settings.fireproof();
				}
				Item item;
				if (gear.isArmor()) {
					settings.maxDamage(gear.armorType().getMaxDamage(material.armorDurability()));
					item = new ForgedArmorItem(material.armorMaterial(), gear.armorType(), settings);
				} else {
					settings.attributeModifiers(SwordItem.createAttributeModifiers(material.toolMaterial(), 3, -2.4F));
					item = new ForgedSwordItem(material.toolMaterial(), settings);
				}
				items.put(gear, Registry.register(
						Registries.ITEM,
						ForgeboundMod.id("forged_" + material.id() + "_" + gear.id()),
						item
				));
			}
			GEAR.put(material, items);
		}
	}

	/** Gibt null zurueck, wenn es die Kombination nicht gibt (z. B. Stein-Ruestung). */
	public static Item gear(ForgeMaterial material, Gear gear) {
		Map<Gear, Item> items = GEAR.get(material);
		return items == null ? null : items.get(gear);
	}

	public static Item sword(ForgeMaterial material) {
		return gear(material, Gear.SWORD);
	}

	public static void register() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(FORGING_STATION));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(AWAKENING_STONE));
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
			entries.add(AURORIT_ORE);
			entries.add(DEEPSLATE_AURORIT_ORE);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
			entries.add(RAW_AURORIT);
			entries.add(AURORIT_INGOT);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
			for (Map<Gear, Item> items : GEAR.values()) {
				for (Item item : items.values()) {
					entries.add(item);
				}
			}
		});
	}
}
