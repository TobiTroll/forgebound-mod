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
 * Operator-Menue (/forge): Klasse, Schmiedelevel, Charakter-Level, Stat-Punkte, Stats und Mana
 * des eigenen Spielers aendern. Der Server prueft bei jeder Aktion die Berechtigung.
 */
public class AdminScreen extends Screen {
	private static final int W = 360;
	private static final int H = 250;

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
			ButtonWidgetHelper.add(this, Text.translatable(classes[i].translationKey()).formatted(classes[i].rarity().color()),
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

		// Stats: Staerke, Durability, Speed, Mana
		String[] keys = {"str", "dur", "spd", "mana"};
		for (int i = 0; i < 4; i++) {
			int rowY = top + 142 + i * 18;
			button(delta(-1), rx + 70, rowY, 24, AdminActions.STAT_DELTA, i, -1);
			button(delta(1), rx + 96, rowY, 24, AdminActions.STAT_DELTA, i, 1);
			button(delta(10), rx + 122, rowY, 30, AdminActions.STAT_DELTA, i, 10);
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
		context.drawText(textRenderer, Text.translatable("forgebound.gui.class", className), left + 8, top + 140, 0xFFFFD866, false);

		context.drawText(textRenderer, Text.translatable("gui.forgebound.admin.forge_level", ClientStats.get(StatsSyncPayload.FORGE_LEVEL)), rx, top + 22, 0xFFFFFFFF, false);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.admin.char_level", ClientStats.get(StatsSyncPayload.LEVEL)), rx, top + 60, 0xFFFFFFFF, false);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.admin.points", ClientStats.get(StatsSyncPayload.POINTS)), rx, top + 98, 0xFFFFFFFF, false);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.admin.stats"), rx, top + 130, 0xFFFFD866, false);

		String[] keys = {"str", "dur", "spd", "mana"};
		int[] indices = {StatsSyncPayload.STR, StatsSyncPayload.DUR, StatsSyncPayload.SPD, StatsSyncPayload.MANA_STAT};
		for (int i = 0; i < 4; i++) {
			Text line = Text.translatable("gui.forgebound.stat_line", Text.translatable("gui.forgebound.stat." + keys[i]), ClientStats.get(indices[i]));
			context.drawText(textRenderer, line, rx, top + 145 + i * 18, 0xFFFFFFFF, false);
		}
		context.drawText(textRenderer, Text.translatable("gui.forgebound.mana", ClientStats.get(StatsSyncPayload.MANA), ClientStats.get(StatsSyncPayload.MAX_MANA)),
				left + 8, top + 154, 0xFF7FA8FF, false);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	/** Kleine Hilfe, damit Klassen-Knoepfe mit Farbe wie die anderen Knoepfe erzeugt werden. */
	static final class ButtonWidgetHelper {
		private ButtonWidgetHelper() {
		}

		static void add(AdminScreen screen, Text label, int x, int y, int width, int action, int a, int b) {
			screen.button(label, x, y, width, action, a, b);
		}
	}
}
