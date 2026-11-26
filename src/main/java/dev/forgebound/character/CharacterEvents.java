package dev.forgebound.character;

import dev.forgebound.balance.BalanceConfig;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.server.network.ServerPlayerEntity;

/** Ereignisse fuer das Charaktersystem: Anwenden der Stats, Mana-Regeneration, Kill-Erfahrung. */
public final class CharacterEvents {
	private CharacterEvents() {
	}

	public static void register() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			CharacterManager.apply(player);
			CharacterManager.sync(player);
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			CharacterManager.apply(newPlayer);
			CharacterManager.sync(newPlayer);
		});
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> CharacterManager.apply(player));

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTicks() % 20 != 0) {
				return;
			}
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				CharacterManager.tickMana(player);
				CharacterManager.sync(player);
			}
		});

		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
			if (entity instanceof ServerPlayerEntity player) {
				int xp = killXp(killedEntity);
				if (xp > 0) {
					CharacterManager.addXp(player, xp);
				}
			}
		});
	}

	/** Nur feindliche Mobs geben Erfahrung (Schutz vor Tierfarmen); Bosse geben einen festen Betrag. */
	private static int killXp(LivingEntity killed) {
		if (killed.getType() == EntityType.ENDER_DRAGON || killed.getType() == EntityType.WITHER) {
			return BalanceConfig.BOSS_XP;
		}
		if (!(killed instanceof Monster)) {
			return 0;
		}
		double xp = Math.ceil(killed.getMaxHealth() * BalanceConfig.KILL_XP_FACTOR);
		return (int) Math.min(BalanceConfig.KILL_XP_CAP, Math.max(1.0, xp));
	}
}
