package com.thepigcat.fancy_pipes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.api.capabilties.JumboItemHandler;
import com.thepigcat.fancy_pipes.content.blockentities.CrateBE;
import com.thepigcat.fancy_pipes.content.blocks.CrateBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class CrateBERenderer implements BlockEntityRenderer<CrateBE> {
    public CrateBERenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CrateBE blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        JumboItemHandler itemHandler = blockEntity.getItemHandler(null);
        ItemStack stack = itemHandler.getStackInSlot(0);

        Direction facing = blockEntity.getBlockState().getValue(CrateBlock.FACING);

        if (!stack.isEmpty()) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            Font font = Minecraft.getInstance().font;
            BakedModel itemModel = itemRenderer.getModel(stack, null, null, 0);

            poseStack.pushPose();
            {
                poseStack.translate(.5f, 0, .5f);
                poseStack.mulPose((new Matrix4f()).rotateYXZ(getRotationYForSide2D(facing), 0, 0));
                poseStack.translate(-.5f, 0, -.5f);

                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.translate(0, 0, 0.51);
                poseStack.scale(0.5f, 0.5f, 0.001f);
                itemRenderer.render(stack, ItemDisplayContext.GUI, false, poseStack, bufferSource, LightTexture.FULL_BRIGHT,
                        packedOverlay, itemModel);
            }
            poseStack.popPose();


            poseStack.pushPose();
            {
                poseStack.translate(.5f, 0, .5f);
                poseStack.mulPose((new Matrix4f()).rotateYXZ(getRotationYForSide2D(facing), 0, 0));
                poseStack.translate(-.5f, 0, -.5f);

                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                String text = String.valueOf(stack.getCount());
                float width = font.width(text) * 0.0175f / 2;
                poseStack.translate(0.5 - width, -0.92, -1.01);
                poseStack.scale(0.0175f, 0.0175f, 0.0175f);
                font.drawInBatch(text, 0, 0, FastColor.ARGB32.color(255, 255, 255), false,
                        poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL,
                        FastColor.ARGB32.color(0, 0, 0, 0), LightTexture.FULL_BRIGHT);
            }
            poseStack.popPose();
        }
    }

    private static final float[] sideRotationY2D = {0, 0, 2, 0, 3, 1};

    private float getRotationYForSide2D(Direction side) {
        return sideRotationY2D[side.ordinal()] * 90 * (float) Math.PI / 180f;
    }
}
