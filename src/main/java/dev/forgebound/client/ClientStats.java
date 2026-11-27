package dev.forgebound.client;

import dev.forgebound.network.ModPayloads.StatsSyncPayload;

/** Zuletzt vom Server empfangene Charakterdaten (nur Anzeige, der Server bleibt massgeblich). */
public final class ClientStats {
	private ClientStats() {
	}

	private static int[] values = new int[StatsSyncPayload.COUNT];

	public static void update(int[] newValues) {
		int[] copy = new int[StatsSyncPayload.COUNT];
		System.arraycopy(newValues, 0, copy, 0, Math.min(newValues.length, copy.length));
		values = copy;
	}

	public static int get(int index) {
		return index >= 0 && index < values.length ? values[index] : 0;
	}
}
