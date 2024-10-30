package com.thepigcat.fancy_pipes;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.thepigcat.fancy_pipes.api.fluids.BaseFluidType;
import com.thepigcat.fancy_pipes.client.CrateBERenderer;
import com.thepigcat.fancy_pipes.client.PipeBERenderer;
import com.thepigcat.fancy_pipes.client.TankBERenderer;
import com.thepigcat.fancy_pipes.registries.FPBlockEntities;
import com.thepigcat.fancy_pipes.registries.FPFluidTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Supplier;

@Mod(FancyPipesClient.MODID)
public final class FancyPipesClient {
    public static final String MODID = "fancy_pipes";

    public FancyPipesClient(IEventBus eventBus, ModContainer modContainer) {
        eventBus.addListener(this::registerRenderers);
        eventBus.addListener(this::registerClientExtensions);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FPBlockEntities.ITEM_PIPE.get(), PipeBERenderer::new);
        event.registerBlockEntityRenderer(FPBlockEntities.EXTRACTING_ITEM_PIPE.get(), PipeBERenderer::new);
        event.registerBlockEntityRenderer(FPBlockEntities.CRATE.get(), CrateBERenderer::new);
        event.registerBlockEntityRenderer(FPBlockEntities.TANK.get(), TankBERenderer::new);
    }

    private void registerClientExtensions(RegisterClientExtensionsEvent event) {
        for (DeferredHolder<FluidType, ? extends FluidType> fluidType : FPFluidTypes.FLUID_TYPES.getEntries()) {
            if (fluidType.get() instanceof BaseFluidType baseFluidType) {
                event.registerFluidType(new IClientFluidTypeExtensions() {
                    @Override
                    public @NotNull ResourceLocation getStillTexture() {
                        return baseFluidType.getStillTexture();
                    }

                    @Override
                    public @NotNull ResourceLocation getFlowingTexture() {
                        return baseFluidType.getFlowingTexture();
                    }

                    @Override
                    public @Nullable ResourceLocation getOverlayTexture() {
                        return baseFluidType.getOverlayTexture();
                    }

                    @Override
                    public int getTintColor() {
                        Vec3i color = baseFluidType.getColor();
                        return FastColor.ARGB32.color(color.getX(), color.getY(), color.getZ());
                    }

                    @Override
                    public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                        Vec3i color = baseFluidType.getFogColor();
                        return new Vector3f(color.getX() / 255f, color.getY() / 255f, color.getZ() / 255f);
                    }

                    @Override
                    public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                        RenderSystem.setShaderFogStart(1f);
                        RenderSystem.setShaderFogEnd(6f); // distance when the fog starts
                    }
                }, baseFluidType);
            }
        }
    }
}
