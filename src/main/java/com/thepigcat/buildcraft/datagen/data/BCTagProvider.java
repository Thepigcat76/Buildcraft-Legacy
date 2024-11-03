package com.thepigcat.buildcraft.datagen.data;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.registries.BCBlocks;
import com.thepigcat.buildcraft.registries.BCFluids;
import com.thepigcat.buildcraft.registries.BCItems;
import com.thepigcat.buildcraft.tags.BCTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BCTagProvider {
    public static void createTagProviders(DataGenerator generator, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper, boolean isServer) {
        Block provider = new Block(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(isServer, provider);
        generator.addProvider(isServer, new Item(packOutput, lookupProvider, provider.contentsGetter()));
        generator.addProvider(isServer, new Fluid(packOutput, lookupProvider, existingFileHelper));
    }

    public static class Block extends BlockTagsProvider {
        public Block(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, BuildcraftLegacy.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(BlockTags.MINEABLE_WITH_AXE)
                    .add(BCBlocks.CRATE.get(), BCBlocks.WOODEN_ITEM_PIPE.get());
            tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(BCBlocks.TANK.get(), BCBlocks.COBBLESTONE_ITEM_PIPE.get());
        }
    }

    public static class Item extends ItemTagsProvider {
        public Item(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<net.minecraft.world.level.block.Block>> blockTags) {
            super(output, lookupProvider, blockTags);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(BCTags.Items.GEARS).add(
                    BCItems.WOODEN_GEAR.get(),
                    BCItems.STONE_GEAR.get(),
                    BCItems.IRON_GEAR.get(),
                    BCItems.GOLD_GEAR.get(),
                    BCItems.DIAMOND_GEAR.get()
            );
            tag(BCTags.Items.WOODEN_GEAR).add(BCItems.WOODEN_GEAR.get());
            tag(BCTags.Items.STONE_GEAR).add(BCItems.STONE_GEAR.get());
            tag(BCTags.Items.IRON_GEAR).add(BCItems.IRON_GEAR.get());
            tag(BCTags.Items.GOLD_GEAR).add(BCItems.GOLD_GEAR.get());
            tag(BCTags.Items.DIAMOND_GEAR).add(BCItems.DIAMOND_GEAR.get());
        }
    }

    public static class Fluid extends FluidTagsProvider {
        public Fluid(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, provider, BuildcraftLegacy.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(BCTags.Fluids.OIL).add(BCFluids.OIL_SOURCE.get(), BCFluids.OIL_FLOWING.get());
            tag(BCTags.Fluids.COMBUSTION_FUEL).addTag(BCTags.Fluids.OIL);
        }
    }

}
