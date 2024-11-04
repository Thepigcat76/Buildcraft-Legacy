package com.thepigcat.buildcraft.client.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thepigcat.buildcraft.api.blocks.EngineBlock;
import com.thepigcat.buildcraft.client.blockentities.CrateBERenderer;
import com.thepigcat.buildcraft.data.BCDataComponents;
import com.thepigcat.buildcraft.registries.BCBlocks;
import com.thepigcat.buildcraft.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.client.model.data.ModelData;

public class CrateItemRenderer extends BlockEntityWithoutLevelRenderer {
    public CrateItemRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        RenderUtils.renderBlockModel(BCBlocks.CRATE.get().defaultBlockState(), poseStack, buffer, packedLight, packedOverlay);
        ItemStack itemStack = stack.get(BCDataComponents.CRATE_CONTENT).copyOne();
        if (!itemStack.isEmpty()) {
            CrateBERenderer.renderItemsAndCount(poseStack, buffer, packedOverlay, itemStack, Direction.NORTH);
        }
    }
}
