package com.thepigcat.fancy_pipes.registries;

import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.content.blocks.ExtractingItemPipeBlock;
import com.thepigcat.fancy_pipes.content.blocks.ItemPipeBlock;
import com.thepigcat.fancy_pipes.content.blocks.CrateBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public final class FPBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FancyPipes.MODID);

    // PIPES
    public static final DeferredBlock<ItemPipeBlock> COBBLESTONE_ITEM_PIPE = registerBlockAndItem("cobblestone_pipe", ItemPipeBlock::new, BlockBehaviour.Properties.of());
    public static final DeferredBlock<ExtractingItemPipeBlock> WOODEN_ITEM_PIPE = registerBlockAndItem("wooden_pipe", ExtractingItemPipeBlock::new, BlockBehaviour.Properties.of());

    // MISC
    public static final DeferredBlock<CrateBlock> CRATE = registerBlockAndItem("crate", CrateBlock::new, BlockBehaviour.Properties.of());

    public static <T extends Block> DeferredBlock<T> registerBlockAndItem(String name, Function<BlockBehaviour.Properties, T> blockConstructor, BlockBehaviour.Properties props) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, blockConstructor, props);
        DeferredItem<BlockItem> item = FPItems.ITEMS.registerSimpleBlockItem(toReturn);
        FPItems.BLOCK_ITEMS.add(item);
        FPItems.TAB_ITEMS.add(item);
        return toReturn;
    }
}
