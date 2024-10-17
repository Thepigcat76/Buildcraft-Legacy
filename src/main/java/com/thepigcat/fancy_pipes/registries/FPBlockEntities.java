package com.thepigcat.fancy_pipes.registries;

import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.content.blockentities.PipeBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class FPBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, FancyPipes.MODID);

    public static final Supplier<BlockEntityType<PipeBlockEntity>> PIPE = BLOCK_ENTITIES.register("pipe",
            () -> BlockEntityType.Builder.of(PipeBlockEntity::new, FPBlocks.PIPE.get()).build(null));
}
