package com.thepigcat.fancy_pipes.content.blocks;

import com.mojang.serialization.MapCodec;
import com.thepigcat.fancy_pipes.api.blocks.PipeBlock;
import com.thepigcat.fancy_pipes.util.CapabilityUtils;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemPipeBlock extends PipeBlock {
    public ItemPipeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canConnectTo(BlockEntity connectTo) {
        return CapabilityUtils.itemHandlerCapability(connectTo) != null;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(ItemPipeBlock::new);
    }
}
