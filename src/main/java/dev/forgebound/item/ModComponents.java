package dev.forgebound.item;

import dev.forgebound.ForgeboundMod;
import dev.forgebound.quality.ItemQuality;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModComponents {
	private ModComponents() {
	}

	/** Qualitaet eines Gegenstands. Wird nur serverseitig gesetzt und per Packet-Codec zum Client synchronisiert. */
	public static final ComponentType<ItemQuality> QUALITY = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			ForgeboundMod.id("quality"),
			ComponentType.<ItemQuality>builder()
					.codec(ItemQuality.CODEC)
					.packetCodec(ItemQuality.PACKET_CODEC)
					.build()
	);

	public static void register() {
		// Laedt die Klasse und damit die statischen Registrierungen.
	}
}
