package com.thepigcat.fancy_pipes;

import com.thepigcat.fancy_pipes.client.CrateBERenderer;
import com.thepigcat.fancy_pipes.client.PipeBERenderer;
import com.thepigcat.fancy_pipes.registries.FPBlockEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(FancyPipesClient.MODID)
public final class FancyPipesClient {
    public static final String MODID = "fancy_pipes";

    public FancyPipesClient(IEventBus eventBus, ModContainer modContainer) {
        eventBus.addListener(this::registerRenderers);
    }

    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FPBlockEntities.ITEM_PIPE.get(), PipeBERenderer::new);
        event.registerBlockEntityRenderer(FPBlockEntities.EXTRACTING_ITEM_PIPE.get(), PipeBERenderer::new);
        event.registerBlockEntityRenderer(FPBlockEntities.CRATE.get(), CrateBERenderer::new);
    }
}
