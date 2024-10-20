package com.thepigcat.fancy_pipes.registries;

import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.content.blocks.CobblestoneItemPipeBlock;
import com.thepigcat.fancy_pipes.content.blocks.WoodenItemPipeBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public final class FPBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FancyPipes.MODID);

    public static final DeferredBlock<CobblestoneItemPipeBlock> COBBLESTONE_ITEM_PIPE = registerBlockAndItem("cobblestone_pipe", CobblestoneItemPipeBlock::new, BlockBehaviour.Properties.of());
    public static final DeferredBlock<WoodenItemPipeBlock> WOODEN_ITEM_PIPE = registerBlockAndItem("wooden_pipe", WoodenItemPipeBlock::new, BlockBehaviour.Properties.of());

    public static <T extends Block> DeferredBlock<T> registerBlockAndItem(String name, Function<BlockBehaviour.Properties, T> blockConstructor, BlockBehaviour.Properties props) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, blockConstructor, props);
        DeferredItem<BlockItem> item = FPItems.ITEMS.registerSimpleBlockItem(toReturn);
        FPItems.BLOCK_ITEMS.add(item);
        FPItems.TAB_ITEMS.add(item);
        return toReturn;
    }
}
