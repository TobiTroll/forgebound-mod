package dev.forgebound.item;

import dev.forgebound.forging.ForgeTrait;
import dev.forgebound.quality.ItemQuality;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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

		List<String> traits = stack.get(ModComponents.TRAITS);
		if (traits != null) {
			for (String raw : traits) {
				ForgeTrait.Entry entry = ForgeTrait.parse(raw);
				if (entry == null) {
					continue;
				}
				Text line = Text.translatable("tooltip.forgebound.trait",
						Text.translatable(entry.trait().translationKey()),
						ForgeTrait.roman(entry.level()),
						Text.translatable(entry.trait().translationKey() + ".short"));
				tooltip.add(Text.literal("+ ").append(line).formatted(Formatting.AQUA));
			}
		}
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		boolean result = super.postHit(stack, target, attacker);
		if (!attacker.getWorld().isClient) {
			List<String> traits = stack.get(ModComponents.TRAITS);
			if (traits != null) {
				for (String raw : traits) {
					ForgeTrait.Entry entry = ForgeTrait.parse(raw);
					if (entry != null) {
						entry.trait().onHit(entry.level(), target, attacker);
					}
				}
			}
		}
		return result;
	}
}
