package dev.forgebound.block;

import com.mojang.serialization.MapCodec;
import dev.forgebound.forging.ForgingService;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
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
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient) {
			return ActionResult.SUCCESS;
		}
		// Herstellung laeuft ausschliesslich serverseitig.
		ForgingService.tryForgeIronSword(player);
		return ActionResult.CONSUME;
	}
}
