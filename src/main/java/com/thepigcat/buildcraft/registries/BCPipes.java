package com.thepigcat.buildcraft.registries;

import com.mojang.datafixers.util.Either;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.pipes.PipeHolder;
import com.thepigcat.buildcraft.util.PipeRegistrationHelper;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public final class BCPipes {
    public static final PipeRegistrationHelper HELPER = new PipeRegistrationHelper(BuildcraftLegacy.MODID);

    public static final PipeHolder WOODEN = HELPER.registerPipe("wooden", BCPipeTypes.EXTRACTING, "Wooden Pipe", 0.25f, List.of(
            BuildcraftLegacy.rl("block/wooden_pipe"),
            BuildcraftLegacy.rl("block/wooden_pipe_extracting")
    ), Either.right(Blocks.OAK_PLANKS));
    public static final PipeHolder COBBLESTONE = HELPER.registerPipe("cobblestone", BCPipeTypes.DEFAULT, "Cobblestone Pipe", 0.25f, List.of(
            BuildcraftLegacy.rl("block/cobblestone_pipe")
    ), Either.right(Blocks.COBBLESTONE));
    public static final PipeHolder DIAMOND = HELPER.registerPipe("diamond", BCPipeTypes.EXTRACTING, "Diamond Pipe", 0.5f, List.of(
            BuildcraftLegacy.rl("block/diamond_pipe"),
            BuildcraftLegacy.rl("block/diamond_pipe_extracting")
    ), Either.right(Blocks.DIAMOND_BLOCK));
    public static final PipeHolder GOLD = HELPER.registerPipe("gold", BCPipeTypes.DEFAULT, "Gold Pipe", 0.5f, List.of(
            BuildcraftLegacy.rl("block/gold_pipe")
    ), Either.right(Blocks.GOLD_BLOCK));
}
