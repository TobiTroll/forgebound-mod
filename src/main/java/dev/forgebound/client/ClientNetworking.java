package dev.forgebound.client;

import dev.forgebound.network.ModPayloads.OpenAdminPayload;
import dev.forgebound.network.ModPayloads.StatsSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/** Client-Empfaenger der Server-Nachrichten. */
public final class ClientNetworking {
	private ClientNetworking() {
	}

	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(StatsSyncPayload.ID,
				(payload, context) -> ClientStats.update(payload.values()));
		ClientPlayNetworking.registerGlobalReceiver(OpenAdminPayload.ID,
				(payload, context) -> context.client().setScreen(new AdminScreen()));
	}
}
