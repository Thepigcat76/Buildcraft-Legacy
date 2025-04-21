package com.thepigcat.buildcraft.client.blockentities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.portingdeadmods.portingdeadlibs.utils.renderers.PDLRenderTypes;
import com.thepigcat.buildcraft.BCConfig;
import com.thepigcat.buildcraft.content.blockentities.TankBE;
import com.thepigcat.buildcraft.content.blocks.TankBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix4f;

public class TankBERenderer implements BlockEntityRenderer<TankBE> {
    private static final float SIDE_MARGIN = (float) TankBlock.SHAPE.min(Direction.Axis.X) + 0.01f;
    private static final float MIN_Y = 0;

    public TankBERenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TankBE entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (entity.getBottomTankPos() != null) {
            FluidStack fluidStack = entity.getFluidHandler().getFluidInTank(0);
            int tank = entity.getBlockPos().getY() - entity.getBottomTankPos().getY();
            int prevFluidAmount = tank * BCConfig.tankCapacity;
            int fluidAmount = Math.min(fluidStack.getAmount() - prevFluidAmount, BCConfig.tankCapacity);
            int nextFluidAmount = Math.min(fluidStack.getAmount() - (prevFluidAmount + BCConfig.tankCapacity), BCConfig.tankCapacity);
            renderTankContents(fluidStack, fluidAmount, BCConfig.tankCapacity, poseStack, bufferSource, combinedLight, entity.isBottomJoined(), entity.isTopJoined()
                    && fluidAmount == BCConfig.tankCapacity
                    && nextFluidAmount > 0);

            //renderErrorBlock(poseStack, entity.getBottomTankPos().subtract(entity.getBlockPos()), bufferSource);
        }
    }

    private static void renderErrorBlock(PoseStack poseStack, BlockPos blockPos, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        int r = 255;
        int g = 0;
        int b = 0;
        int a = 100;
        poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        VertexConsumer consumer = bufferSource.getBuffer(PDLRenderTypes.SIMPLE_SOLID);
        Matrix4f matrix = poseStack.last().pose();
        renderCube(consumer, matrix, r, g, b, a);
        poseStack.popPose();
    }

    private static void renderCube(VertexConsumer consumer, Matrix4f matrix, int r, int g, int b, int a) {
        consumer.addVertex(matrix, -0.0F, 1.0F, -0.0F).setColor(r, g, b, a).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, -0.0F, 1.0F, 1.0F).setColor(r, g, b, a).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, 1.0F, 1.0F, 1.0F).setColor(r, g, b, a).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, 1.0F, 1.0F, -0.0F).setColor(r, g, b, a).setNormal(0.0F, 1.0F, 0.0F);
        consumer.addVertex(matrix, -0.0F, -0.0F, -0.0F).setColor(r, g, b, a).setNormal(0.0F, -1.0F, 0.0F);
        consumer.addVertex(matrix, 1.0F, -0.0F, -0.0F).setColor(r, g, b, a).setNormal(0.0F, -1.0F, 0.0F);
        consumer.addVertex(matrix, 1.0F, -0.0F, 1.0F).setColor(r, g, b, a).setNormal(0.0F, -1.0F, 0.0F);
        consumer.addVertex(matrix, -0.0F, -0.0F, 1.0F).setColor(r, g, b, a).setNormal(0.0F, -1.0F, 0.0F);
        consumer.addVertex(matrix, -0.0F, -0.0F, 1.0F).setColor(r, g, b, a).setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, 1.0F, -0.0F, 1.0F).setColor(r, g, b, a).setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, 1.0F, 1.0F, 1.0F).setColor(r, g, b, a).setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, -0.0F, 1.0F, 1.0F).setColor(r, g, b, a).setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, -0.0F, -0.0F, -0.0F).setColor(r, g, b, a).setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(matrix, -0.0F, 1.0F, -0.0F).setColor(r, g, b, a).setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(matrix, 1.0F, 1.0F, -0.0F).setColor(r, g, b, a).setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(matrix, 1.0F, -0.0F, -0.0F).setColor(r, g, b, a).setNormal(0.0F, 0.0F, -1.0F);
        consumer.addVertex(matrix, -0.0F, -0.0F, -0.0F).setColor(r, g, b, a).setNormal(-1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, -0.0F, -0.0F, 1.0F).setColor(r, g, b, a).setNormal(-1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, -0.0F, 1.0F, 1.0F).setColor(r, g, b, a).setNormal(-1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, -0.0F, 1.0F, -0.0F).setColor(r, g, b, a).setNormal(-1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, 1.0F, -0.0F, -0.0F).setColor(r, g, b, a).setNormal(1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, 1.0F, 1.0F, -0.0F).setColor(r, g, b, a).setNormal(1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, 1.0F, 1.0F, 1.0F).setColor(r, g, b, a).setNormal(1.0F, 0.0F, 0.0F);
        consumer.addVertex(matrix, 1.0F, -0.0F, 1.0F).setColor(r, g, b, a).setNormal(1.0F, 0.0F, 0.0F);
    }

    public static void renderTankContents(FluidStack fluidStack, int amount, int capacity, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, boolean bottomJoined, boolean topJoined) {
        if (fluidStack.isEmpty() || amount <= 0)
            return;

        float fillPercentage = Math.min(1, (float) amount / capacity);
        if (fluidStack.getFluid().getFluidType().isLighterThanAir())
            renderFluid(poseStack, bufferSource, fluidStack, fillPercentage, 1, combinedLight, bottomJoined, topJoined);
        else
            renderFluid(poseStack, bufferSource, fluidStack, 1, fillPercentage, combinedLight, bottomJoined, topJoined);
    }

    private static void renderFluid(PoseStack poseStack, MultiBufferSource bufferSource, FluidStack fluid, float alpha, float heightPercentage, int combinedLight, boolean bottomJoined, boolean topJoined) {
        VertexConsumer vertexBuilder = bufferSource.getBuffer(RenderType.translucent());
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluid.getFluid());
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidTypeExtensions.getStillTexture(fluid));
        int color = fluidTypeExtensions.getTintColor();
        alpha *= (color >> 24 & 255) / 255f;
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;

        renderQuads(poseStack.last().pose(), vertexBuilder, sprite, red, green, blue, alpha, heightPercentage, combinedLight, bottomJoined, topJoined);
    }

    private static void renderQuads(Matrix4f matrix, VertexConsumer buffer, TextureAtlasSprite sprite, float r, float g, float b, float alpha, float heightPercentage, int light, boolean bottomJoined, boolean topJoined) {
        float height = MIN_Y + heightPercentage;
        float minU = sprite.getU(SIDE_MARGIN), maxU = sprite.getU((1 - SIDE_MARGIN));
        float minV = sprite.getV(MIN_Y), maxV = sprite.getV(height);
        // min z
        buffer.addVertex(matrix, SIDE_MARGIN, MIN_Y, SIDE_MARGIN).setColor(r, g, b, alpha).setUv(minU, minV).setLight(light).setNormal(0, 0, -1);
        buffer.addVertex(matrix, SIDE_MARGIN, height, SIDE_MARGIN).setColor(r, g, b, alpha).setUv(minU, maxV).setLight(light).setNormal(0, 0, -1);
        buffer.addVertex(matrix, 1 - SIDE_MARGIN, height, SIDE_MARGIN).setColor(r, g, b, alpha).setUv(maxU, maxV).setLight(light).setNormal(0, 0, -1);
        buffer.addVertex(matrix, 1 - SIDE_MARGIN, MIN_Y, SIDE_MARGIN).setColor(r, g, b, alpha).setUv(maxU, minV).setLight(light).setNormal(0, 0, -1);
        // max z
        buffer.addVertex(matrix, SIDE_MARGIN, MIN_Y, 1 - SIDE_MARGIN).setColor(r, g, b, alpha).setUv(minU, minV).setLight(light).setNormal(0, 0, 1);
        buffer.addVertex(matrix, 1 - SIDE_MARGIN, MIN_Y, 1 - SIDE_MARGIN).setColor(r, g, b, alpha).setUv(maxU, minV).setLight(light).setNormal(0, 0, 1);
        buffer.addVertex(matrix, 1 - SIDE_MARGIN, height, 1 - SIDE_MARGIN).setColor(r, g, b, alpha).setUv(maxU, maxV).setLight(light).setNormal(0, 0, 1);
        buffer.addVertex(matrix, SIDE_MARGIN, height, 1 - SIDE_MARGIN).setColor(r, g, b, alpha).setUv(minU, maxV).setLight(light).setNormal(0, 0, 1);
        // min x
        buffer.addVertex(matrix, SIDE_MARGIN, MIN_Y, SIDE_MARGIN).setColor(r, g, b, alpha).setUv(minU, minV).setLight(light).setNormal(-1, 0, 0);
        buffer.addVertex(matrix, SIDE_MARGIN, MIN_Y, 1 - SIDE_MARGIN).setColor(r, g, b, alpha).setUv(maxU, minV).setLight(light).setNormal(-1, 0, 0);
        buffer.addVertex(matrix, SIDE_MARGIN, height, 1 - SIDE_MARGIN).setColor(r, g, b, alpha).setUv(maxU, maxV).setLight(light).setNormal(-1, 0, 0);
        buffer.addVertex(matrix, SIDE_MARGIN, height, SIDE_MARGIN).setColor(r, g, b, alpha).setUv(minU, maxV).setLight(light).setNormal(-1, 0, 0);
        // max x
        buffer.addVertex(matrix, 1 - SIDE_MARGIN, MIN_Y, SIDE_MARGIN).setColor(r, g, b, alpha).setUv(minU, minV).setLight(light).setNormal(1, 0, 0);
        buffer.addVertex(matrix, 1 - SIDE_MARGIN, height, SIDE_MARGIN).setColor(r, g, b, alpha).setUv(minU, maxV).setLight(light).setNormal(1, 0, 0);
        buffer.addVertex(matrix, 1 - SIDE_MARGIN, height, 1 - SIDE_MARGIN).setColor(r, g, b, alpha).setUv(maxU, maxV).setLight(light).setNormal(1, 0, 0);
        buffer.addVertex(matrix, 1 - SIDE_MARGIN, MIN_Y, 1 - SIDE_MARGIN).setColor(r, g, b, alpha).setUv(maxU, minV).setLight(light).setNormal(1, 0, 0);
        // topJoined

        if (heightPercentage == 1) {
            heightPercentage = 0.99f;
            height = MIN_Y + heightPercentage;
        }

        minV = sprite.getV(SIDE_MARGIN);
        maxV = sprite.getV(1 - SIDE_MARGIN);
        if (!topJoined) {
            buffer.addVertex(matrix, SIDE_MARGIN, height, SIDE_MARGIN).setColor(r, g, b, alpha).setUv(minU, minV).setLight(light).setNormal(0, 1, 0);
            buffer.addVertex(matrix, SIDE_MARGIN, height, 1 - SIDE_MARGIN).setColor(r, g, b, alpha).setUv(minU, maxV).setLight(light).setNormal(0, 1, 0);
            buffer.addVertex(matrix, 1 - SIDE_MARGIN, height, 1 - SIDE_MARGIN).setColor(r, g, b, alpha).setUv(maxU, maxV).setLight(light).setNormal(0, 1, 0);
            buffer.addVertex(matrix, 1 - SIDE_MARGIN, height, SIDE_MARGIN).setColor(r, g, b, alpha).setUv(maxU, minV).setLight(light).setNormal(0, 1, 0);
        }

        if (!bottomJoined) {
            buffer.addVertex(matrix, SIDE_MARGIN, 0.01f, SIDE_MARGIN).setColor(r, g, b, alpha).setUv(minU, minV).setLight(light).setNormal(0, -1, 0);
            buffer.addVertex(matrix, 1 - SIDE_MARGIN, 0.01f, SIDE_MARGIN).setColor(r, g, b, alpha).setUv(maxU, minV).setLight(light).setNormal(0, -1, 0);
            buffer.addVertex(matrix, 1 - SIDE_MARGIN, 0.0F, 1 - SIDE_MARGIN).setColor(r, g, b, alpha).setUv(maxU, maxV).setLight(light).setNormal(0, -1, 0);
            buffer.addVertex(matrix, SIDE_MARGIN, 0.01f, 1 - SIDE_MARGIN).setColor(r, g, b, alpha).setUv(minU, maxV).setLight(light).setNormal(0, -1, 0);
        }
    }
}
