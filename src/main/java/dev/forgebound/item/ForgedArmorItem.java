package dev.forgebound.item;

import dev.forgebound.forging.ArmorTrait;
import dev.forgebound.quality.ItemQuality;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ForgedArmorItem extends ArmorItem {
	public ForgedArmorItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings) {
		super(material, type, settings);
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
				ArmorTrait.Entry entry = ArmorTrait.parse(raw);
				if (entry == null) {
					continue;
				}
				Text line = Text.translatable("tooltip.forgebound.trait",
						Text.translatable(entry.trait().translationKey()),
						dev.forgebound.forging.ForgeTrait.roman(entry.level()),
						Text.translatable(entry.trait().translationKey() + ".short"));
				tooltip.add(Text.literal("+ ").append(line).formatted(Formatting.AQUA));
			}
		}
	}
}
