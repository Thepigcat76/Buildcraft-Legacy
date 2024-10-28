package com.thepigcat.fancy_pipes.registries;

import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.content.blockentities.CrateBE;
import com.thepigcat.fancy_pipes.content.blockentities.ExtractItemPipeBE;
import com.thepigcat.fancy_pipes.content.blockentities.ItemPipeBE;
import com.thepigcat.fancy_pipes.content.blockentities.TankBE;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class FPBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, FancyPipes.MODID);

    public static final Supplier<BlockEntityType<ItemPipeBE>> ITEM_PIPE = BLOCK_ENTITIES.register("item_pipe",
            () -> BlockEntityType.Builder.of(ItemPipeBE::new, FPBlocks.COBBLESTONE_ITEM_PIPE.get()).build(null));
    public static final Supplier<BlockEntityType<ExtractItemPipeBE>> EXTRACTING_ITEM_PIPE = BLOCK_ENTITIES.register("extracting_item_pipe",
            () -> BlockEntityType.Builder.of(ExtractItemPipeBE::new, FPBlocks.WOODEN_ITEM_PIPE.get()).build(null));

    public static final Supplier<BlockEntityType<CrateBE>> CRATE = BLOCK_ENTITIES.register("crate",
            () -> BlockEntityType.Builder.of(CrateBE::new, FPBlocks.CRATE.get()).build(null));
    public static final Supplier<BlockEntityType<TankBE>> TANK = BLOCK_ENTITIES.register("tank",
            () -> BlockEntityType.Builder.of(TankBE::new, FPBlocks.TANK.get()).build(null));
}
