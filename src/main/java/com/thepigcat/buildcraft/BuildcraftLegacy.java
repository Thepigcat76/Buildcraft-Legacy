package com.thepigcat.buildcraft;

import com.mojang.datafixers.util.Either;
import com.portingdeadmods.portingdeadlibs.api.blockentities.ContainerBlockEntity;
import com.portingdeadmods.portingdeadlibs.api.fluids.PDLFluid;
import com.thepigcat.buildcraft.api.capabilties.JumboItemHandlerItemWrapper;
import com.thepigcat.buildcraft.api.pipes.Pipe;
import com.thepigcat.buildcraft.api.pipes.PipeType;
import com.thepigcat.buildcraft.content.blockentities.CrateBE;
import com.thepigcat.buildcraft.content.blockentities.ItemPipeBE;
import com.thepigcat.buildcraft.content.blockentities.TankBE;
import com.thepigcat.buildcraft.data.BCDataComponents;
import com.thepigcat.buildcraft.networking.RedstoneSignalTypeSyncPayload;
import com.thepigcat.buildcraft.networking.SyncPipeDirectionPayload;
import com.thepigcat.buildcraft.networking.SyncPipeMovementPayload;
import com.thepigcat.buildcraft.registries.*;
import com.thepigcat.buildcraft.util.PipeRegistrationHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.RegisterEvent;
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

import java.util.Comparator;
import java.util.Map;

@Mod(BuildcraftLegacy.MODID)
public final class BuildcraftLegacy {
    public static final String MODID = "buildcraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID);

    private static boolean pipesLoaded = false;

    static {
        CREATIVE_MODE_TABS.register("bc_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.buildcraft.bc_tab"))
                .icon(BCBlocks.TANK::toStack)
                .displayItems((parameters, output) -> {
                    for (DeferredItem<?> item : BCItems.TAB_ITEMS) {
                        output.accept(item);
                    }

                    for (PDLFluid fluid : BCFluids.HELPER.getFluids()) {
                        output.accept(fluid.deferredBucket);
                    }

                    PipesRegistry.PIPES.entrySet().stream().sorted(Comparator.comparingInt(e -> e.getValue().tabOrdering())).forEach(e -> {
                        Block block = BuiltInRegistries.BLOCK.get(rl(e.getKey()));
                        output.accept(block);
                    });
                }).build());
    }

    public BuildcraftLegacy(IEventBus modEventBus, ModContainer modContainer) {
        CREATIVE_MODE_TABS.register(modEventBus);
        BCBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        BCBlocks.BLOCKS.register(modEventBus);
        BCItems.ITEMS.register(modEventBus);
        BCFluids.HELPER.register(modEventBus);
        BCDataComponents.DATA_COMPONENTS.register(modEventBus);
        BCMenuTypes.MENUS.register(modEventBus);
        BCPipeTypes.init();

        modContainer.registerConfig(ModConfig.Type.COMMON, BCConfig.SPEC);

        modEventBus.addListener(this::attachCaps);
        modEventBus.addListener(this::onCommonSetup);
        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(this::onRegister);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        PipesRegistry.writeDefaultPipeFiles();
    }

    private void attachCaps(RegisterCapabilitiesEvent event) {
        // ITEMS
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BCBlockEntities.ITEM_PIPE.get(), ItemPipeBE::getItemHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BCBlockEntities.EXTRACTING_ITEM_PIPE.get(), ItemPipeBE::getItemHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BCBlockEntities.CRATE.get(), CrateBE::getItemHandler);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BCBlockEntities.STIRLING_ENGINE.get(), ContainerBlockEntity::getItemHandlerOnSide);
        // FLUID
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BCBlockEntities.COMBUSTION_ENGINE.get(), ContainerBlockEntity::getFluidHandlerOnSide);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BCBlockEntities.TANK.get(), ContainerBlockEntity::getFluidHandlerOnSide);
        // ENERGY
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BCBlockEntities.REDSTONE_ENGINE.get(), ContainerBlockEntity::getEnergyStorageOnSide);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BCBlockEntities.STIRLING_ENGINE.get(), ContainerBlockEntity::getEnergyStorageOnSide);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, BCBlockEntities.COMBUSTION_ENGINE.get(), ContainerBlockEntity::getEnergyStorageOnSide);

        event.registerItem(Capabilities.ItemHandler.ITEM, (stack, ctx) -> new JumboItemHandlerItemWrapper(stack), BCBlocks.CRATE);
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidHandlerItemStack(BCDataComponents.TANK_CONTENT, stack, BCConfig.tankCapacity), BCBlocks.TANK);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID);
        registrar.playToClient(SyncPipeDirectionPayload.TYPE, SyncPipeDirectionPayload.STREAM_CODEC, SyncPipeDirectionPayload::handle);
        registrar.playToClient(SyncPipeMovementPayload.TYPE, SyncPipeMovementPayload.STREAM_CODEC, SyncPipeMovementPayload::handle);
        registrar.playToServer(RedstoneSignalTypeSyncPayload.TYPE, RedstoneSignalTypeSyncPayload.STREAM_CODEC, RedstoneSignalTypeSyncPayload::handle);
    }

    private void onRegister(RegisterEvent event) {
        if (!pipesLoaded) {
            PipesRegistry.loadPipes();
            pipesLoaded = true;
        }

        if (event.getRegistryKey() == Registries.BLOCK) {
            for (Map.Entry<String, Pipe> entry : PipesRegistry.PIPES.entrySet()) {
                PipeType<?, ?> type = PipeRegistrationHelper.PIPE_TYPES.getOrDefault(entry.getValue().type(), BCPipeTypes.DEFAULT.value());
                ResourceLocation id = rl(entry.getKey());
                if (!event.getRegistry().containsKey(id)) {
                    Either<BlockBehaviour.Properties, ResourceLocation> properties = entry.getValue().properties();
                    ResourceLocation block = properties.right().orElse(ResourceLocation.parse("cobblestone"));
                    BuildcraftLegacy.LOGGER.debug("Properties: {}", properties);
                    event.register(Registries.BLOCK, id, () -> type.blockConstructor().apply(properties.left().isPresent()
                            ? properties.left().get()
                            : BlockBehaviour.Properties.ofFullCopy(BuiltInRegistries.BLOCK.get(block))
                    ));
                } else {
                    BuildcraftLegacy.LOGGER.error("Failed to register pipe {} because a block with the same name exists already", id);
                }
            }
        }

        if (event.getRegistryKey() == Registries.ITEM) {
            for (Map.Entry<String, Pipe> entry : PipesRegistry.PIPES.entrySet()) {
                PipeType<?, ?> type = PipeRegistrationHelper.PIPE_TYPES.getOrDefault(entry.getValue().type(), BCPipeTypes.DEFAULT.value());
                ResourceLocation id = rl(entry.getKey());
                if (!event.getRegistry().containsKey(id)) {
                    event.register(Registries.ITEM, id, () -> type.blockItemConstructor().apply(BuiltInRegistries.BLOCK.get(id), new Item.Properties()));
                } else {
                    BuildcraftLegacy.LOGGER.error("Failed to register pipe {} because a block item with the same name exists already", id);
                }
            }
        }
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
