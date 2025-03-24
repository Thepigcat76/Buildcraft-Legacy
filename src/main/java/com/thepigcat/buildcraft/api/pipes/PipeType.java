package com.thepigcat.buildcraft.api.pipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @param blockConstructor Constructor for the block of the pipe. Takes in Block properties and returns the block
 * @param blockItemConstructor Constructor for the block item of the pipe. Takes in the Block, Item Properties and returns the block item
 * @param blockModelDefinition Function for generating the block model definition aka blockstate. Takes in the pipe value, the texture for the pipe and returns the model as a json string
 * @param defaultBlockModel Function for generating the block model. Takes in the pipe value, the texture for the pipe and returns the model as a json string
 * @param defaultItemModel Function for generating the item model. Takes in the pipe value, the block id path (we can assume that the namespace is always {@link com.thepigcat.buildcraft.BuildcraftLegacy.MODID}) of the pipe and returns the model as a json string
 * @param models The models that the blockModelDefinition consists of. By default, these are "base" and "connection"
 * @param <B>
 * @param <I>
 */
public record PipeType<B extends Block, I extends BlockItem>(Function<BlockBehaviour.Properties, B> blockConstructor,
                                                             BiFunction<Block, Item.Properties, I> blockItemConstructor,
                                                             BiFunction<Pipe, ResourceLocation, String> blockModelDefinition,
                                                             BiFunction<Pipe, ResourceLocation, String> defaultBlockModel,
                                                             BiFunction<Pipe, String, String> defaultItemModel,
                                                             List<String> models) {
}
