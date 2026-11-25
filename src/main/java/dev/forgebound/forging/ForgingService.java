package dev.forgebound.forging;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.item.ModComponents;
import dev.forgebound.item.ModItems;
import dev.forgebound.quality.ItemQuality;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Serverseitige Hilfen: Materialverbrauch, Ergebnis-Gegenstand, Traits, Erfahrung. */
public final class ForgingService {
	private ForgingService() {
	}

	/** Prueft und verbraucht die Materialien. Im Kreativmodus kostenlos. Ruestung braucht keine Staebe. */
	public static boolean consumeMaterials(PlayerEntity player, ForgeMaterial material, Gear gear) {
		if (player.isCreative()) {
			return true;
		}
		PlayerInventory inventory = player.getInventory();
		int cost = material.cost(gear);
		int sticks = gear.isArmor() ? 0 : BalanceConfig.STICKS;
		if (count(inventory, material.ingredient()) < cost || count(inventory, Items.STICK) < sticks) {
			return false;
		}
		remove(inventory, material.ingredient(), cost);
		remove(inventory, Items.STICK, sticks);
		return true;
	}

	public static ItemStack createGear(ForgeMaterial material, Gear gear, ItemQuality quality) {
		return gear.isArmor() ? createArmor(material, gear, quality) : createSword(material, quality);
	}

	public static ItemStack createSword(ForgeMaterial material, ItemQuality quality) {
		ItemStack stack = new ItemStack(ModItems.sword(material));
		stack.set(ModComponents.QUALITY, quality);
		stack.set(ModComponents.TRAITS, rollTraits(quality, Random.create()));
		stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS,
				SwordItem.createAttributeModifiers(material.toolMaterial(), 3 + quality.damageBonus(), -2.4F));
		return stack;
	}

	/** Ruestung: Grundwerte des Materials, Seltenheitsbonus und Trait-Attribute als Modifikatoren. */
	public static ItemStack createArmor(ForgeMaterial material, Gear gear, ItemQuality quality) {
		ItemStack stack = new ItemStack(ModItems.gear(material, gear));
		stack.set(ModComponents.QUALITY, quality);

		ArmorItem.Type type = gear.armorType();
		AttributeModifierSlot slot = AttributeModifierSlot.forEquipmentSlot(type.getEquipmentSlot());
		ArmorMaterial armor = material.armorMaterial().value();
		Identifier baseId = Identifier.ofVanilla("armor." + type.getName());

		AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
		builder.add(EntityAttributes.GENERIC_ARMOR, new EntityAttributeModifier(baseId,
				armor.getProtection(type) + BalanceConfig.ARMOR_BONUS[quality.ordinal()],
				EntityAttributeModifier.Operation.ADD_VALUE), slot);
		if (armor.toughness() > 0.0F) {
			builder.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new EntityAttributeModifier(baseId,
					armor.toughness(), EntityAttributeModifier.Operation.ADD_VALUE), slot);
		}
		if (armor.knockbackResistance() > 0.0F) {
			builder.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(baseId,
					armor.knockbackResistance(), EntityAttributeModifier.Operation.ADD_VALUE), slot);
		}

		List<String> encoded = new ArrayList<>();
		for (ArmorTrait.Entry entry : rollArmorTraits(quality, Random.create())) {
			entry.trait().addModifier(builder, slot, type.getName(), entry.level());
			encoded.add(entry.encode());
		}
		stack.set(ModComponents.TRAITS, encoded);
		stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());
		return stack;
	}

	/** Wuerfelt verschiedene Waffen-Traits aus. Anzahl und Stufe haengen von der Seltenheit ab. */
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

	/** Wuerfelt verschiedene Ruestungs-Traits aus. */
	public static List<ArmorTrait.Entry> rollArmorTraits(ItemQuality quality, Random random) {
		List<ArmorTrait> pool = new ArrayList<>(Arrays.asList(ArmorTrait.values()));
		Collections.shuffle(pool, new java.util.Random(random.nextLong()));
		int count = Math.min(BalanceConfig.TRAIT_COUNT[quality.ordinal()], pool.size());
		int level = BalanceConfig.TRAIT_LEVEL[quality.ordinal()];
		List<ArmorTrait.Entry> result = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			result.add(new ArmorTrait.Entry(pool.get(i), level));
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
