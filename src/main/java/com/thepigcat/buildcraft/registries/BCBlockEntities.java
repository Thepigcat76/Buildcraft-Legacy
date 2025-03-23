package com.thepigcat.buildcraft.registries;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.content.blockentities.*;
import com.thepigcat.buildcraft.content.blocks.ItemPipeBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class BCBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BuildcraftLegacy.MODID);

    public static final Supplier<BlockEntityType<ItemPipeBE>> ITEM_PIPE = BLOCK_ENTITIES.register("item_pipe",
            () -> BlockEntityType.Builder.of(ItemPipeBE::new, collectPipes()).build(null));

    private static Block[] collectPipes() {
        return BuiltInRegistries.BLOCK.stream().filter(block -> block instanceof ItemPipeBlock).toList().toArray(Block[]::new);
    }

    public static final Supplier<BlockEntityType<ExtractItemPipeBE>> EXTRACTING_ITEM_PIPE = BLOCK_ENTITIES.register("extracting_item_pipe",
            () -> BlockEntityType.Builder.of(ExtractItemPipeBE::new, BCBlocks.WOODEN_ITEM_PIPE.get()).build(null));

    public static final Supplier<BlockEntityType<TankBE>> TANK = BLOCK_ENTITIES.register("tank",
            () -> BlockEntityType.Builder.of(TankBE::new, BCBlocks.TANK.get()).build(null));
    public static final Supplier<BlockEntityType<CrateBE>> CRATE = BLOCK_ENTITIES.register("crate",
            () -> BlockEntityType.Builder.of(CrateBE::new, BCBlocks.CRATE.get()).build(null));

    public static final Supplier<BlockEntityType<RedstoneEngineBE>> REDSTONE_ENGINE = BLOCK_ENTITIES.register("redstone_engine",
            () -> BlockEntityType.Builder.of(RedstoneEngineBE::new, BCBlocks.REDSTONE_ENGINE.get()).build(null));
    public static final Supplier<BlockEntityType<StirlingEngineBE>> STIRLING_ENGINE = BLOCK_ENTITIES.register("stirling_engine",
            () -> BlockEntityType.Builder.of(StirlingEngineBE::new, BCBlocks.STIRLING_ENGINE.get()).build(null));
    public static final Supplier<BlockEntityType<CombustionEngineBE>> COMBUSTION_ENGINE = BLOCK_ENTITIES.register("combustion_engine",
            () -> BlockEntityType.Builder.of(CombustionEngineBE::new, BCBlocks.COMBUSTION_ENGINE.get()).build(null));
}
