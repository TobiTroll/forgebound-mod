package dev.forgebound.item;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.classes.ClassManager;
import dev.forgebound.classes.ForgeClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Erweckungsstein: Rechtsklick wuerfelt eine Klasse. Wer schon eine Klasse hat, muss den
 * Stein innerhalb von 10 Sekunden ein zweites Mal benutzen (die alte Klasse geht verloren,
 * die neue ist garantiert eine andere).
 */
public class AwakeningStoneItem extends Item {
	private static final Map<UUID, Long> CONFIRM = new HashMap<>();

	public AwakeningStoneItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (world.isClient || !(user instanceof ServerPlayerEntity player)) {
			return TypedActionResult.success(stack);
		}

		ForgeClass current = ClassManager.get(player);
		long now = world.getTime();
		if (current != null) {
			Long asked = CONFIRM.get(player.getUuid());
			if (asked == null || now - asked > BalanceConfig.AWAKEN_CONFIRM_TICKS) {
				CONFIRM.put(player.getUuid(), now);
				player.sendMessage(Text.translatable("message.forgebound.class_confirm",
						Text.translatable(current.translationKey()).formatted(current.rarity().color())), false);
				return TypedActionResult.fail(stack);
			}
			CONFIRM.remove(player.getUuid());
		}

		ForgeClass result = ClassManager.roll(player.getRandom(), current);
		ClassManager.set(player, result);
		if (!player.isCreative()) {
			stack.decrement(1);
		}
		player.getItemCooldownManager().set(this, 40);
		ClassManager.announce(player, result);
		return TypedActionResult.success(stack);
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		super.appendTooltip(stack, context, tooltip, type);
		tooltip.add(Text.translatable("tooltip.forgebound.awakening_stone.1").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("tooltip.forgebound.awakening_stone.2").formatted(Formatting.DARK_GRAY));
	}
}
