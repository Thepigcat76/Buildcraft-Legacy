package com.thepigcat.fancy_pipes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.thepigcat.fancy_pipes.content.blockentities.CrateBE;
import com.thepigcat.fancy_pipes.content.blocks.CrateBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CrateBERenderer implements BlockEntityRenderer<CrateBE> {
    public CrateBERenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CrateBE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = blockEntity.getItemHandler(null).getStackInSlot(0);

        Direction facing = blockEntity.getBlockState().getValue(CrateBlock.FACING);

        if (!stack.isEmpty()) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            BakedModel itemModel = itemRenderer.getModel(stack, null, null, 0);

            poseStack.pushPose();
            {
                poseStack.translate(.5f, 0, .5f);
                poseStack.mulPose((new Matrix4f()).rotateYXZ(getRotationYForSide2D(facing), 0, 0));
                poseStack.translate(-.5f, 0, -.5f);

                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.translate(0, 0, 0.51);
                poseStack.scale(1f, 1f, 0.001f);
                itemRenderer.render(stack, ItemDisplayContext.GUI, false, poseStack, bufferSource, LightTexture.FULL_BRIGHT,
                        packedOverlay, itemModel);
            }
            poseStack.popPose();
        }
    }

    private static final float[] sideRotationY2D = { 0, 0, 2, 0, 3, 1 };

    private float getRotationYForSide2D (Direction side) {
        return sideRotationY2D[side.ordinal()] * 90 * (float)Math.PI / 180f;
    }
}
