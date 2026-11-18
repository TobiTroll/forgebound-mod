package dev.forgebound.block;

import com.mojang.serialization.MapCodec;
import dev.forgebound.screen.ForgingScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ForgingStationBlock extends Block {
	public static final MapCodec<ForgingStationBlock> CODEC = createCodec(ForgingStationBlock::new);

	public ForgingStationBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected MapCodec<? extends Block> getCodec() {
		return CODEC;
	}

	@Override
	protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
		return new SimpleNamedScreenHandlerFactory(
				(syncId, inventory, player) -> new ForgingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos)),
				Text.translatable("container.forgebound.forging_station")
		);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (!world.isClient) {
			NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);
			if (factory != null) {
				player.openHandledScreen(factory);
			}
		}
		return ActionResult.SUCCESS;
	}
}
