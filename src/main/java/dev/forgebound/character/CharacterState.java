package dev.forgebound.character;

import dev.forgebound.ForgeboundMod;
import dev.forgebound.balance.BalanceConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Dauerhafte Charakterdaten pro Spieler: Level, Erfahrung, Stat-Punkte, Stats und Mana.
 * Gespeichert im Welt-Zustand der Overworld (ueberlebt Tod, Dimensionswechsel und Neustart).
 * Aeltere Speicherstaende mit nur vier Stats werden beim Laden uebernommen.
 */
public class CharacterState extends PersistentState {
	/** Veraenderliche Daten eines Spielers. Nach Aenderungen CharacterState.markDirty() aufrufen. */
	public static class Data {
		public int level = 1;
		public int xp;
		public int points;
		/** Staerke, Durability, Speed, Mana, Geschick, Widerstand, Regeneration, Glueck. */
		public final int[] stats = new int[BalanceConfig.STAT_COUNT];
		public double curMana = BalanceConfig.BASE_MANA;

		public int allocated() {
			int sum = 0;
			for (int value : stats) {
				sum += value;
			}
			return sum;
		}
	}

	public static final PersistentState.Type<CharacterState> TYPE =
			new PersistentState.Type<>(CharacterState::new, CharacterState::fromNbt, null);

	private final Map<UUID, Data> players = new HashMap<>();

	public static CharacterState get(MinecraftServer server) {
		return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE, ForgeboundMod.MOD_ID + "_character");
	}

	public Data data(UUID id) {
		return players.computeIfAbsent(id, k -> new Data());
	}

	private static CharacterState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
		CharacterState state = new CharacterState();
		NbtCompound list = nbt.getCompound("players");
		for (String key : list.getKeys()) {
			try {
				UUID id = UUID.fromString(key);
				NbtCompound entry = list.getCompound(key);
				Data data = new Data();
				data.level = Math.max(1, entry.getInt("level"));
				data.xp = Math.max(0, entry.getInt("xp"));
				data.points = Math.max(0, entry.getInt("points"));
				int[] saved = entry.getIntArray("stats");
				if (saved.length > 0) {
					for (int i = 0; i < Math.min(saved.length, data.stats.length); i++) {
						data.stats[i] = Math.max(0, saved[i]);
					}
				} else {
					// Altes Format: nur Staerke, Durability, Speed und Mana
					data.stats[0] = Math.max(0, entry.getInt("str"));
					data.stats[1] = Math.max(0, entry.getInt("dur"));
					data.stats[2] = Math.max(0, entry.getInt("spd"));
					data.stats[3] = Math.max(0, entry.getInt("mana"));
				}
				data.curMana = entry.contains("curMana") ? entry.getDouble("curMana") : BalanceConfig.BASE_MANA;
				state.players.put(id, data);
			} catch (IllegalArgumentException ignored) {
				// ungueltiger Eintrag wird uebersprungen
			}
		}
		return state;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
		NbtCompound list = new NbtCompound();
		for (Map.Entry<UUID, Data> entry : players.entrySet()) {
			Data data = entry.getValue();
			NbtCompound tag = new NbtCompound();
			tag.putInt("level", data.level);
			tag.putInt("xp", data.xp);
			tag.putInt("points", data.points);
			tag.putIntArray("stats", data.stats);
			tag.putDouble("curMana", data.curMana);
			list.put(entry.getKey().toString(), tag);
		}
		nbt.put("players", list);
		return nbt;
	}
}
