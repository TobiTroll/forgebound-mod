package dev.forgebound.classes;

import dev.forgebound.balance.BalanceConfig;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Ereignisse, die Klassen-Passive ausloesen. Alles laeuft serverseitig:
 * Attribute beim Beitritt, Respawn und Dimensionswechsel anwenden, Sekunden-Tick,
 * Kills, Erzabbau, Feuerimmunitaet, Ausweichen und die Phoenix-Wiedergeburt.
 */
public final class ClassEvents {
	private ClassEvents() {
	}

	public static void register() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ClassManager.apply(handler.getPlayer()));
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> ClassManager.apply(newPlayer));
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> ClassManager.apply(player));

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTicks() % 20 != 0) {
				return;
			}
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				ForgeClass cls = ClassManager.get(player);
				if (cls != null) {
					tickPassives(player, cls);
				}
			}
		});

		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
			if (!(entity instanceof ServerPlayerEntity player)) {
				return;
			}
			ForgeClass cls = ClassManager.get(player);
			if (cls == ForgeClass.WARRIOR) {
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 100, 0));
			} else if (cls == ForgeClass.BERSERKER) {
				player.heal(4.0F);
			}
		});

		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (!(world instanceof ServerWorld serverWorld) || !(player instanceof ServerPlayerEntity serverPlayer)) {
				return;
			}
			if (ClassManager.get(serverPlayer) != ForgeClass.MINER || !(state.getBlock() instanceof ExperienceDroppingBlock)) {
				return;
			}
			if (serverPlayer.getRandom().nextDouble() >= BalanceConfig.MINER_DOUBLE_DROP_CHANCE) {
				return;
			}
			for (ItemStack drop : Block.getDroppedStacks(state, serverWorld, pos, blockEntity, serverPlayer, serverPlayer.getMainHandStack())) {
				// Mit Behutsamkeit abgebaute Erzbloecke werden nicht verdoppelt.
				if (!drop.isOf(state.getBlock().asItem())) {
					Block.dropStack(serverWorld, pos, drop);
				}
			}
		});

		// Feuerimmunitaet (Phoenix, Essenhueter) und Ausweichen (Assassine, Duellant, Leerenwandler)
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (!(entity instanceof ServerPlayerEntity player)) {
				return true;
			}
			ForgeClass cls = ClassManager.get(player);
			if (cls == null) {
				return true;
			}
			if (cls.isFireImmune() && source.isIn(DamageTypeTags.IS_FIRE)) {
				player.extinguish();
				return false;
			}
			if (source.getAttacker() == null || cls.dodgeChance() <= 0.0 || player.getRandom().nextDouble() >= cls.dodgeChance()) {
				return true;
			}
			ServerWorld world = player.getServerWorld();
			world.spawnParticles(ParticleTypes.POOF, player.getX(), player.getBodyY(0.5), player.getZ(), 8, 0.3, 0.5, 0.3, 0.02);
			world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, SoundCategory.PLAYERS, 1.0F, 1.2F);
			player.sendMessage(Text.translatable("message.forgebound.dodged"), true);
			return false;
		});

		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
			if (!(entity instanceof ServerPlayerEntity player) || ClassManager.get(player) != ForgeClass.PHOENIX) {
				return true;
			}
			if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
				return true;
			}
			ServerWorld world = player.getServerWorld();
			long now = world.getTime();
			if (now < ClassManager.phoenixReadyAt(player.getUuid())) {
				return true;
			}
			ClassManager.setPhoenixReadyAt(player.getUuid(), now + BalanceConfig.PHOENIX_COOLDOWN_TICKS);
			player.setHealth(6.0F);
			player.clearStatusEffects();
			player.extinguish();
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 160, 1));
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 1));
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200, 0));
			world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getBodyY(0.6), player.getZ(), 60, 0.5, 0.9, 0.5, 0.4);
			world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
			player.sendMessage(Text.translatable("message.forgebound.rebirth"), true);
			return false;
		});
	}

	/** Einmal pro Sekunde fuer jeden Spieler mit Klasse. */
	private static void tickPassives(ServerPlayerEntity player, ForgeClass cls) {
		if (cls.isFireImmune() && player.isOnFire()) {
			player.extinguish();
		}
		switch (cls) {
			case GUARDIAN -> {
				if (player.getAbsorptionAmount() < 2.0F && player.getServerWorld().getTime() % 600 < 20) {
					player.setAbsorptionAmount(2.0F);
				}
			}
			case ASSASSIN -> {
				if (player.isSneaking()) {
					player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 40, 0, false, false, false));
				}
			}
			case EXPLORER -> {
				World world = player.getWorld();
				BlockPos pos = player.getBlockPos();
				if (!world.isSkyVisible(pos) && world.getLightLevel(pos) < 6) {
					player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, false, false, false));
				}
			}
			case BERSERKER -> {
				if (player.getHealth() <= 6.0F) {
					player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 60, 0, false, false, false));
				}
			}
			default -> {
			}
		}
	}
}
