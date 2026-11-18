package dev.forgebound.item;

import dev.forgebound.quality.ItemQuality;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class ForgedSwordItem extends SwordItem {
	public ForgedSwordItem(ToolMaterial material, Settings settings) {
		super(material, settings);
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		super.appendTooltip(stack, context, tooltip, type);
		ItemQuality quality = stack.getOrDefault(ModComponents.QUALITY, ItemQuality.COMMON);
		Text name = Text.translatable(quality.translationKey());
		tooltip.add(Text.translatable("tooltip.forgebound.quality", name, quality.symbol()).formatted(quality.color()));
	}
}
