package dev.forgebound;

import dev.forgebound.block.ModBlocks;
import dev.forgebound.item.ModComponents;
import dev.forgebound.item.ModItems;
import dev.forgebound.screen.ModScreens;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgeboundMod implements ModInitializer {
	public static final String MOD_ID = "forgebound";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		// Reihenfolge: Datenkomponenten, Bloecke, Items, Menues
		ModComponents.register();
		ModBlocks.register();
		ModItems.register();
		ModScreens.register();
		LOGGER.info("Forgebound geladen");
	}
}
