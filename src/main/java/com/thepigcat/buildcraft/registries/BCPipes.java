package com.thepigcat.buildcraft.registries;

import com.mojang.datafixers.util.Either;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.pipes.PipeHolder;
import com.thepigcat.buildcraft.util.PipeRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public final class BCPipes {
    public static final PipeRegistrationHelper HELPER = new PipeRegistrationHelper(BuildcraftLegacy.MODID);

    public static final PipeHolder WOODEN = HELPER.registerPipe("wooden", BCPipeTypes.EXTRACTING, "Wooden Pipe", 0.25f, List.of(
            BuildcraftLegacy.rl("block/wooden_pipe"),
            BuildcraftLegacy.rl("block/wooden_pipe_extracting")
    ), Either.right(ResourceLocation.parse("oak_planks")), Ingredient.of(ItemTags.PLANKS), List.of(BlockTags.MINEABLE_WITH_AXE), 0);
    public static final PipeHolder COBBLESTONE = HELPER.registerPipe("cobblestone", BCPipeTypes.DEFAULT, "Cobblestone Pipe", 0.25f, List.of(
            BuildcraftLegacy.rl("block/cobblestone_pipe")
    ), Either.right(ResourceLocation.parse("cobblestone")), Ingredient.of(Tags.Items.COBBLESTONES), List.of(BlockTags.MINEABLE_WITH_PICKAXE), 1);
    public static final PipeHolder GOLD = HELPER.registerPipe("gold", BCPipeTypes.DEFAULT, "Gold Pipe", 0.5f, List.of(
            BuildcraftLegacy.rl("block/gold_pipe")
    ), Either.right(ResourceLocation.parse("gold_block")), Ingredient.of(Tags.Items.INGOTS_GOLD), List.of(BlockTags.MINEABLE_WITH_PICKAXE), 2);
    public static final PipeHolder DIAMOND = HELPER.registerPipe("diamond", BCPipeTypes.EXTRACTING, "Diamond Pipe", 0.5f, List.of(
            BuildcraftLegacy.rl("block/diamond_pipe"),
            BuildcraftLegacy.rl("block/diamond_pipe_extracting")
    ), Either.right(ResourceLocation.parse("diamond_block")), Ingredient.of(Tags.Items.GEMS_DIAMOND), List.of(BlockTags.MINEABLE_WITH_PICKAXE), 3);
}
