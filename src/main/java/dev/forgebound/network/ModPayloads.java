package dev.forgebound.network;

import dev.forgebound.ForgeboundMod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

/** Alle Netzwerk-Nachrichten der Mod. Der Server bleibt massgeblich, der Client schickt nur Wuensche. */
public final class ModPayloads {
	private ModPayloads() {
	}

	/** Server an Client: Charakter-, Klassen- und Schmiededaten des Spielers (fuer HUD und Menues). */
	public record StatsSyncPayload(int[] values) implements CustomPayload {
		public static final int LEVEL = 0;
		public static final int XP = 1;
		public static final int XP_NEEDED = 2;
		public static final int POINTS = 3;
		public static final int STR = 4;
		public static final int DUR = 5;
		public static final int SPD = 6;
		public static final int MANA_STAT = 7;
		public static final int MANA = 8;
		public static final int MAX_MANA = 9;
		public static final int FORGE_LEVEL = 10;
		public static final int FORGE_XP = 11;
		public static final int CLASS = 12;
		public static final int ADMIN = 13;
		public static final int DEX = 14;
		public static final int RES = 15;
		public static final int REG = 16;
		public static final int LUCK = 17;
		public static final int COUNT = 18;

		/** Position eines Stats (0 bis 7: Staerke, Durability, Speed, Mana, Geschick, Widerstand, Regeneration, Glueck) im Wertefeld. */
		public static int statSlot(int stat) {
			return stat < 4 ? STR + stat : DEX + (stat - 4);
		}

		public static final CustomPayload.Id<StatsSyncPayload> ID = new CustomPayload.Id<>(ForgeboundMod.id("stats_sync"));
		public static final PacketCodec<RegistryByteBuf, StatsSyncPayload> CODEC = PacketCodec.of(
				(payload, buf) -> {
					buf.writeVarInt(payload.values.length);
					for (int value : payload.values) {
						buf.writeVarInt(value);
					}
				},
				buf -> {
					int count = buf.readVarInt();
					int[] values = new int[count];
					for (int i = 0; i < count; i++) {
						values[i] = buf.readVarInt();
					}
					return new StatsSyncPayload(values);
				}
		);

		@Override
		public CustomPayload.Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	/** Server an Client: oeffnet das Operator-Menue. */
	public record OpenAdminPayload() implements CustomPayload {
		public static final CustomPayload.Id<OpenAdminPayload> ID = new CustomPayload.Id<>(ForgeboundMod.id("open_admin"));
		public static final PacketCodec<RegistryByteBuf, OpenAdminPayload> CODEC = PacketCodec.unit(new OpenAdminPayload());

		@Override
		public CustomPayload.Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	/** Client an Server: Stat-Punkte ausgeben (stat 0 bis 7). */
	public record AllocatePayload(int stat, int amount) implements CustomPayload {
		public static final CustomPayload.Id<AllocatePayload> ID = new CustomPayload.Id<>(ForgeboundMod.id("allocate"));
		public static final PacketCodec<RegistryByteBuf, AllocatePayload> CODEC = PacketCodec.tuple(
				PacketCodecs.VAR_INT, AllocatePayload::stat,
				PacketCodecs.VAR_INT, AllocatePayload::amount,
				AllocatePayload::new);

		@Override
		public CustomPayload.Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	/** Client an Server: Aktion im Operator-Menue. Der Server prueft die Berechtigung. */
	public record AdminActionPayload(int action, int a, int b) implements CustomPayload {
		public static final CustomPayload.Id<AdminActionPayload> ID = new CustomPayload.Id<>(ForgeboundMod.id("admin_action"));
		public static final PacketCodec<RegistryByteBuf, AdminActionPayload> CODEC = PacketCodec.tuple(
				PacketCodecs.VAR_INT, AdminActionPayload::action,
				PacketCodecs.VAR_INT, AdminActionPayload::a,
				PacketCodecs.VAR_INT, AdminActionPayload::b,
				AdminActionPayload::new);

		@Override
		public CustomPayload.Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
}
