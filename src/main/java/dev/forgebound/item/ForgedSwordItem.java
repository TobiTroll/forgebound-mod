package dev.forgebound.item;

import dev.forgebound.forging.ForgeTrait;
import dev.forgebound.forging.Relic;
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

		int upgrade = stack.getOrDefault(ModComponents.UPGRADE, 0);
		if (upgrade > 0) {
			tooltip.add(Text.translatable("tooltip.forgebound.upgrade", upgrade).formatted(Formatting.GOLD));
		}

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

		Relic relic = Relic.byId(stack.get(ModComponents.RELIC));
		if (relic != null) {
			for (int i = 1; i <= 3; i++) {
				tooltip.add(Text.translatable(relic.translationKey() + ".lore." + i).formatted(Formatting.DARK_GRAY, Formatting.ITALIC));
			}
			tooltip.add(Text.translatable(relic.translationKey() + ".ability").formatted(relic.color()));
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
			Relic relic = Relic.byId(stack.get(ModComponents.RELIC));
			if (relic != null) {
				relic.onHit(target, attacker);
			}
		}
		return result;
	}
}
