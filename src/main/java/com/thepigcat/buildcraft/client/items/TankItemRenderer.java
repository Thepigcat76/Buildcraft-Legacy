package com.thepigcat.buildcraft.client.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thepigcat.buildcraft.FPConfig;
import com.thepigcat.buildcraft.FancyPipes;
import com.thepigcat.buildcraft.client.blockentities.TankBERenderer;
import com.thepigcat.buildcraft.registries.FPBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public class TankItemRenderer extends BlockEntityWithoutLevelRenderer {
    public TankItemRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        super.renderByItem(stack, displayContext, poseStack, buffer, packedLight, packedOverlay);
        CustomData beNbt = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (beNbt != null) {
            Optional<FluidStack> fluidStack = FluidStack.parse(Minecraft.getInstance().level.registryAccess(), beNbt.copyTag().getCompound("fluidTank").getCompound("Fluid"));
            if (fluidStack.isPresent() && !fluidStack.get().isEmpty()) {
                TankBERenderer.renderTankContents(fluidStack.get(), FPConfig.tankCapacity, poseStack, buffer, packedLight);
            } else {
                FancyPipes.LOGGER.debug("No fluid to render :sob:");
            }
        }
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(FPBlocks.TANK.get().defaultBlockState(), poseStack, buffer, packedLight, packedOverlay);
    }
}
