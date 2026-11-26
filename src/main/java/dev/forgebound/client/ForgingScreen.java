package dev.forgebound.client;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.classes.ForgeClass;
import dev.forgebound.forging.ForgeMaterial;
import dev.forgebound.forging.ForgeTrait;
import dev.forgebound.forging.ForgingMath;
import dev.forgebound.forging.Gear;
import dev.forgebound.item.ModItems;
import dev.forgebound.quality.ItemQuality;
import dev.forgebound.screen.ForgingScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Schmiedemenue: links Material und Start, in der Mitte das Timing-Minigame und die
 * Auswahl der Ausruestungsart, rechts die aktuellen Seltenheits-Chancen, unten das
 * Schmiedelevel und die Klasse. Die Anzeige rechnet nur mit; verbindlich entscheidet der Server.
 */
public class ForgingScreen extends HandledScreen<ForgingScreenHandler> {
	private static final int W = 380;
	private static final int H = 236;

	private static final int[] ZONE_COLORS = {0, 0xFF6A6A6A, 0xFF3F9F3F, 0xFFE6B800};

	private final ButtonWidget[] materialButtons = new ButtonWidget[ForgeMaterial.values().length];
	private final ButtonWidget[] gearButtons = new ButtonWidget[Gear.values().length];
	private ButtonWidget startButton;
	private ButtonWidget strikeButton;
	private ButtonWidget againButton;

	public ForgingScreen(ForgingScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = W;
		this.backgroundHeight = H;
	}

	@Override
	protected void init() {
		super.init();
		ForgeMaterial[] materials = ForgeMaterial.values();
		for (int i = 0; i < materials.length; i++) {
			final int id = i;
			materialButtons[i] = addDrawableChild(ButtonWidget.builder(Text.empty(), button -> press(id))
					.dimensions(x + 10, y + 34 + i * 22, 106, 20).build());
		}
		startButton = addDrawableChild(ButtonWidget.builder(Text.translatable("forgebound.gui.start"),
				button -> press(ForgingScreenHandler.BTN_START)).dimensions(x + 10, y + 34 + materials.length * 22 + 4, 106, 20).build());
		strikeButton = addDrawableChild(ButtonWidget.builder(Text.translatable("forgebound.gui.strike"),
				button -> press(ForgingScreenHandler.BTN_STRIKE)).dimensions(x + 132, y + 112, 128, 20).build());
		againButton = addDrawableChild(ButtonWidget.builder(Text.translatable("forgebound.gui.again"),
				button -> {
					press(ForgingScreenHandler.BTN_RESET);
					close();
				}).dimensions(x + 132, y + 112, 128, 20).build());
		Gear[] gears = Gear.values();
		for (int i = 0; i < gears.length; i++) {
			final int id = ForgingScreenHandler.BTN_GEAR_BASE + i;
			gearButtons[i] = addDrawableChild(ButtonWidget.builder(Text.empty(), button -> press(id))
					.dimensions(x + 132 + i * 26, y + 178, 24, 24)
					.tooltip(Tooltip.of(Text.translatable(gears[i].translationKey())))
					.build());
		}
	}

	private void press(int buttonId) {
		if (client != null && client.interactionManager != null) {
			client.interactionManager.clickButton(handler.syncId, buttonId);
		}
	}

	private void updateWidgets() {
		int state = handler.get(ForgingScreenHandler.P_STATE);
		int selected = handler.get(ForgingScreenHandler.P_MATERIAL);
		Gear gear = Gear.byIndex(handler.get(ForgingScreenHandler.P_GEAR));
		ForgeMaterial[] materials = ForgeMaterial.values();
		for (int i = 0; i < materials.length; i++) {
			Text materialName = Text.translatable(materials[i].translationKey());
			MutableText label = Text.literal(i == selected ? "> " : "  ");
			if (materials[i].supports(gear)) {
				label.append(Text.translatable("forgebound.gui.material_button", materialName, materials[i].cost(gear)));
			} else {
				label.append(Text.translatable("forgebound.gui.material_na", materialName));
			}
			materialButtons[i].setMessage(label);
			materialButtons[i].active = state != ForgingScreenHandler.STATE_FORGING;
		}
		for (ButtonWidget gearButton : gearButtons) {
			gearButton.active = state != ForgingScreenHandler.STATE_FORGING;
		}
		startButton.visible = state == ForgingScreenHandler.STATE_IDLE;
		startButton.active = ForgeMaterial.byIndex(selected).supports(gear);
		strikeButton.visible = state == ForgingScreenHandler.STATE_FORGING;
		againButton.visible = state == ForgingScreenHandler.STATE_DONE;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		updateWidgets();
		super.render(context, mouseX, mouseY, delta);
		drawGearIcons(context);
		drawMouseoverTooltip(context, mouseX, mouseY);
	}

