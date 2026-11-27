package dev.forgebound.screen;

import dev.forgebound.block.ModBlocks;
import dev.forgebound.forging.GearService;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Reparatur- und Verbesserungsmenue. Arbeitet auf dem geschmiedeten Stueck in der Haupthand.
 * Der Client zeigt nur Kosten und Zustand an; Verbrauch, Reparatur und Verbesserung
 * passieren ausschliesslich auf dem Server (GearService).
 */
public class RepairScreenHandler extends ScreenHandler {
	public static final int BTN_REPAIR = 1;
	public static final int BTN_UPGRADE = 2;
	public static final int BTN_BACK = 3;

	private final ScreenHandlerContext context;

	/** Client-Konstruktor. */
	public RepairScreenHandler(int syncId, PlayerInventory inventory) {
		this(syncId, inventory, ScreenHandlerContext.EMPTY);
	}

	/** Server-Konstruktor. */
	public RepairScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
		super(ModScreens.REPAIR_HANDLER, syncId);
		this.context = context;
	}

	@Override
	public boolean onButtonClick(PlayerEntity player, int id) {
		if (player.getWorld().isClient || !(player instanceof ServerPlayerEntity serverPlayer)) {
			return false;
		}
		switch (id) {
			case BTN_REPAIR -> GearService.repair(serverPlayer);
			case BTN_UPGRADE -> GearService.upgrade(serverPlayer);
			case BTN_BACK -> context.run((world, pos) -> serverPlayer.openHandledScreen(new SimpleNamedScreenHandlerFactory(
					(syncId, inventory, p) -> new ForgingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)),
					Text.translatable("container.forgebound.forging_station"))));
			default -> {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack quickMove(PlayerEntity player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return canUse(context, player, ModBlocks.FORGING_STATION);
	}
}
