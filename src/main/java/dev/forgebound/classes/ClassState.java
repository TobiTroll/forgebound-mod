package dev.forgebound.classes;

import dev.forgebound.ForgeboundMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Dauerhafte Klassenwahl pro Spieler, gespeichert im Welt-Zustand der Overworld.
 * Ueberlebt Tod, Dimensionswechsel und Neustart.
 */
public class ClassState extends PersistentState {
	public static final PersistentState.Type<ClassState> TYPE =
			new PersistentState.Type<>(ClassState::new, ClassState::fromNbt, null);

	private final Map<UUID, String> classes = new HashMap<>();

	public static ClassState get(MinecraftServer server) {
		return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE, ForgeboundMod.MOD_ID + "_classes");
	}

	private static ClassState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
		ClassState state = new ClassState();
		NbtCompound list = nbt.getCompound("players");
		for (String key : list.getKeys()) {
			try {
				state.classes.put(UUID.fromString(key), list.getString(key));
			} catch (IllegalArgumentException ignored) {
				// ungueltiger Eintrag wird uebersprungen
			}
		}
		return state;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
		NbtCompound list = new NbtCompound();
		for (Map.Entry<UUID, String> entry : classes.entrySet()) {
			list.putString(entry.getKey().toString(), entry.getValue());
		}
		nbt.put("players", list);
		return nbt;
	}

	/** Gibt null zurueck, wenn der Spieler keine (oder eine unbekannte) Klasse hat. */
	public ForgeClass get(UUID id) {
		String raw = classes.get(id);
		return raw == null ? null : ForgeClass.byId(raw);
	}

	public void set(UUID id, ForgeClass cls) {
		if (cls == null) {
			classes.remove(id);
		} else {
			classes.put(id, cls.id());
		}
		markDirty();
	}
}
