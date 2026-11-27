package dev.forgebound;

import dev.forgebound.block.ModBlockEntities;
import dev.forgebound.block.ModBlocks;
import dev.forgebound.character.AdminCommands;
import dev.forgebound.character.CharacterEvents;
import dev.forgebound.classes.ClassCommands;
import dev.forgebound.classes.ClassEvents;
import dev.forgebound.forging.GearEvents;
import dev.forgebound.item.ModComponents;
import dev.forgebound.item.ModItems;
import dev.forgebound.network.ModNetworking;
import dev.forgebound.screen.ModScreens;
import dev.forgebound.world.ModWorldgen;
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
		// Reihenfolge: Datenkomponenten, Bloecke, Block-Entities, Items, Menues, Weltgenerierung, Netzwerk, Klassen, Charakter, Ausruestung
		ModComponents.register();
		ModBlocks.register();
		ModBlockEntities.register();
		ModItems.register();
		ModScreens.register();
		ModWorldgen.register();
		ModNetworking.register();
		ClassEvents.register();
		ClassCommands.register();
		CharacterEvents.register();
		AdminCommands.register();
		GearEvents.register();
		LOGGER.info("Forgebound geladen");
	}
}
