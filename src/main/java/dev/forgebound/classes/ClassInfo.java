package dev.forgebound.classes;

import dev.forgebound.quality.ItemQuality;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Erzeugt die Beschreibung einer Klasse. Attribut-Vor- und Nachteile werden aus den
 * Modifikatoren erzeugt (mit den Attributnamen des Spiels). Neben der Farbe zeigen
 * Plus- und Minuszeichen, ob etwas ein Vor- oder Nachteil ist.
 */
public final class ClassInfo {
	private ClassInfo() {
	}

	private static final DecimalFormat NUMBER = new DecimalFormat("0.##", DecimalFormatSymbols.getInstance(Locale.ROOT));

	public static String fmt(double value) {
		return NUMBER.format(value);
	}

	public static List<Text> describe(ForgeClass cls) {
		ItemQuality rarity = cls.rarity();
		List<Text> lines = new ArrayList<>();
		lines.add(Text.translatable("message.forgebound.class_header",
				Text.translatable(cls.translationKey()),
				Text.translatable(rarity.translationKey()),
				rarity.symbol()).formatted(rarity.color(), Formatting.BOLD));
		lines.add(Text.translatable(cls.translationKey() + ".desc").formatted(Formatting.GRAY, Formatting.ITALIC));

		List<Text> pros = new ArrayList<>();
		List<Text> cons = new ArrayList<>();
		for (ForgeClass.Mod mod : cls.mods()) {
			String sign = mod.value() >= 0.0 ? "+" : "-";
			double abs = Math.abs(mod.value());
			String number = mod.operation() == net.minecraft.entity.attribute.EntityAttributeModifier.Operation.ADD_VALUE
					? fmt(abs)
					: fmt(abs * 100.0) + "%";
			Text line = Text.literal(sign + number + " ").append(Text.translatable(mod.attribute().value().getTranslationKey()));
			(mod.isBenefit() ? pros : cons).add(line);
		}
		if (cls.forgeLevelBonus() > 0 || cls.forgeXpMultiplier() > 1.0) {
			pros.add(Text.translatable("class.forgebound.forge_bonus",
					cls.forgeLevelBonus(), fmt((cls.forgeXpMultiplier() - 1.0) * 100.0)));
		}
		if (cls.dodgeChance() > 0.0) {
			pros.add(Text.translatable("class.forgebound.dodge", fmt(cls.dodgeChance() * 100.0)));
		}
		if (cls.isFireImmune()) {
			pros.add(Text.translatable("class.forgebound.fire_immune"));
		}
		if (cls.hasPassive()) {
			pros.add(Text.translatable(cls.translationKey() + ".passive"));
		}

		lines.add(Text.translatable("message.forgebound.class_pros").formatted(Formatting.GREEN, Formatting.BOLD));
		for (Text line : pros) {
			lines.add(Text.literal("  ").append(line).formatted(Formatting.GREEN));
		}
		lines.add(Text.translatable("message.forgebound.class_cons").formatted(Formatting.RED, Formatting.BOLD));
		for (Text line : cons) {
			lines.add(Text.literal("  ").append(line).formatted(Formatting.RED));
		}
		return lines;
	}
}
