package com.thepigcat.buildcraft.client.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thepigcat.buildcraft.BCConfig;
import com.thepigcat.buildcraft.client.blockentities.TankBERenderer;
import com.thepigcat.buildcraft.data.BCDataComponents;
import com.thepigcat.buildcraft.registries.BCBlocks;
import com.thepigcat.buildcraft.util.RenderUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class TankItemRenderer extends BlockEntityWithoutLevelRenderer {
    public TankItemRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
        FluidStack fluidStack = stack.get(BCDataComponents.TANK_CONTENT).copy();
        if (!fluidStack.isEmpty()) {
            TankBERenderer.renderTankContents(fluidStack, fluidStack.getAmount(), BCConfig.tankCapacity, poseStack, buffer, packedLight, false, false);
        }
        RenderUtils.renderBlockModel(BCBlocks.TANK.get().defaultBlockState(), poseStack, buffer, packedLight, packedOverlay);
    }
}
