package dev.forgebound.character;

import dev.forgebound.network.ModPayloads.OpenAdminPayload;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;

/** /forge oeffnet das Operator-Menue (nur mit Berechtigungsstufe 2, z. B. /op). */
public final class AdminCommands {
	private AdminCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal("forge")
						.requires(source -> source.hasPermissionLevel(2))
						.executes(context -> {
							ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
							CharacterManager.sync(player);
							ServerPlayNetworking.send(player, new OpenAdminPayload());
							return 1;
						})));
	}
}
