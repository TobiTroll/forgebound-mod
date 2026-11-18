package dev.forgebound.screen;

import dev.forgebound.ForgeboundMod;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

public final class ModScreens {
	private ModScreens() {
	}

	public static final ScreenHandlerType<ForgingScreenHandler> FORGING_HANDLER = Registry.register(
			Registries.SCREEN_HANDLER,
			ForgeboundMod.id("forging"),
			new ScreenHandlerType<>(ForgingScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
	);

	public static void register() {
		// Laedt die Klasse und damit die statische Registrierung.
	}
}
