package dev.forgebound.client;

import dev.forgebound.block.ModBlockEntities;
import dev.forgebound.screen.ModScreens;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ForgeboundClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreens.FORGING_HANDLER, ForgingScreen::new);
		BlockEntityRendererFactories.register(ModBlockEntities.FORGING_STATION, ForgingStationRenderer::new);
	}
}
