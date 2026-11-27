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

	/** Verbesserungsstufe (0 bis UPGRADE_MAX). Wird an der Schmiedestation erhoeht. */
	public static final ComponentType<Integer> UPGRADE = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			ForgeboundMod.id("upgrade"),
			ComponentType.<Integer>builder()
					.codec(Codec.INT)
					.packetCodec(PacketCodecs.VAR_INT)
					.build()
	);

	/** Kennung des benannten Reliktes (siehe Relic). Nur bei Relikt-Waffen vorhanden. */
	public static final ComponentType<String> RELIC = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			ForgeboundMod.id("relic"),
			ComponentType.<String>builder()
					.codec(Codec.STRING)
					.packetCodec(PacketCodecs.STRING)
					.build()
	);

	public static void register() {
		// Laedt die Klasse und damit die statischen Registrierungen.
	}
}
