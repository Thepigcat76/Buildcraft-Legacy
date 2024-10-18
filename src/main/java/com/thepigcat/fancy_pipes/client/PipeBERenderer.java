package com.thepigcat.fancy_pipes.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.content.blockentities.PipeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class PipeBERenderer implements BlockEntityRenderer<PipeBlockEntity> {
    private float offset;

    public PipeBERenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(PipeBlockEntity pipeBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        if (offset >= 1) {
            offset = 0;
        } else {
            offset += 0.001f;
        }

        ItemStack stack = pipeBlockEntity.getItemHandler().getStackInSlot(0);

        Direction from = pipeBlockEntity.from.getOpposite();
        Direction to = pipeBlockEntity.to;

        float scalar = 1.5f - offset;

        poseStack.pushPose();
        {
            if (from != null && to != null) {
                Vec3i normal = from.getNormal();
                double x = 0.5 + normal.getX();
                double y = 0.5 + normal.getY();
                double z = 0.5 + normal.getZ();
                double x1 = x - normal.getX() * scalar;
                double y1 = y - normal.getY() * scalar;
                double z1 = z - normal.getZ() * scalar;
                poseStack.translate(x1, y1, z1);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, i, i1, poseStack, multiBufferSource, pipeBlockEntity.getLevel(), 1);
            }
        }
        poseStack.popPose();
    }
}
