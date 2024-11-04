package com.thepigcat.buildcraft;

import com.thepigcat.buildcraft.content.blockentities.CrateBE;
import com.thepigcat.buildcraft.content.blockentities.ItemPipeBE;
import com.thepigcat.buildcraft.content.blockentities.StirlingEngineBE;
import com.thepigcat.buildcraft.content.blockentities.TankBE;
import com.thepigcat.buildcraft.data.BCDataComponents;
import com.thepigcat.buildcraft.networking.SyncPipeDirectionPayload;
import com.thepigcat.buildcraft.networking.SyncPipeMovementPayload;
import com.thepigcat.buildcraft.registries.*;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(BuildcraftLegacy.MODID)
public final class BuildcraftLegacy {
    public static final String MODID = "buildcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID);

    static {
        CREATIVE_MODE_TABS.register("bc_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.buildcraft.bc_tab"))
                .icon(BCBlocks.COBBLESTONE_ITEM_PIPE::toStack)
                .displayItems((parameters, output) -> {
                    for (DeferredItem<?> item : BCItems.TAB_ITEMS) {
                        output.accept(item);
                    }
                }).build());
    }

    public BuildcraftLegacy(IEventBus modEventBus, ModContainer modContainer) {
        CREATIVE_MODE_TABS.register(modEventBus);
        BCBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        BCBlocks.BLOCKS.register(modEventBus);
        BCItems.ITEMS.register(modEventBus);
        BCFluids.FLUIDS.register(modEventBus);
        BCFluidTypes.FLUID_TYPES.register(modEventBus);
        BCDataComponents.DATA_COMPONENTS.register(modEventBus);
        BCMenuTypes.MENUS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, BCConfig.SPEC);

        modEventBus.addListener(this::attachCaps);
        modEventBus.addListener(this::registerPayloads);
    }

    private void attachCaps(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BCBlockEntities.ITEM_PIPE.get(), ItemPipeBE::getItemHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BCBlockEntities.EXTRACTING_ITEM_PIPE.get(), ItemPipeBE::getItemHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BCBlockEntities.CRATE.get(), CrateBE::getItemHandler);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BCBlockEntities.TANK.get(), TankBE::getFluidTank);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BCBlockEntities.STIRLING_ENGINE.get(), (be, ctx) -> be.getItemHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BCBlockEntities.COMBUSTION_ENGINE.get(), (be, ctx) -> be.getFluidHandler());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BCBlockEntities.REDSTONE_ENGINE.get(), (be, ctx) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BCBlockEntities.STIRLING_ENGINE.get(), (be, ctx) -> be.getEnergyStorage());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BCBlockEntities.COMBUSTION_ENGINE.get(), (be, ctx) -> be.getEnergyStorage());
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID);
        registrar.playToClient(SyncPipeDirectionPayload.TYPE, SyncPipeDirectionPayload.STREAM_CODEC, SyncPipeDirectionPayload::sync);
        registrar.playToClient(SyncPipeMovementPayload.TYPE, SyncPipeMovementPayload.STREAM_CODEC, SyncPipeMovementPayload::sync);
    }
}
