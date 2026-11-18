package dev.forgebound.quality;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Formatting;

/**
 * Qualitaets- bzw. Rangstufen. Ein hoeherer Rang ist Fortschrittsidee,
 * keine Garantie fuer staerkere Werte. Die Anzeige nutzt Text und Symbol,
 * nicht nur Farbe.
 */
public enum ItemQuality {
	COMMON("common", "\u00b7", Formatting.GRAY),
	RARE("rare", "\u25c6", Formatting.AQUA),
	EPIC("epic", "\u25c6\u25c6", Formatting.LIGHT_PURPLE),
	UNIQUE("unique", "\u2605", Formatting.YELLOW),
	LEGENDARY("legendary", "\u2605\u2605", Formatting.GOLD),
	MYTHIC("mythic", "\u2605\u2605\u2605", Formatting.RED);

	public static final Codec<ItemQuality> CODEC = Codec.STRING.xmap(ItemQuality::fromId, ItemQuality::id);
	public static final PacketCodec<ByteBuf, ItemQuality> PACKET_CODEC = PacketCodecs.STRING.xmap(ItemQuality::fromId, ItemQuality::id);

	private final String id;
	private final String symbol;
	private final Formatting color;

	ItemQuality(String id, String symbol, Formatting color) {
		this.id = id;
		this.symbol = symbol;
		this.color = color;
	}

	public String id() {
		return id;
	}

	public String symbol() {
		return symbol;
	}

	public Formatting color() {
		return color;
	}

	public String translationKey() {
		return "quality.forgebound." + id;
	}

	public static ItemQuality fromId(String id) {
		for (ItemQuality quality : values()) {
			if (quality.id.equals(id)) {
				return quality;
			}
		}
		return COMMON;
	}
}
