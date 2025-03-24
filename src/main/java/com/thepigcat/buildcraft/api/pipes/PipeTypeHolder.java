package com.thepigcat.buildcraft.api.pipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public record PipeTypeHolder<B extends Block, I extends BlockItem>(ResourceLocation key, PipeType<B, I> value) {
}
