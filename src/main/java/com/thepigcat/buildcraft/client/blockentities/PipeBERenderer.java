package com.thepigcat.buildcraft.client.blockentities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.content.blockentities.ItemPipeBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PipeBERenderer implements BlockEntityRenderer<ItemPipeBE> {
    public PipeBERenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ItemPipeBE pipeBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        ItemStack stack = pipeBlockEntity.getItemHandler().getStackInSlot(0);

        Direction from = pipeBlockEntity.from;
        Direction to = pipeBlockEntity.to;

        float v = Mth.lerp(partialTicks, pipeBlockEntity.lastMovement, pipeBlockEntity.movement);
        float scalar = 1.5f - v;

        poseStack.pushPose();
        {
            if (from != null && to != null) {
                Vec3i normal = (scalar > 1 ? from.getOpposite() : to).getNormal();
                double x = 0.5 + normal.getX();
                double y = 0.5 + normal.getY();
                double z = 0.5 + normal.getZ();
                double x1 = x - normal.getX() * scalar;
                double y1 = y - normal.getY() * scalar;
                double z1 = z - normal.getZ() * scalar;
                poseStack.translate(x1, y1, z1);
            } else {
                poseStack.translate(0.5, 0.5, 0.5);
            }
            poseStack.scale(0.5f, 0.5f, 0.5f);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, i, i1, poseStack, multiBufferSource, pipeBlockEntity.getLevel(), 1);
        }
        poseStack.popPose();
    }
}
