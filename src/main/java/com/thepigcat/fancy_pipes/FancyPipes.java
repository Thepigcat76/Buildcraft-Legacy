package com.thepigcat.fancy_pipes;

import com.thepigcat.fancy_pipes.content.blockentities.CrateBE;
import com.thepigcat.fancy_pipes.content.blockentities.ItemPipeBE;
import com.thepigcat.fancy_pipes.networking.SyncPipeDirectionPayload;
import com.thepigcat.fancy_pipes.networking.SyncPipeMovementPayload;
import com.thepigcat.fancy_pipes.registries.FPBlockEntities;
import com.thepigcat.fancy_pipes.registries.FPBlocks;
import com.thepigcat.fancy_pipes.registries.FPItems;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredItem;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FancyPipes.MODID)
public final class FancyPipes {
    public static final String MODID = "fancy_pipes";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID);

    static {
        CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.examplemod"))
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(FPBlocks.COBBLESTONE_ITEM_PIPE::toStack)
                .displayItems((parameters, output) -> {
                    output.accept(FPItems.WRENCH);

                    for (DeferredItem<?> item : FPItems.TAB_ITEMS) {
                        output.accept(item);
                    }
                }).build());
    }

    public FancyPipes(IEventBus modEventBus, ModContainer modContainer) {
        CREATIVE_MODE_TABS.register(modEventBus);
        FPBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        FPBlocks.BLOCKS.register(modEventBus);
        FPItems.ITEMS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modEventBus.addListener(this::attachCaps);
        modEventBus.addListener(this::registerPayloads);
    }

    private void attachCaps(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FPBlockEntities.ITEM_PIPE.get(), ItemPipeBE::getItemHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FPBlockEntities.EXTRACTING_ITEM_PIPE.get(), ItemPipeBE::getItemHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FPBlockEntities.CRATE.get(), CrateBE::getItemHandler);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID);
        registrar.playToClient(SyncPipeDirectionPayload.TYPE, SyncPipeDirectionPayload.STREAM_CODEC, SyncPipeDirectionPayload::sync);
        registrar.playToClient(SyncPipeMovementPayload.TYPE, SyncPipeMovementPayload.STREAM_CODEC, SyncPipeMovementPayload::sync);
    }
}
