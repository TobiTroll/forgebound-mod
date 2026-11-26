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
 */
public class CharacterState extends PersistentState {
	/** Veraenderliche Daten eines Spielers. Nach Aenderungen CharacterState.markDirty() aufrufen. */
	public static class Data {
		public int level = 1;
		public int xp;
		public int points;
		public int str;
		public int dur;
		public int spd;
		public int mana;
		public double curMana = BalanceConfig.BASE_MANA;

		public int allocated() {
			return str + dur + spd + mana;
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
				data.str = Math.max(0, entry.getInt("str"));
				data.dur = Math.max(0, entry.getInt("dur"));
				data.spd = Math.max(0, entry.getInt("spd"));
				data.mana = Math.max(0, entry.getInt("mana"));
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
			tag.putInt("str", data.str);
			tag.putInt("dur", data.dur);
			tag.putInt("spd", data.spd);
			tag.putInt("mana", data.mana);
			tag.putDouble("curMana", data.curMana);
			list.put(entry.getKey().toString(), tag);
		}
		nbt.put("players", list);
		return nbt;
	}
}
