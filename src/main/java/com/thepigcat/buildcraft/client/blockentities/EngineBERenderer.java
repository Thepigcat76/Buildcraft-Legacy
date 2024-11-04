package com.thepigcat.buildcraft.client.blockentities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.thepigcat.buildcraft.api.blockentities.EngineBlockEntity;
import com.thepigcat.buildcraft.api.blocks.EngineBlock;
import com.thepigcat.buildcraft.client.models.EnginePistonModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;

public class EngineBERenderer implements BlockEntityRenderer<EngineBlockEntity> {
    private final Material enginePistonMaterial;
    private final EnginePistonModel model;

    public EngineBERenderer(BlockEntityRendererProvider.Context context, ResourceLocation texture) {
        this.enginePistonMaterial = new Material(InventoryMenu.BLOCK_ATLAS, texture);
        this.model = new EnginePistonModel(context.bakeLayer(EnginePistonModel.LAYER_LOCATION));
    }

    @Override
    public void render(EngineBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float movement = Mth.lerp(partialTick, blockEntity.lastMovement, blockEntity.movement);
        renderPiston(blockEntity.getBlockState().getValue(EngineBlock.FACING), model, enginePistonMaterial, poseStack, bufferSource, packedLight, packedOverlay, movement);
    }

    public static void renderPiston(Model model, Material material, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float movement) {
        renderPiston(Direction.UP, model, material, poseStack, bufferSource, packedLight, packedOverlay, movement);
    }

    public static void renderPiston(Direction direction, Model model, Material material, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, float movement) {
        poseStack.pushPose();
        {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(direction.getRotation());
            poseStack.translate(-0.5, -0.5, -0.5);
            poseStack.translate(0.5, -0.5 - movement, 0.5);
            model.renderToBuffer(poseStack, material.buffer(bufferSource, RenderType::entitySolid), packedLight, packedOverlay);
        }
        poseStack.popPose();
    }
}
