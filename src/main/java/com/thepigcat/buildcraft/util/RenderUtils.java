package com.thepigcat.buildcraft.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;

public final class RenderUtils {
    public static void renderBlockModel(BlockState blockState, PoseStack poseStack, MultiBufferSource pBufferSource, int combinedLight, int combinedOverlay) {
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BakedModel bakedmodel = blockRenderer.getBlockModel(blockState);
        int i = blockRenderer.blockColors.getColor(blockState, null, null, 0);
        float f = (float) (i >> 16 & 255) / 255.0F;
        float f1 = (float) (i >> 8 & 255) / 255.0F;
        float f2 = (float) (i & 255) / 255.0F;

        for (RenderType rt : bakedmodel.getRenderTypes(blockState, RandomSource.create(42L), ModelData.EMPTY)) {
            blockRenderer.getModelRenderer().renderModel(poseStack.last(), pBufferSource.getBuffer(RenderTypeHelper.getEntityRenderType(rt, false)), blockState, bakedmodel, f, f1, f2, combinedLight, combinedOverlay, ModelData.EMPTY, rt);
        }
    }
}
