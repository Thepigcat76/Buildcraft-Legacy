package com.thepigcat.buildcraft.client.items;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.blocks.EngineBlock;
import com.thepigcat.buildcraft.client.blockentities.EngineBERenderer;
import com.thepigcat.buildcraft.client.models.EnginePistonModel;
import com.thepigcat.buildcraft.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.registries.DeferredBlock;

public class EngineItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final DeferredBlock<?> engineBlock;
    private final Material material;

    public EngineItemRenderer(ResourceLocation texture, DeferredBlock<?> block) {
        super(null, null);
        this.material = new Material(InventoryMenu.BLOCK_ATLAS, texture);
        this.engineBlock = block;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        EnginePistonModel model = new EnginePistonModel(Minecraft.getInstance().getEntityModels().bakeLayer(EnginePistonModel.LAYER_LOCATION));
        RenderUtils.renderBlockModel(engineBlock.get().defaultBlockState().setValue(EngineBlock.FACING, Direction.UP), poseStack, buffer, packedLight, packedOverlay);
        EngineBERenderer.renderPiston(model, material, poseStack, buffer, packedLight, packedOverlay, 0.3f);
    }

}
