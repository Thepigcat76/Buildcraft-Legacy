package com.thepigcat.buildcraft.api.blocks;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.blockentities.PipeBlockEntity;
import com.thepigcat.buildcraft.content.blockentities.ItemPipeBE;
import com.thepigcat.buildcraft.util.BlockUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class PipeBlock extends BaseEntityBlock {
    public static final EnumProperty<PipeState>[] CONNECTION = new EnumProperty[6];

    public final int border;
    public final VoxelShape shapeCenter;
    public final VoxelShape shapeD;
    public final VoxelShape shapeU;
    public final VoxelShape shapeN;
    public final VoxelShape shapeS;
    public final VoxelShape shapeW;
    public final VoxelShape shapeE;
    public final VoxelShape[] shapes;

    static {
        for (Direction dir : Direction.values()) {
            CONNECTION[dir.get3DDataValue()] = EnumProperty.create(dir.getSerializedName(), PipeState.class);
        }
    }

    public PipeBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(CONNECTION[0], PipeState.NONE)
                .setValue(CONNECTION[1], PipeState.NONE)
                .setValue(CONNECTION[2], PipeState.NONE)
                .setValue(CONNECTION[3], PipeState.NONE)
                .setValue(CONNECTION[4], PipeState.NONE)
                .setValue(CONNECTION[5], PipeState.NONE)
        );
        int width = 10;
        border = (16 - width) / 2;
        int B0 = border;
        int B1 = 16 - border;
        shapeCenter = box(B0, B0, B0, B1, B1, B1);
        shapeD = box(B0, 0, B0, B1, B0, B1);
        shapeU = box(B0, B1, B0, B1, 16, B1);
        shapeN = box(B0, B0, 0, B1, B1, B0);
        shapeS = box(B0, B0, B1, B1, B1, 16);
        shapeW = box(0, B0, B0, B0, B1, B1);
        shapeE = box(B1, B0, B0, 16, B1, B1);
        shapes = new VoxelShape[64];
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        int index = 0;

        for (Direction direction : Direction.values()) {
            if (blockState.getValue(CONNECTION[direction.get3DDataValue()]) != PipeState.NONE) {
                index |= 1 << direction.get3DDataValue();
            }
        }

        return getShape(index);
    }

    public VoxelShape getShape(int i) {
        if (shapes[i] == null) {
            shapes[i] = shapeCenter;

            if (((i >> 0) & 1) != 0) {
                shapes[i] = Shapes.or(shapes[i], shapeD);
            }

            if (((i >> 1) & 1) != 0) {
                shapes[i] = Shapes.or(shapes[i], shapeU);
            }

            if (((i >> 2) & 1) != 0) {
                shapes[i] = Shapes.or(shapes[i], shapeN);
            }

            if (((i >> 3) & 1) != 0) {
                shapes[i] = Shapes.or(shapes[i], shapeS);
            }

            if (((i >> 4) & 1) != 0) {
                shapes[i] = Shapes.or(shapes[i], shapeW);
            }

            if (((i >> 5) & 1) != 0) {
                shapes[i] = Shapes.or(shapes[i], shapeE);
            }
        }

        return shapes[i];
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return getBlockEntityType().create(blockPos, blockState);
    }

    protected abstract BlockEntityType<? extends PipeBlockEntity<?>> getBlockEntityType();

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, getBlockEntityType(), (beLevel, bePos, beState, be) -> be.tick());
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONNECTION[0], CONNECTION[1], CONNECTION[2], CONNECTION[3], CONNECTION[4], CONNECTION[5]);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState blockState, Direction facingDirection, BlockState facingBlockState, LevelAccessor level, BlockPos blockPos, BlockPos facingBlockPos) {
        int connectionIndex = facingDirection.get3DDataValue();
        PipeBlockEntity<?> pipeBE = BlockUtils.getBE(PipeBlockEntity.class, level, blockPos);
        PipeState connectionType = getConnectionType(level, blockPos, blockState, facingDirection, facingBlockPos);
        if (connectionType != PipeState.NONE) {
            pipeBE.getDirections().add(facingDirection);
            return blockState.setValue(CONNECTION[connectionIndex], connectionType);
        } else if (facingBlockState.isEmpty()) {
            pipeBE.getDirections().remove(facingDirection);
            return blockState.setValue(CONNECTION[connectionIndex], PipeState.NONE);
        }
        setPipeProperties(pipeBE, blockState);

        return blockState;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = defaultBlockState();

        for (Direction direction : Direction.values()) {
            int connectionIndex = direction.get3DDataValue();
            BlockPos facingBlockPos = blockPos.relative(direction);

            blockState = blockState.setValue(CONNECTION[connectionIndex], getConnectionType(level, blockPos, blockState, direction, facingBlockPos));
        }

        return blockState;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        PipeBlockEntity<?> be = BlockUtils.getBE(PipeBlockEntity.class, level, pos);
        setPipeProperties(be, state);
        level.invalidateCapabilities(pos);
    }

    public static void setPipeProperties(PipeBlockEntity<?> be) {
        setPipeProperties(be, be.getBlockState());
    }

    public static void setPipeProperties(PipeBlockEntity<?> be, BlockState state) {
        Set<Direction> directions = new ObjectArraySet<>();
        for (Direction direction : Direction.values()) {
            PipeState pipeState = state.getValue(CONNECTION[direction.get3DDataValue()]);
            if (pipeState != PipeState.NONE) {
                directions.add(direction);
                if (pipeState == PipeState.EXTRACTING) {
                    be.extracting = direction;
                }
            }
        }
        BuildcraftLegacy.LOGGER.debug("set Dirs: {}", directions);
        be.setDirections(directions);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            ItemPipeBE be = BlockUtils.getBE(ItemPipeBE.class, level, pos);
            player.sendSystemMessage(Component.literal("Item: " + be.getItemHandler().getStackInSlot(0)));
        }
        return ItemInteractionResult.SUCCESS;
    }

    public abstract PipeState getConnectionType(LevelAccessor level, BlockPos pipePos, BlockState pipeState, Direction connectionDirection, BlockPos connectPos);

    public enum PipeState implements StringRepresentable {
        EXTRACTING("extracting"),
        CONNECTED("connected"),
        NONE("none");

        private final String name;

        PipeState(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
