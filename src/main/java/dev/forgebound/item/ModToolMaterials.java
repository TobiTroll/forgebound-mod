package dev.forgebound.item;

import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

/** Eigene Werkzeugmaterialien. Aurorit liegt ueber Netherit. */
public enum ModToolMaterials implements ToolMaterial {
	AURORIT(3200, 10.0F, 6.0F, BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 22);

	private final int durability;
	private final float miningSpeed;
	private final float attackDamage;
	private final TagKey<Block> inverseTag;
	private final int enchantability;

	ModToolMaterials(int durability, float miningSpeed, float attackDamage, TagKey<Block> inverseTag, int enchantability) {
		this.durability = durability;
		this.miningSpeed = miningSpeed;
		this.attackDamage = attackDamage;
		this.inverseTag = inverseTag;
		this.enchantability = enchantability;
	}

	@Override
	public int getDurability() {
		return durability;
	}

	@Override
	public float getMiningSpeedMultiplier() {
		return miningSpeed;
	}

	@Override
	public float getAttackDamage() {
		return attackDamage;
	}

	@Override
	public TagKey<Block> getInverseTag() {
		return inverseTag;
	}

	@Override
	public int getEnchantability() {
		return enchantability;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.ofItems(ModItems.AURORIT_INGOT);
	}
}
