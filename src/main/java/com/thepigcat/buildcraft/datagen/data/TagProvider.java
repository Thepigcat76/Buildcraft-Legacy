package com.thepigcat.buildcraft.datagen.data;

import com.thepigcat.buildcraft.FancyPipes;
import com.thepigcat.buildcraft.registries.FPBlocks;
import com.thepigcat.buildcraft.registries.FPItems;
import com.thepigcat.buildcraft.tags.FPTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TagProvider {
    public static void createTagProviders(DataGenerator generator, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper, boolean isServer) {
        Block provider = new Block(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(isServer, provider);
        generator.addProvider(isServer, new Item(packOutput, lookupProvider, provider.contentsGetter()));
    }

    public static class Block extends BlockTagsProvider {
        public Block(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, FancyPipes.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(BlockTags.MINEABLE_WITH_AXE)
                    .add(FPBlocks.CRATE.get(), FPBlocks.WOODEN_ITEM_PIPE.get());
            tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(FPBlocks.TANK.get(), FPBlocks.COBBLESTONE_ITEM_PIPE.get());
        }
    }

    public static class Item extends ItemTagsProvider {
        public Item(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<net.minecraft.world.level.block.Block>> blockTags) {
            super(output, lookupProvider, blockTags);
        }

        @Override
        protected void addTags(HolderLookup.Provider provider) {
            tag(FPTags.Items.GEARS).add(
                    FPItems.WOODEN_GEAR.get(),
                    FPItems.STONE_GEAR.get(),
                    FPItems.IRON_GEAR.get(),
                    FPItems.GOLD_GEAR.get(),
                    FPItems.DIAMOND_GEAR.get()
            );
            tag(FPTags.Items.WOODEN_GEAR).add(FPItems.WOODEN_GEAR.get());
            tag(FPTags.Items.STONE_GEAR).add(FPItems.STONE_GEAR.get());
            tag(FPTags.Items.IRON_GEAR).add(FPItems.IRON_GEAR.get());
            tag(FPTags.Items.GOLD_GEAR).add(FPItems.GOLD_GEAR.get());
            tag(FPTags.Items.DIAMOND_GEAR).add(FPItems.DIAMOND_GEAR.get());
        }
    }
}
