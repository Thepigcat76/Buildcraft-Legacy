package com.thepigcat.buildcraft;

import com.thepigcat.buildcraft.client.blockentities.CrateBERenderer;
import com.thepigcat.buildcraft.client.blockentities.EngineBERenderer;
import com.thepigcat.buildcraft.client.blockentities.PipeBERenderer;
import com.thepigcat.buildcraft.client.blockentities.TankBERenderer;
import com.thepigcat.buildcraft.client.items.CrateItemRenderer;
import com.thepigcat.buildcraft.client.items.EngineItemRenderer;
import com.thepigcat.buildcraft.client.items.TankItemRenderer;
import com.thepigcat.buildcraft.client.models.EnginePistonModel;
import com.thepigcat.buildcraft.client.screens.CombustionEngineScreen;
import com.thepigcat.buildcraft.client.screens.StirlingEngineScreen;
import com.thepigcat.buildcraft.registries.BCBlockEntities;
import com.thepigcat.buildcraft.registries.BCBlocks;
import com.thepigcat.buildcraft.registries.BCMenuTypes;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

@Mod(value = BuildcraftLegacyClient.MODID, dist = Dist.CLIENT)
public final class BuildcraftLegacyClient {
    public static final String MODID = "buildcraft";

    public BuildcraftLegacyClient(IEventBus eventBus, ModContainer modContainer) {
        eventBus.addListener(this::registerRenderers);
        eventBus.addListener(this::registerClientExtensions);
        eventBus.addListener(this::registerModelLayers);
        eventBus.addListener(this::registerMenuScreens);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return TANK_ITEM_RENDERER;
            }
        }, BCBlocks.TANK.asItem());

        event.registerItem(new IClientItemExtensions() {
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return CRATE_ITEM_RENDERER;
            }
        }, BCBlocks.CRATE.asItem());

        event.registerItem(new IClientItemExtensions() {
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ENGINE_ITEM_RENDERERS[0];
            }
        }, BCBlocks.REDSTONE_ENGINE.asItem());

        event.registerItem(new IClientItemExtensions() {
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ENGINE_ITEM_RENDERERS[1];
            }
        }, BCBlocks.STIRLING_ENGINE.asItem());

        event.registerItem(new IClientItemExtensions() {
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ENGINE_ITEM_RENDERERS[2];
            }
        }, BCBlocks.COMBUSTION_ENGINE.asItem());

    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BCBlockEntities.ITEM_PIPE.get(), PipeBERenderer::new);
        event.registerBlockEntityRenderer(BCBlockEntities.EXTRACTING_ITEM_PIPE.get(), PipeBERenderer::new);
        event.registerBlockEntityRenderer(BCBlockEntities.CRATE.get(), CrateBERenderer::new);
        event.registerBlockEntityRenderer(BCBlockEntities.TANK.get(), TankBERenderer::new);
        event.registerBlockEntityRenderer(BCBlockEntities.REDSTONE_ENGINE.get(), ctx -> new EngineBERenderer(ctx, ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "entity/wooden_engine_piston")));
        event.registerBlockEntityRenderer(BCBlockEntities.STIRLING_ENGINE.get(), ctx -> new EngineBERenderer(ctx, ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "entity/cobblestone_engine_piston")));
        event.registerBlockEntityRenderer(BCBlockEntities.COMBUSTION_ENGINE.get(), ctx -> new EngineBERenderer(ctx, ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "entity/iron_engine_piston")));
    }

    private static final TankItemRenderer TANK_ITEM_RENDERER = new TankItemRenderer();
    private static final CrateItemRenderer CRATE_ITEM_RENDERER = new CrateItemRenderer();

    private static final EngineItemRenderer[] ENGINE_ITEM_RENDERERS = new EngineItemRenderer[]{
            new EngineItemRenderer(ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "entity/wooden_engine_piston"), BCBlocks.REDSTONE_ENGINE),
            new EngineItemRenderer(ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "entity/cobblestone_engine_piston"), BCBlocks.STIRLING_ENGINE),
            new EngineItemRenderer(ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "entity/iron_engine_piston"), BCBlocks.COMBUSTION_ENGINE),
    };

    private void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EnginePistonModel.LAYER_LOCATION, EnginePistonModel::createBodyLayer);
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(BCMenuTypes.STIRLING_ENGINE.get(), StirlingEngineScreen::new);
        event.register(BCMenuTypes.COMBUSTION_ENGINE.get(), CombustionEngineScreen::new);
    }
}
