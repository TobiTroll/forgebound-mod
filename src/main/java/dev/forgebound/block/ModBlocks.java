package dev.forgebound.block;

import dev.forgebound.ForgeboundMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public final class ModBlocks {
	private ModBlocks() {
	}

	public static final Block FORGING_STATION = Registry.register(
			Registries.BLOCK,
			ForgeboundMod.id("forging_station"),
			new ForgingStationBlock(AbstractBlock.Settings.copy(Blocks.ANVIL).nonOpaque())
	);

	public static final Block AURORIT_ORE = Registry.register(
			Registries.BLOCK,
			ForgeboundMod.id("aurorit_ore"),
			new ExperienceDroppingBlock(UniformIntProvider.create(5, 10),
					AbstractBlock.Settings.copy(Blocks.DIAMOND_ORE).strength(4.0F, 4.0F))
	);

	public static final Block DEEPSLATE_AURORIT_ORE = Registry.register(
			Registries.BLOCK,
			ForgeboundMod.id("deepslate_aurorit_ore"),
			new ExperienceDroppingBlock(UniformIntProvider.create(5, 10),
					AbstractBlock.Settings.copy(Blocks.DEEPSLATE_DIAMOND_ORE).strength(5.5F, 4.0F))
	);

	public static void register() {
		// Laedt die Klasse und damit die statischen Registrierungen.
	}
}
