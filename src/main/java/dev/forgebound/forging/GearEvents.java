package dev.forgebound.forging;

import dev.forgebound.block.ModBlocks;
import dev.forgebound.screen.RepairScreenHandler;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

/**
 * Ereignisse rund um geschmiedete Ausruestung: Schleichen + Rechtsklick auf die Schmiedestation
 * mit einem geschmiedeten Stueck in der Hand oeffnet das Reparatur- und Verbesserungsmenue;
 * die Relikt-Waffe Seelendurst heilt bei Kills.
 */
public final class GearEvents {
	private GearEvents() {
	}

	public static void register() {
		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			if (hand != Hand.MAIN_HAND || !player.isSneaking() || player.isSpectator()) {
				return ActionResult.PASS;
			}
			BlockPos pos = hit.getBlockPos();
			if (!world.getBlockState(pos).isOf(ModBlocks.FORGING_STATION)) {
				return ActionResult.PASS;
			}
			if (GearService.info(player.getMainHandStack()) == null) {
				return ActionResult.PASS;
			}
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
				serverPlayer.openHandledScreen(new SimpleNamedScreenHandlerFactory(
						(syncId, inventory, p) -> new RepairScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)),
						Text.translatable("gui.forgebound.repair.title")));
			}
			return ActionResult.SUCCESS;
		});

		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
			if (entity instanceof ServerPlayerEntity player) {
				ItemStack main = player.getMainHandStack();
				if (Relic.byId(main.get(dev.forgebound.item.ModComponents.RELIC)) == Relic.SOULTHIRST) {
					player.heal(6.0F);
				}
			}
		});
	}
}
