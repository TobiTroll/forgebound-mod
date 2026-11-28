package dev.forgebound.client;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.character.AdminActions;
import dev.forgebound.classes.ForgeClass;
import dev.forgebound.network.ModPayloads.AdminActionPayload;
import dev.forgebound.network.ModPayloads.StatsSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * Operator-Menue (/forge): Klasse, Schmiedelevel, Charakter-Level, Stat-Punkte, alle acht Stats
 * und Mana des eigenen Spielers aendern. Mit gedrueckter Umschalttaste aendern die Stat-Knoepfe um 10.
 * Der Server prueft bei jeder Aktion die Berechtigung.
 */
public class AdminScreen extends Screen {
	private static final int W = 360;
	private static final int H = 274;
	private static final String[] STAT_KEYS = {"str", "dur", "spd", "mana", "dex", "res", "reg", "luck"};

	private int left;
	private int top;

	public AdminScreen() {
		super(Text.translatable("gui.forgebound.admin.title"));
	}

	private void send(int action, int a, int b) {
		ClientPlayNetworking.send(new AdminActionPayload(action, a, b));
	}

	private void button(Text label, int x, int y, int width, int action, int a, int b) {
		addDrawableChild(ButtonWidget.builder(label, btn -> send(action, a, b)).dimensions(x, y, width, 14).build());
	}

	private static Text delta(int value) {
		return Text.literal(value > 0 ? "+" + value : String.valueOf(value));
	}

	@Override
	protected void init() {
		left = (width - W) / 2;
		top = (height - H) / 2;

		// Klassen: erst "Keine", dann alle
		button(Text.translatable("gui.forgebound.admin.none"), left + 8, top + 22, 86, AdminActions.SET_CLASS, 0, 0);
		ForgeClass[] classes = ForgeClass.values();
		for (int i = 0; i < classes.length; i++) {
			int index = i + 1;
			int column = index % 2;
			int row = index / 2;
			button(Text.translatable(classes[i].translationKey()).formatted(classes[i].rarity().color()),
					left + 8 + column * 90, top + 22 + row * 16, 86, AdminActions.SET_CLASS, classes[i].propertyValue(), 0);
		}

		int rx = left + 196;
		int[] steps = {-5, -1, 1, 5};

		// Schmiedelevel
		for (int j = 0; j < steps.length; j++) {
			button(delta(steps[j]), rx + j * 28, top + 34, 26, AdminActions.FORGE_LEVEL_DELTA, steps[j], 0);
		}
		button(Text.translatable("gui.forgebound.admin.max"), rx + 4 * 28, top + 34, 44, AdminActions.FORGE_LEVEL_SET, BalanceConfig.MAX_LEVEL, 0);

		// Charakter-Level
		for (int j = 0; j < steps.length; j++) {
			button(delta(steps[j]), rx + j * 28, top + 72, 26, AdminActions.CHAR_LEVEL_DELTA, steps[j], 0);
		}
		button(Text.translatable("gui.forgebound.admin.max"), rx + 4 * 28, top + 72, 44, AdminActions.CHAR_LEVEL_DELTA, BalanceConfig.CHAR_MAX_LEVEL, 0);

		// Unverteilte Punkte
		button(delta(-1), rx, top + 110, 26, AdminActions.POINTS_DELTA, -1, 0);
		button(delta(1), rx + 28, top + 110, 26, AdminActions.POINTS_DELTA, 1, 0);
		button(delta(10), rx + 56, top + 110, 30, AdminActions.POINTS_DELTA, 10, 0);

		// Stats in zwei Spalten, Umschalttaste = 10
		for (int i = 0; i < STAT_KEYS.length; i++) {
			final int stat = i;
			int cx = rx + (i / 4) * 82;
			int cy = top + 144 + (i % 4) * 26;
			addDrawableChild(ButtonWidget.builder(Text.literal("-"),
					btn -> send(AdminActions.STAT_DELTA, stat, hasShiftDown() ? -10 : -1)).dimensions(cx, cy + 10, 24, 12).build());
			addDrawableChild(ButtonWidget.builder(Text.literal("+"),
					btn -> send(AdminActions.STAT_DELTA, stat, hasShiftDown() ? 10 : 1)).dimensions(cx + 26, cy + 10, 24, 12).build());
		}

		// Unten: Mana, Stats, Alles
		button(Text.translatable("gui.forgebound.admin.fill_mana"), left + 8, top + H - 24, 112, AdminActions.FILL_MANA, 0, 0);
		button(Text.translatable("gui.forgebound.admin.reset_stats"), left + 124, top + H - 24, 112, AdminActions.RESET_STATS, 0, 0);
		button(Text.translatable("gui.forgebound.admin.reset_all"), left + 240, top + H - 24, 112, AdminActions.RESET_ALL, 0, 0);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		super.renderBackground(context, mouseX, mouseY, delta);
		context.fill(left, top, left + W, top + H, 0xF0141414);
		context.drawBorder(left, top, W, H, 0xFFAAAAAA);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.admin.title"), left + 8, top + 8, 0xFFFFFFFF, false);

		int rx = left + 196;
		ForgeClass cls = ForgeClass.fromProperty(ClientStats.get(StatsSyncPayload.CLASS));
		Text className = cls == null ? Text.translatable("forgebound.gui.class_none") : Text.translatable(cls.translationKey());
		context.drawText(textRenderer, Text.translatable("forgebound.gui.class", className), left + 8, top + 156, 0xFFFFD866, false);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.mana", ClientStats.get(StatsSyncPayload.MANA), ClientStats.get(StatsSyncPayload.MAX_MANA)),
				left + 8, top + 168, 0xFF7FA8FF, false);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.admin.shift_hint"), left + 8, top + H - 38, 0xFF999999, false);

		context.drawText(textRenderer, Text.translatable("gui.forgebound.admin.forge_level", ClientStats.get(StatsSyncPayload.FORGE_LEVEL)), rx, top + 22, 0xFFFFFFFF, false);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.admin.char_level", ClientStats.get(StatsSyncPayload.LEVEL)), rx, top + 60, 0xFFFFFFFF, false);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.admin.points", ClientStats.get(StatsSyncPayload.POINTS)), rx, top + 98, 0xFFFFFFFF, false);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.admin.stats"), rx, top + 130, 0xFFFFD866, false);

		for (int i = 0; i < STAT_KEYS.length; i++) {
			int cx = rx + (i / 4) * 82;
			int cy = top + 144 + (i % 4) * 26;
			Text line = Text.translatable("gui.forgebound.stat_line",
					Text.translatable("gui.forgebound.stat." + STAT_KEYS[i]), ClientStats.get(StatsSyncPayload.statSlot(i)));
			context.drawText(textRenderer, line, cx, cy, 0xFFFFFFFF, false);
		}
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
