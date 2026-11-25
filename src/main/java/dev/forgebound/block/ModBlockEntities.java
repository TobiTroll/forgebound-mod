package dev.forgebound.block;

import dev.forgebound.ForgeboundMod;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModBlockEntities {
	private ModBlockEntities() {
	}

	public static final BlockEntityType<ForgingStationBlockEntity> FORGING_STATION = Registry.register(
			Registries.BLOCK_ENTITY_TYPE,
			ForgeboundMod.id("forging_station"),
			FabricBlockEntityTypeBuilder.create(ForgingStationBlockEntity::new, ModBlocks.FORGING_STATION).build()
	);

	public static void register() {
		// Laedt die Klasse und damit die statische Registrierung.
	}
}