	/** Zeichnet die Gegenstands-Symbole auf die Ausruestungs-Knoepfe (nach den Widgets). */
	private void drawGearIcons(DrawContext context) {
		ForgeMaterial material = ForgeMaterial.byIndex(handler.get(ForgingScreenHandler.P_MATERIAL));
		int selectedGear = handler.get(ForgingScreenHandler.P_GEAR);
		Gear[] gears = Gear.values();
		for (int i = 0; i < gears.length; i++) {
			Item item = ModItems.gear(material, gears[i]);
			ItemStack icon = new ItemStack(item != null ? item : Items.BARRIER);
			context.drawItem(icon, x + 132 + i * 26 + 4, y + 178 + 4);
			if (i == selectedGear) {
				context.drawBorder(x + 131 + i * 26, y + 177, 26, 26, 0xFFFFD866);
			}
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == GLFW.GLFW_KEY_SPACE && handler.get(ForgingScreenHandler.P_STATE) == ForgingScreenHandler.STATE_FORGING) {
			press(ForgingScreenHandler.BTN_STRIKE);
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		// Eigene Beschriftung in drawBackground, keine Standard-Titel.
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		int state = handler.get(ForgingScreenHandler.P_STATE);
		int level = handler.get(ForgingScreenHandler.P_LEVEL);
		ForgeClass playerClass = ForgeClass.fromProperty(handler.get(ForgingScreenHandler.P_CLASS));
		int effectiveLevel = level + (playerClass == null ? 0 : playerClass.forgeLevelBonus());
		int points = handler.get(ForgingScreenHandler.P_POINTS);
		int strikes = handler.get(ForgingScreenHandler.P_STRIKES);

		context.fill(x, y, x + W, y + H, 0xF0141414);
		context.drawBorder(x, y, W, H, 0xFFAAAAAA);
		context.drawText(textRenderer, Text.translatable("container.forgebound.forging_station"), x + 8, y + 8, 0xFFFFFFFF, false);

		// Linke Spalte
		context.drawText(textRenderer, Text.translatable("forgebound.gui.material"), x + 10, y + 22, 0xFFFFD866, false);

		// Mitte: Minigame
		int midX = x + 132;
		context.drawText(textRenderer, Text.translatable("forgebound.gui.strikes", strikes, BalanceConfig.STRIKES), midX, y + 22, 0xFFFFFFFF, false);
		context.drawText(textRenderer, Text.translatable("forgebound.gui.points", points, BalanceConfig.MAX_POINTS), midX, y + 34, 0xFFFFFFFF, false);

		int barX = midX;
		int barY = y + 54;
		int barW = 128;
		int barH = 18;
		context.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF000000);
		context.fill(barX, barY, barX + barW, barY + barH, 0xFF303030);
		if (state == ForgingScreenHandler.STATE_FORGING && client != null && client.world != null) {
			int target = handler.get(ForgingScreenHandler.P_TARGET);
			for (int judgement = 1; judgement <= 3; judgement++) {
				double r = ForgingMath.radius(judgement, effectiveLevel);
				int left = Math.max(barX, barX + (int) ((target - r) / 100.0 * barW));
				int right = Math.min(barX + barW, barX + (int) ((target + r) / 100.0 * barW));
				context.fill(left, barY, right, barY + barH, ZONE_COLORS[judgement]);
			}
			double pos = ForgingMath.markerPos(client.world.getTime(), handler.get(ForgingScreenHandler.P_PHASE), delta);
			int markerX = barX + (int) (pos / 100.0 * barW);
			context.fill(markerX - 1, barY - 3, markerX + 2, barY + barH + 3, 0xFFFFFFFF);
		}
		context.drawText(textRenderer, Text.translatable("forgebound.gui.legend"), midX, y + 78, 0xFFBBBBBB, false);
		context.drawText(textRenderer, Text.translatable("forgebound.gui.hint"), midX, y + 92, 0xFFBBBBBB, false);

		int last = handler.get(ForgingScreenHandler.P_LAST);
		if (last >= 0 && last <= 3 && state != ForgingScreenHandler.STATE_IDLE) {
			context.drawText(textRenderer, Text.translatable("forgebound.gui.judge." + last), midX, y + 138, 0xFFFFFFFF, true);
		}
		int result = handler.get(ForgingScreenHandler.P_RESULT);
		if (state == ForgingScreenHandler.STATE_DONE && result >= 0 && result < ItemQuality.values().length) {
			ItemQuality quality = ItemQuality.values()[result];
			Integer rgb = quality.color().getColorValue();
			int color = 0xFF000000 | (rgb == null ? 0xFFFFFF : rgb);
			Text text = Text.translatable("forgebound.gui.result", Text.translatable(quality.translationKey()), quality.symbol());
			context.drawText(textRenderer, text, midX, y + 148, color, true);
			Text traitText = Text.translatable("forgebound.gui.result_traits",
					BalanceConfig.TRAIT_COUNT[result], ForgeTrait.roman(BalanceConfig.TRAIT_LEVEL[result]));
			context.drawText(textRenderer, traitText, midX, y + 158, 0xFFFFFFFF, true);
			context.drawText(textRenderer, Text.translatable("forgebound.gui.result_hint"), midX, y + 168, 0xFFBBBBBB, false);
		}

		// Rechte Spalte: Chancen
		int panelX = x + 274;
		int panelW = 98;
		context.drawText(textRenderer, Text.translatable("forgebound.gui.chances"), panelX, y + 22, 0xFFFFD866, false);
		ForgeMaterial material = ForgeMaterial.byIndex(handler.get(ForgingScreenHandler.P_MATERIAL));
		double[] chances = ForgingMath.chances(material, effectiveLevel, points);
		ItemQuality[] qualities = ItemQuality.values();
		for (int i = 0; i < qualities.length; i++) {
			int rowY = y + 36 + i * 20;
			Integer rgb = qualities[i].color().getColorValue();
			int base = rgb == null ? 0xFFFFFF : rgb;
			context.fill(panelX, rowY, panelX + panelW, rowY + 16, 0xFF262626);
			int fillW = (int) Math.round(panelW * chances[i]);
			context.fill(panelX, rowY, panelX + fillW, rowY + 16, 0x99000000 | base);
			Text name = Text.translatable(qualities[i].translationKey()).append(" " + qualities[i].symbol());
			context.drawText(textRenderer, name, panelX + 3, rowY + 4, 0xFFFFFFFF, true);
			String percent = (Math.round(chances[i] * 1000.0) / 10.0) + "%";
			context.drawText(textRenderer, percent, panelX + panelW - textRenderer.getWidth(percent) - 3, rowY + 4, 0xFFFFFFFF, true);
		}
		context.drawTextWrapped(textRenderer, Text.translatable("forgebound.gui.chances_note"), panelX, y + 142, panelW, 0xFFBBBBBB);

		// Unten: Klasse, Schmiedelevel
		if (playerClass != null) {
			Integer rgb = playerClass.rarity().color().getColorValue();
			int color = 0xFF000000 | (rgb == null ? 0xFFFFFF : rgb);
			context.drawText(textRenderer, Text.translatable("forgebound.gui.class", Text.translatable(playerClass.translationKey())), x + 10, y + 194, color, true);
		} else {
			context.drawText(textRenderer, Text.translatable("forgebound.gui.class_none"), x + 10, y + 194, 0xFF999999, false);
		}
		int xp = handler.get(ForgingScreenHandler.P_XP);
		int needed = BalanceConfig.xpForLevel(level);
		Text levelText = level >= BalanceConfig.MAX_LEVEL
				? Text.translatable("forgebound.gui.level_max", level)
				: Text.translatable("forgebound.gui.level", level, xp, needed);
		context.drawText(textRenderer, levelText, x + 8, y + H - 28, 0xFFFFFFFF, false);
		int levelBarW = 114;
		context.fill(x + 8, y + H - 16, x + 8 + levelBarW, y + H - 8, 0xFF262626);
		double fraction = level >= BalanceConfig.MAX_LEVEL ? 1.0 : Math.min(1.0, xp / (double) needed);
		context.fill(x + 8, y + H - 16, x + 8 + (int) (levelBarW * fraction), y + H - 8, 0xFF55AA55);
	}
}
