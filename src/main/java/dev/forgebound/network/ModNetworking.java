package dev.forgebound.network;

import dev.forgebound.character.AdminActions;
import dev.forgebound.character.CharacterManager;
import dev.forgebound.network.ModPayloads.AdminActionPayload;
import dev.forgebound.network.ModPayloads.AllocatePayload;
import dev.forgebound.network.ModPayloads.OpenAdminPayload;
import dev.forgebound.network.ModPayloads.StatsSyncPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/** Registriert Nachrichtentypen (beide Seiten) und die Server-Empfaenger. */
public final class ModNetworking {
	private ModNetworking() {
	}

	public static void register() {
		PayloadTypeRegistry.playS2C().register(StatsSyncPayload.ID, StatsSyncPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(OpenAdminPayload.ID, OpenAdminPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(AllocatePayload.ID, AllocatePayload.CODEC);
		PayloadTypeRegistry.playC2S().register(AdminActionPayload.ID, AdminActionPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(AllocatePayload.ID,
				(payload, context) -> CharacterManager.allocate(context.player(), payload.stat(), payload.amount()));
		ServerPlayNetworking.registerGlobalReceiver(AdminActionPayload.ID,
				(payload, context) -> AdminActions.handle(context.player(), payload));
	}
}
