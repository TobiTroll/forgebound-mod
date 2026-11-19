package dev.forgebound.block;

import com.mojang.serialization.MapCodec;
import dev.forgebound.screen.ForgingScreenHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/** Schmiedestation in Amboss-Form. Gedreht wie ein Amboss, oeffnet das Schmiedemenue. */
public class ForgingStationBlock extends HorizontalFacingBlock {
	public static final MapCodec<ForgingStationBlock> CODEC = createCodec(ForgingStationBlock::new);

	private static final VoxelShape BASE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
	private static final VoxelShape X_SHAPE = VoxelShapes.union(
			BASE,
			Block.createCuboidShape(3.0, 4.0, 4.0, 13.0, 5.0, 12.0),
			Block.createCuboidShape(4.0, 5.0, 6.0, 12.0, 10.0, 10.0),
			Block.createCuboidShape(0.0, 10.0, 3.0, 16.0, 16.0, 13.0)
	);
	private static final VoxelShape Z_SHAPE = VoxelShapes.union(
			BASE,
			Block.createCuboidShape(4.0, 4.0, 3.0, 12.0, 5.0, 13.0),
			Block.createCuboidShape(6.0, 5.0, 4.0, 10.0, 10.0, 12.0),
			Block.createCuboidShape(3.0, 10.0, 0.0, 13.0, 16.0, 16.0)
	);

	public ForgingStationBlock(Settings settings) {
		super(settings);
		setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
		return CODEC;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return getDefaultState().with(FACING, context.getHorizontalPlayerFacing().rotateYClockwise());
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return state.get(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
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
