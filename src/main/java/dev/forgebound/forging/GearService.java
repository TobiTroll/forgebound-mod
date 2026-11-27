package dev.forgebound.forging;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.classes.ClassManager;
import dev.forgebound.classes.ForgeClass;
import dev.forgebound.item.ModComponents;
import dev.forgebound.item.ModItems;
import dev.forgebound.quality.ItemQuality;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

/** Serverseitiges Reparieren und Verbessern des geschmiedeten Stuecks in der Haupthand. */
public final class GearService {
	private GearService() {
	}

	/** Gibt null zurueck, wenn der Gegenstand kein geschmiedetes Stueck ist. */
	public static GearInfo info(ItemStack stack) {
		return stack.isEmpty() ? null : ModItems.info(stack.getItem());
	}

	public static int upgradeLevel(ItemStack stack) {
		return stack.getOrDefault(ModComponents.UPGRADE, 0);
	}

	public static void repair(ServerPlayerEntity player) {
		ItemStack stack = player.getMainHandStack();
		GearInfo info = info(stack);
		if (info == null) {
			player.sendMessage(Text.translatable("message.forgebound.repair_no_gear"), true);
			return;
		}
		if (!stack.isDamaged()) {
			player.sendMessage(Text.translatable("message.forgebound.repair_not_damaged"), true);
			return;
		}
		ForgeClass cls = ClassManager.get(player);
		int cost = GearCosts.repairCost(stack, info, cls);
		if (!ForgingService.consume(player, info.material().ingredient(), cost)) {
			player.sendMessage(Text.translatable("message.forgebound.repair_missing", cost, info.material().ingredient().getName()), true);
			return;
		}
		stack.setDamage(0);
		effects(player, false);
		player.sendMessage(Text.translatable("message.forgebound.repaired"), true);
	}

	public static void upgrade(ServerPlayerEntity player) {
		ItemStack stack = player.getMainHandStack();
		GearInfo info = info(stack);
		if (info == null) {
			player.sendMessage(Text.translatable("message.forgebound.repair_no_gear"), true);
			return;
		}
		int next = upgradeLevel(stack) + 1;
		if (next > BalanceConfig.UPGRADE_MAX) {
			player.sendMessage(Text.translatable("message.forgebound.upgrade_max"), true);
			return;
		}
		MinecraftServer server = player.getServer();
		int forgeLevel = server == null ? 1 : dev.forgebound.forging.ForgeProgressState.get(server).level(player.getUuid());
		int required = GearCosts.requiredForgeLevel(next);
		if (forgeLevel < required) {
			player.sendMessage(Text.translatable("message.forgebound.upgrade_level", required), true);
			return;
		}
		ForgeClass cls = ClassManager.get(player);
		int cost = GearCosts.upgradeCost(info, next, cls);
		if (!ForgingService.consume(player, info.material().ingredient(), cost)) {
			player.sendMessage(Text.translatable("message.forgebound.repair_missing", cost, info.material().ingredient().getName()), true);
			return;
		}
		ItemQuality quality = stack.getOrDefault(ModComponents.QUALITY, ItemQuality.COMMON);
		stack.set(ModComponents.UPGRADE, next);
		ForgingService.applyAttributes(stack, info.material(), info.gear(), quality, next);
		effects(player, true);
		player.sendMessage(Text.translatable("message.forgebound.upgraded", next), true);
	}

	private static void effects(ServerPlayerEntity player, boolean big) {
		ServerWorld world = player.getServerWorld();
		world.playSound(null, player.getBlockPos(), big ? SoundEvents.BLOCK_ANVIL_USE : SoundEvents.BLOCK_SMITHING_TABLE_USE,
				SoundCategory.BLOCKS, 1.0F, big ? 1.2F : 1.0F);
		world.spawnParticles(big ? ParticleTypes.ENCHANT : ParticleTypes.CRIT, player.getX(), player.getBodyY(0.7), player.getZ(),
				big ? 40 : 15, 0.4, 0.5, 0.4, 0.1);
	}
}
