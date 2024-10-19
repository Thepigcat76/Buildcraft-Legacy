package com.thepigcat.fancy_pipes.registries;

import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.content.blockentities.ItemPipeBE;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class FPBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, FancyPipes.MODID);

    public static final Supplier<BlockEntityType<ItemPipeBE>> COBBLESTONE_PIPE = BLOCK_ENTITIES.register("cobblestone_item_pipe",
            () -> BlockEntityType.Builder.of(ItemPipeBE::new, FPBlocks.COBBLESTONE_ITEM_PIPE.get(), FPBlocks.WOODEN_ITEM_PIPE.get()).build(null));
}
