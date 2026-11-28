package dev.forgebound.client;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.classes.ForgeClass;
import dev.forgebound.forging.GearCosts;
import dev.forgebound.forging.GearInfo;
import dev.forgebound.item.ModComponents;
import dev.forgebound.item.ModItems;
import dev.forgebound.network.ModPayloads.StatsSyncPayload;
import dev.forgebound.quality.ItemQuality;
import dev.forgebound.screen.RepairScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Reparatur- und Verbesserungsmenue fuer das geschmiedete Stueck in deiner Haupthand
 * (Schleichen + Rechtsklick auf die Schmiedestation). Die Anzeige rechnet nur mit;
 * Verbrauch und Wirkung entscheidet der Server.
 */
public class RepairScreen extends HandledScreen<RepairScreenHandler> {
	private static final int W = 260;
	private static final int H = 196;

	private ButtonWidget repairButton;
	private ButtonWidget upgradeButton;

	public RepairScreen(RepairScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
		this.backgroundWidth = W;
		this.backgroundHeight = H;
	}

	@Override
	protected void init() {
		super.init();
		repairButton = addDrawableChild(ButtonWidget.builder(Text.empty(), button -> press(RepairScreenHandler.BTN_REPAIR))
				.dimensions(x + 10, y + 130, 240, 18).build());
		upgradeButton = addDrawableChild(ButtonWidget.builder(Text.empty(), button -> press(RepairScreenHandler.BTN_UPGRADE))
				.dimensions(x + 10, y + 152, 240, 18).build());
		addDrawableChild(ButtonWidget.builder(Text.translatable("gui.forgebound.repair.back"), button -> press(RepairScreenHandler.BTN_BACK))
				.dimensions(x + 10, y + 174, 240, 16).build());
	}

	private void press(int buttonId) {
		if (client != null && client.interactionManager != null) {
			client.interactionManager.clickButton(handler.syncId, buttonId);
		}
	}

	private ItemStack heldStack() {
		return client != null && client.player != null ? client.player.getMainHandStack() : ItemStack.EMPTY;
	}

	private int countInInventory(Item item) {
		if (client == null || client.player == null) {
			return 0;
		}
		PlayerEntity player = client.player;
		int total = 0;
		for (int slot = 0; slot < player.getInventory().size(); slot++) {
			ItemStack stack = player.getInventory().getStack(slot);
			if (stack.isOf(item)) {
				total += stack.getCount();
			}
		}
		return total;
	}

	private void updateWidgets() {
		ItemStack stack = heldStack();
		GearInfo info = stack.isEmpty() ? null : ModItems.info(stack.getItem());
		ForgeClass cls = ForgeClass.fromProperty(ClientStats.get(StatsSyncPayload.CLASS));
		int forgeLevel = ClientStats.get(StatsSyncPayload.FORGE_LEVEL);

		if (info == null) {
			repairButton.setMessage(Text.translatable("gui.forgebound.repair.repair_none"));
			repairButton.active = false;
			upgradeButton.setMessage(Text.translatable("gui.forgebound.repair.no_item"));
			upgradeButton.active = false;
			return;
		}

		Item ingredient = info.material().ingredient();
		int have = countInInventory(ingredient);

		if (!stack.isDamaged()) {
			repairButton.setMessage(Text.translatable("gui.forgebound.repair.repair_none"));
			repairButton.active = false;
		} else {
			int cost = GearCosts.repairCost(stack, info, cls);
			repairButton.setMessage(Text.translatable("gui.forgebound.repair.repair_button", cost, ingredient.getName()));
			repairButton.active = have >= cost || (client != null && client.player != null && client.player.isCreative());
		}

		int upgrade = stack.getOrDefault(ModComponents.UPGRADE, 0);
		int next = upgrade + 1;
		if (next > BalanceConfig.UPGRADE_MAX) {
			upgradeButton.setMessage(Text.translatable("gui.forgebound.repair.upgrade_max"));
			upgradeButton.active = false;
		} else {
			int cost = GearCosts.upgradeCost(info, next, cls);
			upgradeButton.setMessage(Text.translatable("gui.forgebound.repair.upgrade_button", next, cost, ingredient.getName()));
			boolean creative = client != null && client.player != null && client.player.isCreative();
			upgradeButton.active = (have >= cost || creative) && forgeLevel >= GearCosts.requiredForgeLevel(next);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		updateWidgets();
		super.render(context, mouseX, mouseY, delta);
		ItemStack stack = heldStack();
		if (!stack.isEmpty() && ModItems.info(stack.getItem()) != null) {
			context.drawItem(stack, x + 10, y + 24);
		}
		drawMouseoverTooltip(context, mouseX, mouseY);
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		// Eigene Beschriftung in drawBackground.
	}

	@Override
	protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		context.fill(x, y, x + W, y + H, 0xF0141414);
		context.drawBorder(x, y, W, H, 0xFFAAAAAA);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.repair.title"), x + 8, y + 8, 0xFFFFFFFF, false);

		ItemStack stack = heldStack();
		GearInfo info = stack.isEmpty() ? null : ModItems.info(stack.getItem());
		if (info == null) {
			context.drawText(textRenderer, Text.translatable("gui.forgebound.repair.no_item"), x + 10, y + 30, 0xFFFF8888, false);
			context.drawText(textRenderer, Text.translatable("gui.forgebound.repair.hint"), x + 10, y + 46, 0xFFBBBBBB, false);
			return;
		}

		ForgeClass cls = ForgeClass.fromProperty(ClientStats.get(StatsSyncPayload.CLASS));
		int forgeLevel = ClientStats.get(StatsSyncPayload.FORGE_LEVEL);
		Item ingredient = info.material().ingredient();
		int have = countInInventory(ingredient);

		context.drawText(textRenderer, stack.getName(), x + 32, y + 24, 0xFFFFFFFF, true);
		ItemQuality quality = stack.getOrDefault(ModComponents.QUALITY, ItemQuality.COMMON);
		Integer rgb = quality.color().getColorValue();
		int qualityColor = 0xFF000000 | (rgb == null ? 0xFFFFFF : rgb);
		context.drawText(textRenderer, Text.translatable("tooltip.forgebound.quality", Text.translatable(quality.translationKey()), quality.symbol()),
				x + 32, y + 35, qualityColor, false);

		int upgrade = stack.getOrDefault(ModComponents.UPGRADE, 0);
		context.drawText(textRenderer, Text.translatable("gui.forgebound.repair.upgrade_level", upgrade, BalanceConfig.UPGRADE_MAX), x + 10, y + 54, 0xFFFFD866, false);
		if (stack.getMaxDamage() > 0) {
			context.drawText(textRenderer, Text.translatable("gui.forgebound.repair.durability",
					stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage()), x + 10, y + 66, 0xFFFFFFFF, false);
		}

		context.drawText(textRenderer, Text.translatable("gui.forgebound.repair.have", have, ingredient.getName()), x + 10, y + 82, 0xFFBBBBBB, false);
		int next = upgrade + 1;
		if (next <= BalanceConfig.UPGRADE_MAX) {
			int required = GearCosts.requiredForgeLevel(next);
			int color = forgeLevel >= required ? 0xFF88DD88 : 0xFFFF8888;
			context.drawText(textRenderer, Text.translatable("gui.forgebound.repair.requires", required, forgeLevel), x + 10, y + 94, color, false);
		}
		context.drawText(textRenderer, Text.translatable("gui.forgebound.repair.hint"), x + 10, y + 112, 0xFF888888, false);
	}
}
