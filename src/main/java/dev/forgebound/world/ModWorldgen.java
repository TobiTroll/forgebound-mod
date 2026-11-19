package dev.forgebound.world;

import dev.forgebound.ForgeboundMod;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

/**
 * Weltgenerierung: Aurorit-Erz in der Overworld, tief unter der Erde und
 * deutlich seltener als Diamanten. Die Feature-Definition liegt als Datenpaket
 * unter data/forgebound/worldgen. Gilt nur fuer neu generierte Chunks.
 */
public final class ModWorldgen {
	private ModWorldgen() {
	}

	public static final RegistryKey<PlacedFeature> AURORIT_ORE_PLACED =
			RegistryKey.of(RegistryKeys.PLACED_FEATURE, ForgeboundMod.id("aurorit_ore"));

	public static void register() {
		BiomeModifications.addFeature(
				BiomeSelectors.foundInOverworld(),
				GenerationStep.Feature.UNDERGROUND_ORES,
				AURORIT_ORE_PLACED
		);
	}
}
