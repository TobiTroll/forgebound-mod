package dev.forgebound.client;

import dev.forgebound.network.ModPayloads.StatsSyncPayload;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

/**
 * Kleine Mana-Leiste ueber der Hungeranzeige (rechte Seite), mit Zahlen darueber.
 * Sie leert sich wie der Hunger von rechts nach links. Nur sichtbar, wenn Mana vorhanden ist.
 */
public final class ManaHud {
	private ManaHud() {
	}

	public static void register() {
		HudRenderCallback.EVENT.register((context, tickCounter) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.options.hudHidden || client.player == null || client.player.isSpectator()) {
				return;
			}
			int max = ClientStats.get(StatsSyncPayload.MAX_MANA);
			if (max <= 0) {
				return;
			}
			int current = Math.max(0, Math.min(max, ClientStats.get(StatsSyncPayload.MANA)));
			// Die Hungeranzeige liegt rechts der Hotbar-Mitte (Breite 81). Ueber den Luftblasen, ueber dem Hunger.
			int x = context.getScaledWindowWidth() / 2 + 10;
			int y = context.getScaledWindowHeight() - 60;
			int filled = (int) (81.0 * current / max);
			context.fill(x - 1, y - 1, x + 82, y + 6, 0xFF000000);
			context.fill(x, y, x + 81, y + 5, 0xFF14143C);
			context.fill(x + 81 - filled, y, x + 81, y + 5, 0xFF3A78FF);
			String text = current + "/" + max;
			context.drawText(client.textRenderer, text, x + 81 - client.textRenderer.getWidth(text), y - 10, 0xFF7FA8FF, true);
		});
	}
}
