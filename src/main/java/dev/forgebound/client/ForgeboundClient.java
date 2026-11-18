package dev.forgebound.client;

import dev.forgebound.screen.ModScreens;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ForgeboundClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreens.FORGING_HANDLER, ForgingScreen::new);
	}
}
