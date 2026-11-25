package dev.forgebound.client;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.block.ForgingStationBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

/** Zeigt das fertig geschmiedete Stueck schwebend und rotierend ueber dem Amboss. */
public class ForgingStationRenderer implements BlockEntityRenderer<ForgingStationBlockEntity> {
	private final ItemRenderer itemRenderer;

	public ForgingStationRenderer(BlockEntityRendererFactory.Context context) {
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(ForgingStationBlockEntity entity, float tickDelta, MatrixStack matrices,
					   VertexConsumerProvider vertexConsumers, int light, int overlay) {
		ItemStack stack = entity.getOutput();
		if (stack.isEmpty() || entity.getWorld() == null) {
			return;
		}
		float age = entity.getWorld().getTime() - entity.getRevealStart() + tickDelta;
		if (age < BalanceConfig.REVEAL_ITEM_TICKS) {
			return;
		}
		float sinceAppear = age - BalanceConfig.REVEAL_ITEM_TICKS;
		float appear = Math.min(1.0F, sinceAppear / 10.0F);
		float bob = (float) Math.sin(age * 0.1F) * 0.06F;

		matrices.push();
		matrices.translate(0.5, 1.35 + bob + (1.0F - appear) * 0.4F, 0.5);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(age * 3.0F));
		float scale = 1.1F * appear;
		matrices.scale(scale, scale, scale);
		int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
		itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV,
				matrices, vertexConsumers, entity.getWorld(), 0);
		matrices.pop();
	}
}
