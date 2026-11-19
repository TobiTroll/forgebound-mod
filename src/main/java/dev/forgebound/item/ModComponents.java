package dev.forgebound.item;

import com.mojang.serialization.Codec;
import dev.forgebound.ForgeboundMod;
import dev.forgebound.quality.ItemQuality;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.List;

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

	/** Traits als Liste von Texten der Form "id:stufe". Nur serverseitig gesetzt. */
	public static final ComponentType<List<String>> TRAITS = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			ForgeboundMod.id("traits"),
			ComponentType.<List<String>>builder()
					.codec(Codec.STRING.listOf())
					.packetCodec(PacketCodecs.STRING.collect(PacketCodecs.toList()))
					.build()
	);

	public static void register() {
		// Laedt die Klasse und damit die statischen Registrierungen.
	}
}
