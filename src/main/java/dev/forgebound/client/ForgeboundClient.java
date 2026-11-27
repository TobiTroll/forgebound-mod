package dev.forgebound.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import dev.forgebound.block.ModBlockEntities;
import dev.forgebound.screen.ModScreens;
import org.lwjgl.glfw.GLFW;

public class ForgeboundClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(ModScreens.FORGING_HANDLER, ForgingScreen::new);
		BlockEntityRendererFactories.register(ModBlockEntities.FORGING_STATION, ForgingStationRenderer::new);
		ClientNetworking.register();
		ManaHud.register();

		KeyBinding openCharacter = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.forgebound.character", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.categories.forgebound"));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openCharacter.wasPressed()) {
				if (client.currentScreen == null && client.player != null) {
					client.setScreen(new CharacterScreen());
				}
			}
		});
	}
}
