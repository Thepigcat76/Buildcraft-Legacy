package com.thepigcat.fancy_pipes.content.blocks;

import com.mojang.serialization.MapCodec;
import com.thepigcat.fancy_pipes.api.blockentities.PipeBlockEntity;
import com.thepigcat.fancy_pipes.api.blocks.ExtractingPipeBlock;
import com.thepigcat.fancy_pipes.api.blocks.PipeBlock;
import com.thepigcat.fancy_pipes.content.blockentities.ExtractItemPipeBE;
import com.thepigcat.fancy_pipes.registries.FPBlockEntities;
import com.thepigcat.fancy_pipes.util.CapabilityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ExtractingItemPipeBlock extends ExtractingPipeBlock {
    public ExtractingItemPipeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public PipeState getConnectionType(LevelAccessor level, BlockPos pipePos, BlockState pipeState, Direction connectionDirection, BlockPos connectPos) {
        BlockEntity be = level.getBlockEntity(connectPos);
        BlockState connectState = level.getBlockState(connectPos);
        if (be != null && !connectState.is(this) && CapabilityUtils.itemHandlerCapability(be, connectionDirection) != null) {
            // Check that pipe is only extracting on one side and that pipe is not extracting from a pipe
            if (!isExtracting(pipeState) && !(connectState.getBlock() instanceof PipeBlock)) {
                return PipeState.EXTRACTING;
            } else {
                return PipeState.CONNECTED;
            }
        }
        return PipeState.NONE;
    }

    // Check if pipe is already extracting on one side
    private static boolean isExtracting(BlockState state) {
        for (Direction direction : Direction.values()) {
            if (state.getValue(CONNECTION[direction.get3DDataValue()]) == PipeState.EXTRACTING) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(ExtractingItemPipeBlock::new);
    }

    @Override
    protected BlockEntityType<? extends PipeBlockEntity<?>> getBlockEntityType() {
        return FPBlockEntities.EXTRACTING_ITEM_PIPE.get();
    }
}
