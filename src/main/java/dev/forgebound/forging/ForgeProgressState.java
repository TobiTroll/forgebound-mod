package dev.forgebound.forging;

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
 * Dauerhafter Schmiedefortschritt pro Spieler (Level und Erfahrung), gespeichert
 * im Welt-Zustand der Overworld. Ueberlebt Tod, Dimensionswechsel und Neustart.
 */
public class ForgeProgressState extends PersistentState {
	public static final PersistentState.Type<ForgeProgressState> TYPE =
			new PersistentState.Type<>(ForgeProgressState::new, ForgeProgressState::fromNbt, null);

	private final Map<UUID, int[]> players = new HashMap<>();

	public static ForgeProgressState get(MinecraftServer server) {
		return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE, ForgeboundMod.MOD_ID + "_progress");
	}

	private static ForgeProgressState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
		ForgeProgressState state = new ForgeProgressState();
		NbtCompound list = nbt.getCompound("players");
		for (String key : list.getKeys()) {
			try {
				UUID id = UUID.fromString(key);
				NbtCompound entry = list.getCompound(key);
				state.players.put(id, new int[]{Math.max(1, entry.getInt("level")), Math.max(0, entry.getInt("xp"))});
			} catch (IllegalArgumentException ignored) {
				// ungueltiger Eintrag wird uebersprungen
			}
		}
		return state;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
		NbtCompound list = new NbtCompound();
		for (Map.Entry<UUID, int[]> entry : players.entrySet()) {
			NbtCompound data = new NbtCompound();
			data.putInt("level", entry.getValue()[0]);
			data.putInt("xp", entry.getValue()[1]);
			list.put(entry.getKey().toString(), data);
		}
		nbt.put("players", list);
		return nbt;
	}

	public int level(UUID id) {
		int[] p = players.get(id);
		return p == null ? 1 : p[0];
	}

	public int xp(UUID id) {
		int[] p = players.get(id);
		return p == null ? 0 : p[1];
	}

	/** Setzt das Schmiedelevel direkt (z. B. durch das Operator-Menue); Erfahrung beginnt bei 0. */
	public void setLevel(UUID id, int level) {
		int clamped = Math.max(1, Math.min(BalanceConfig.MAX_LEVEL, level));
		players.put(id, new int[]{clamped, 0});
		markDirty();
	}

	/** Fuegt Erfahrung hinzu und gibt die Anzahl neu erreichter Level zurueck. */
	public int addXp(UUID id, int amount) {
		int[] p = players.computeIfAbsent(id, k -> new int[]{1, 0});
		p[1] += amount;
		int gained = 0;
		while (p[0] < BalanceConfig.MAX_LEVEL && p[1] >= BalanceConfig.xpForLevel(p[0])) {
			p[1] -= BalanceConfig.xpForLevel(p[0]);
			p[0]++;
			gained++;
		}
		if (p[0] >= BalanceConfig.MAX_LEVEL) {
			p[1] = 0;
		}
		markDirty();
		return gained;
	}
}
