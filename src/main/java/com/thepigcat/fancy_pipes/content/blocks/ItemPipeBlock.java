package com.thepigcat.fancy_pipes.content.blocks;

import com.mojang.serialization.MapCodec;
import com.thepigcat.fancy_pipes.api.blockentities.PipeBlockEntity;
import com.thepigcat.fancy_pipes.api.blocks.PipeBlock;
import com.thepigcat.fancy_pipes.registries.FPBlockEntities;
import com.thepigcat.fancy_pipes.util.CapabilityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ItemPipeBlock extends PipeBlock {
    public ItemPipeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public PipeState getConnectionType(LevelAccessor level, BlockPos pipePos, BlockState pipeState, Direction connectionDirection, BlockPos connectPos) {
        BlockEntity be = level.getBlockEntity(connectPos);
        BlockState blockState = level.getBlockState(connectPos);
        if (be != null
                && CapabilityUtils.itemHandlerCapability(be, connectionDirection) != null
                || blockState.is(this)) {
            return PipeState.CONNECTED;
        }
        return PipeState.NONE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(ItemPipeBlock::new);
    }

    @Override
    protected BlockEntityType<? extends PipeBlockEntity<?>> getBlockEntityType() {
        return FPBlockEntities.ITEM_PIPE.get();
    }
}
