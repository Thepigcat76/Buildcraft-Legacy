package com.thepigcat.buildcraft.registries;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.content.blocks.*;
import com.thepigcat.buildcraft.content.items.blocks.CrateBlockItem;
import com.thepigcat.buildcraft.content.items.blocks.TankBlockItem;
import com.thepigcat.buildcraft.data.BCDataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public final class BCBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BuildcraftLegacy.MODID);

    // PIPES
    public static final DeferredBlock<ItemPipeBlock> COBBLESTONE_ITEM_PIPE = registerBlockAndItem("cobblestone_pipe", ItemPipeBlock::new,
            BlockBehaviour.Properties.of().strength(1.5f, 6).sound(SoundType.STONE).mapColor(MapColor.STONE));
    public static final DeferredBlock<ExtractingItemPipeBlock> WOODEN_ITEM_PIPE = registerBlockAndItem("wooden_pipe", ExtractingItemPipeBlock::new,
            BlockBehaviour.Properties.of().strength(2.0f, 3).sound(SoundType.WOOD).mapColor(MapColor.WOOD));

    // ENGINES
    public static final DeferredBlock<RedstoneEngineBlock> REDSTONE_ENGINE = registerBlockAndItem("redstone_engine", RedstoneEngineBlock::new,
            BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD).mapColor(MapColor.WOOD));
    public static final DeferredBlock<StirlingEngineBlock> STIRLING_ENGINE = registerBlockAndItem("stirling_engine", StirlingEngineBlock::new,
            BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.STONE).mapColor(MapColor.STONE).requiresCorrectToolForDrops());
    public static final DeferredBlock<CombustionEngineBlock> COMBUSTION_ENGINE = registerBlockAndItem("combustion_engine", CombustionEngineBlock::new,
            BlockBehaviour.Properties.of().strength(5.0f).sound(SoundType.METAL).mapColor(MapColor.METAL).requiresCorrectToolForDrops());

    // MISC
    public static final DeferredBlock<CrateBlock> CRATE = registerBlockAndItem("crate", CrateBlock::new,
            BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD).mapColor(MapColor.WOOD),
            () -> new CrateBlockItem(new Item.Properties().component(BCDataComponents.CRATE_CONTENT, ItemContainerContents.EMPTY)));
    // TODO: Option to empty the tank
    public static final DeferredBlock<TankBlock> TANK = registerBlockAndItem("tank", TankBlock::new,
            BlockBehaviour.Properties.of().strength(0.3f).sound(SoundType.GLASS),
            () -> new TankBlockItem(new Item.Properties().component(BCDataComponents.TANK_CONTENT, SimpleFluidContent.EMPTY)));
//    public static final DeferredBlock<Block> QUARRY = registerBlockAndItem("quarry", Block::new,
//            BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD).mapColor(MapColor.WOOD));

    // Fluids
    public static final DeferredBlock<LiquidBlock> OIL_FLUID = BLOCKS.register("oil_block",
            () -> new LiquidBlock(BCFluids.OIL_SOURCE.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA)));

    public static <T extends Block> DeferredBlock<T> registerBlockAndItem(String name, Function<BlockBehaviour.Properties, T> blockConstructor, BlockBehaviour.Properties props) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, blockConstructor, props);
        DeferredItem<BlockItem> item = BCItems.ITEMS.registerSimpleBlockItem(toReturn);
        BCItems.BLOCK_ITEMS.add(item);
        BCItems.TAB_ITEMS.add(item);
        return toReturn;
    }

    public static <T extends Block> DeferredBlock<T> registerBlockAndItem(String name, Function<BlockBehaviour.Properties, T> blockConstructor, BlockBehaviour.Properties props, Supplier<BlockItem> blockItemSupplier) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, blockConstructor, props);
        DeferredItem<BlockItem> item = BCItems.ITEMS.register(name, blockItemSupplier);
        BCItems.BLOCK_ITEMS.add(item);
        BCItems.TAB_ITEMS.add(item);
        return toReturn;
    }
}
