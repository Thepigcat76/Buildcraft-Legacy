package com.thepigcat.buildcraft.registries;

import com.thepigcat.buildcraft.FancyPipes;
import com.thepigcat.buildcraft.content.blocks.ExtractingItemPipeBlock;
import com.thepigcat.buildcraft.content.blocks.ItemPipeBlock;
import com.thepigcat.buildcraft.content.blocks.CrateBlock;
import com.thepigcat.buildcraft.content.blocks.TankBlock;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public final class FPBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FancyPipes.MODID);

    // PIPES
    public static final DeferredBlock<ItemPipeBlock> COBBLESTONE_ITEM_PIPE = registerBlockAndItem("cobblestone_pipe", ItemPipeBlock::new,
            BlockBehaviour.Properties.of().strength(1.5f, 6).sound(SoundType.STONE).mapColor(MapColor.STONE));
    public static final DeferredBlock<ExtractingItemPipeBlock> WOODEN_ITEM_PIPE = registerBlockAndItem("wooden_pipe", ExtractingItemPipeBlock::new,
            BlockBehaviour.Properties.of().strength(2.0f, 3).sound(SoundType.WOOD).mapColor(MapColor.WOOD));

    // ENGINES
    public static final DeferredBlock<Block> WOODEN_ENGINE = registerBlockAndItem("wooden_engine", Block::new,
            BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD).mapColor(MapColor.WOOD));

    // MISC
    public static final DeferredBlock<CrateBlock> CRATE = registerBlockAndItem("crate", CrateBlock::new,
            BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD).mapColor(MapColor.WOOD));
    // TODO: Option to empty the tank
    public static final DeferredBlock<TankBlock> TANK = registerBlockAndItem("tank", TankBlock::new,
            BlockBehaviour.Properties.of().strength(0.3f).sound(SoundType.GLASS));

    // Fluids
    public static final DeferredBlock<LiquidBlock> OIL_FLUID = BLOCKS.register("oil_block",
            () -> new LiquidBlock(FPFluids.OIL_SOURCE.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA)));

    public static <T extends Block> DeferredBlock<T> registerBlockAndItem(String name, Function<BlockBehaviour.Properties, T> blockConstructor, BlockBehaviour.Properties props) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, blockConstructor, props);
        DeferredItem<BlockItem> item = FPItems.ITEMS.registerSimpleBlockItem(toReturn);
        FPItems.BLOCK_ITEMS.add(item);
        FPItems.TAB_ITEMS.add(item);
        return toReturn;
    }
}
