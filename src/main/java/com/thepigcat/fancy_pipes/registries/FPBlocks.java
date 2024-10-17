package com.thepigcat.fancy_pipes.registries;

import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.content.blocks.PipeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public final class FPBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FancyPipes.MODID);

    public static final DeferredBlock<PipeBlock> PIPE = registerBlockAndItem("pipe", PipeBlock::new, BlockBehaviour.Properties.of());

    public static <T extends Block> DeferredBlock<T> registerBlockAndItem(String name, Function<BlockBehaviour.Properties, T> blockConstructor, BlockBehaviour.Properties props) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, blockConstructor, props);
        FPItems.ITEMS.registerSimpleBlockItem(toReturn);
        return toReturn;
    }
}
