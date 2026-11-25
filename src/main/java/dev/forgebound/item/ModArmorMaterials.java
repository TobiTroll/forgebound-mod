package dev.forgebound.item;

import dev.forgebound.ForgeboundMod;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Eigene Ruestungsmaterialien. Aurorit liegt ueber Netherit. Texturen: textures/models/armor/aurorit_layer_1 und _2. */
public final class ModArmorMaterials {
	private ModArmorMaterials() {
	}

	public static final RegistryEntry<ArmorMaterial> AURORIT = register("aurorit", defense(3, 7, 9, 4), 22, 4.0F, 0.15F);

	private static Map<ArmorItem.Type, Integer> defense(int boots, int leggings, int chestplate, int helmet) {
		Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
		map.put(ArmorItem.Type.BOOTS, boots);
		map.put(ArmorItem.Type.LEGGINGS, leggings);
		map.put(ArmorItem.Type.CHESTPLATE, chestplate);
		map.put(ArmorItem.Type.HELMET, helmet);
		map.put(ArmorItem.Type.BODY, 12);
		return map;
	}

	private static RegistryEntry<ArmorMaterial> register(String name, Map<ArmorItem.Type, Integer> defense,
																 int enchantability, float toughness, float knockbackResistance) {
		Identifier id = ForgeboundMod.id(name);
		ArmorMaterial material = new ArmorMaterial(
				defense,
				enchantability,
				SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE,
				() -> Ingredient.ofItems(ModItems.AURORIT_INGOT),
				List.of(new ArmorMaterial.Layer(id)),
				toughness,
				knockbackResistance
		);
		return Registry.registerReference(Registries.ARMOR_MATERIAL, id, material);
	}
}
