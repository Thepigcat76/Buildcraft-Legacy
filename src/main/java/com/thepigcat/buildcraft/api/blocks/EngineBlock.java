package com.thepigcat.buildcraft.api.blocks;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.blockentities.EngineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class EngineBlock extends ContainerBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final Map<Direction, VoxelShape> SHAPES = Map.of(
            Direction.UP, Shapes.or(Block.box(4, 4, 4, 12, 16, 12), Block.box(0, 0, 0, 16, 4, 16)),
            Direction.DOWN, Shapes.or(Block.box(4, 0, 4, 12, 12, 12), Block.box(0, 12, 0, 16, 16, 16)),
            Direction.NORTH, Shapes.or(Block.box(4, 4, 0, 12, 12, 12), Block.box(0, 0, 12, 16, 16, 16)),
            Direction.EAST, Shapes.or(Block.box(4, 4, 4, 16, 12, 12), Block.box(0, 0, 0, 4, 16, 16)),
            Direction.SOUTH, Shapes.or(Block.box(4, 4, 4, 12, 12, 16), Block.box(0, 0, 0, 16, 16, 4)),
            Direction.WEST, Shapes.or(Block.box(0, 4, 4, 12, 12, 12), Block.box(12, 0, 0, 16, 16, 16))
    );

    public EngineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (level.getBlockEntity(pos) instanceof EngineBlockEntity engineBlockEntity) {
            engineBlockEntity.initCaches(newState.getValue(FACING));
        }
        level.invalidateCapabilities(pos);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state != null ? state.setValue(FACING, context.getClickedFace()) : null;
    }

    @Override
    public boolean tickingEnabled() {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }
}
