package dev.forgebound.client;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.classes.ClassInfo;
import dev.forgebound.classes.ForgeClass;
import dev.forgebound.network.ModPayloads.AllocatePayload;
import dev.forgebound.network.ModPayloads.StatsSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * Charaktermenue (Taste K): Level, Erfahrung, Klasse, Mana und die vier Stats
 * Staerke, Durability, Speed und Mana mit Plus-Knoepfen fuer Stat-Punkte.
 * Die Anzeige liest nur die Server-Daten; vergeben wird serverseitig.
 */
public class CharacterScreen extends Screen {
	private static final int W = 280;
	private static final int H = 196;
	private static final String[] STAT_KEYS = {"str", "dur", "spd", "mana"};
	private static final int[] STAT_INDEX = {
			StatsSyncPayload.STR, StatsSyncPayload.DUR, StatsSyncPayload.SPD, StatsSyncPayload.MANA_STAT
	};

	private int left;
	private int top;
	private final ButtonWidget[] plusOne = new ButtonWidget[4];
	private final ButtonWidget[] plusFive = new ButtonWidget[4];

	public CharacterScreen() {
		super(Text.translatable("gui.forgebound.character"));
	}

	@Override
	protected void init() {
		left = (width - W) / 2;
		top = (height - H) / 2;
		for (int i = 0; i < 4; i++) {
			final int stat = i;
			plusOne[i] = addDrawableChild(ButtonWidget.builder(Text.literal("+1"), button -> send(stat, 1))
					.dimensions(left + W - 66, top + 88 + i * 24, 26, 18).build());
			plusFive[i] = addDrawableChild(ButtonWidget.builder(Text.literal("+5"), button -> send(stat, 5))
					.dimensions(left + W - 36, top + 88 + i * 24, 26, 18).build());
		}
	}

	private void send(int stat, int amount) {
		ClientPlayNetworking.send(new AllocatePayload(stat, amount));
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		super.renderBackground(context, mouseX, mouseY, delta);

		int level = ClientStats.get(StatsSyncPayload.LEVEL);
		int xp = ClientStats.get(StatsSyncPayload.XP);
		int needed = ClientStats.get(StatsSyncPayload.XP_NEEDED);
		int points = ClientStats.get(StatsSyncPayload.POINTS);
		int mana = ClientStats.get(StatsSyncPayload.MANA);
		int maxMana = ClientStats.get(StatsSyncPayload.MAX_MANA);
		ForgeClass cls = ForgeClass.fromProperty(ClientStats.get(StatsSyncPayload.CLASS));

		for (int i = 0; i < 4; i++) {
			plusOne[i].active = points > 0;
			plusFive[i].active = points > 0;
		}

		context.fill(left, top, left + W, top + H, 0xF0141414);
		context.drawBorder(left, top, W, H, 0xFFAAAAAA);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.character"), left + 8, top + 8, 0xFFFFFFFF, false);

		context.drawText(textRenderer, Text.translatable("gui.forgebound.level", level), left + 8, top + 24, 0xFFFFD866, false);
		Text xpText = needed > 0
				? Text.translatable("gui.forgebound.xp", xp, needed)
				: Text.translatable("gui.forgebound.xp_max");
		context.drawText(textRenderer, xpText, left + W - 8 - textRenderer.getWidth(xpText), top + 24, 0xFFBBBBBB, false);
		context.fill(left + 8, top + 36, left + W - 8, top + 42, 0xFF262626);
		double fraction = needed > 0 ? Math.min(1.0, xp / (double) needed) : 1.0;
		context.fill(left + 8, top + 36, left + 8 + (int) ((W - 16) * fraction), top + 42, 0xFF55AA55);

		context.drawText(textRenderer, Text.translatable("gui.forgebound.points", points), left + 8, top + 50,
				points > 0 ? 0xFFFFD866 : 0xFFBBBBBB, false);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.mana", mana, maxMana), left + W / 2, top + 50, 0xFF7FA8FF, false);

		if (cls != null) {
			Integer rgb = cls.rarity().color().getColorValue();
			int color = 0xFF000000 | (rgb == null ? 0xFFFFFF : rgb);
			context.drawText(textRenderer, Text.translatable("forgebound.gui.class", Text.translatable(cls.translationKey())), left + 8, top + 62, color, true);
		} else {
			context.drawText(textRenderer, Text.translatable("forgebound.gui.class_none"), left + 8, top + 62, 0xFF999999, false);
		}
		context.drawText(textRenderer, Text.translatable("gui.forgebound.forge_level", ClientStats.get(StatsSyncPayload.FORGE_LEVEL)),
				left + 8, top + 74, 0xFFBBBBBB, false);

		for (int i = 0; i < 4; i++) {
			int value = ClientStats.get(STAT_INDEX[i]);
			int rowY = top + 92 + i * 24;
			context.drawText(textRenderer, Text.translatable("gui.forgebound.stat_line",
					Text.translatable("gui.forgebound.stat." + STAT_KEYS[i]), value), left + 8, rowY, 0xFFFFFFFF, true);
			context.drawText(textRenderer, effectText(i, value), left + 8, rowY + 10, 0xFF999999, false);
		}
	}

	private static Text effectText(int stat, int value) {
		return switch (stat) {
			case 0 -> Text.translatable("gui.forgebound.effect.str", ClassInfo.fmt(value * BalanceConfig.STR_DAMAGE_PER_POINT));
			case 1 -> Text.translatable("gui.forgebound.effect.dur",
					ClassInfo.fmt(value * BalanceConfig.DUR_HEALTH_PER_POINT), ClassInfo.fmt(value * BalanceConfig.DUR_ARMOR_PER_POINT));
			case 2 -> Text.translatable("gui.forgebound.effect.spd", ClassInfo.fmt(value * BalanceConfig.SPD_SPEED_PER_POINT * 100.0));
			default -> Text.translatable("gui.forgebound.effect.mana",
					BalanceConfig.BASE_MANA + BalanceConfig.MANA_PER_POINT * value,
					ClassInfo.fmt(BalanceConfig.BASE_MANA_REGEN + BalanceConfig.MANA_REGEN_PER_POINT * value));
		};
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
