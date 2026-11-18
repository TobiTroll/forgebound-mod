package dev.forgebound.block;

import dev.forgebound.ForgeboundMod;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlocks {
	private ModBlocks() {
	}

	public static final Block FORGING_STATION = Registry.register(
			Registries.BLOCK,
			ForgeboundMod.id("forging_station"),
			new ForgingStationBlock(AbstractBlock.Settings.copy(Blocks.ANVIL))
	);

	public static void register() {
		// Laedt die Klasse und damit die statischen Registrierungen.
	}
}
