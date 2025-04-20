package com.thepigcat.buildcraft.content.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.api.blockentities.ContainerBlockEntity;
import com.portingdeadmods.portingdeadlibs.api.blocks.ContainerBlock;
import com.thepigcat.buildcraft.BCConfig;
import com.thepigcat.buildcraft.content.blockentities.TankBE;
import com.thepigcat.buildcraft.data.BCDataComponents;
import com.thepigcat.buildcraft.registries.BCBlockEntities;
import com.thepigcat.buildcraft.util.BlockUtils;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TankBlock extends ContainerBlock {
    public static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);
    public static final BooleanProperty TOP_JOINED = BooleanProperty.create("top_joined");
    public static final BooleanProperty BOTTOM_JOINED = BooleanProperty.create("bottom_joined");

    public TankBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TOP_JOINED, false).setValue(BOTTOM_JOINED, false));
    }

    @Override
    public boolean tickingEnabled() {
        return false;
    }

    @Override
    public BlockEntityType<? extends ContainerBlockEntity> getBlockEntityType() {
        return BCBlockEntities.TANK.get();
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(TankBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(TOP_JOINED, BOTTOM_JOINED));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        TankBE be = BlockUtils.getBE(TankBE.class, level, pos);
        IFluidHandler itemFluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        IFluidHandler tankFluidHandler = be.getFluidHandler();

        if (itemFluidHandler != null && !(stack.getItem() instanceof BucketItem)) {
            FluidStack fluidInItemTank = itemFluidHandler.getFluidInTank(0);
            IFluidHandler fluidHandler0 = tankFluidHandler;
            IFluidHandler fluidHandler1 = itemFluidHandler;

            if (!fluidInItemTank.isEmpty()) {
                fluidInItemTank.getFluid().getPickupSound().ifPresent(player::playSound);
                fluidHandler0 = itemFluidHandler;
                fluidHandler1 = tankFluidHandler;
            } else {
                SoundEvent sound = tankFluidHandler.getFluidInTank(0).getFluidType().getSound(SoundActions.BUCKET_EMPTY);
                if (sound != null) {
                    player.playSound(sound);
                }
            }

            FluidStack drained = fluidHandler0.drain(fluidHandler0.getFluidInTank(0), IFluidHandler.FluidAction.EXECUTE);
            int filled = fluidHandler1.fill(drained, IFluidHandler.FluidAction.EXECUTE);
            fluidHandler0.fill(drained.copyWithAmount(drained.getAmount() - filled), IFluidHandler.FluidAction.EXECUTE);

            return ItemInteractionResult.SUCCESS;
        } else if (itemFluidHandler != null && stack.getItem() instanceof BucketItem) {
            FluidStack fluidInItemTank = itemFluidHandler.getFluidInTank(0);
            if (fluidInItemTank.isEmpty() && tankFluidHandler.drain(1000, IFluidHandler.FluidAction.SIMULATE).getAmount() == 1000) {
                ItemStack filledBucket = ItemUtils.createFilledResult(stack, player, tankFluidHandler.drain(1000, IFluidHandler.FluidAction.EXECUTE).getFluid().getBucket().getDefaultInstance());
                player.setItemInHand(hand, filledBucket);
                tankFluidHandler.getFluidInTank(0).getFluid().getPickupSound().ifPresent(player::playSound);
                return ItemInteractionResult.SUCCESS;
            } else if (!fluidInItemTank.isEmpty() && tankFluidHandler.fill(fluidInItemTank.copyWithAmount(1000), IFluidHandler.FluidAction.SIMULATE) == 1000) {
                tankFluidHandler.fill(fluidInItemTank.copyWithAmount(1000), IFluidHandler.FluidAction.EXECUTE);
                ItemStack emptyBucket = ItemUtils.createFilledResult(stack, player, BucketItem.getEmptySuccessItem(stack, player));
                player.setItemInHand(hand, emptyBucket);
                SoundEvent sound = tankFluidHandler.getFluidInTank(0).getFluidType().getSound(SoundActions.BUCKET_EMPTY);
                if (sound != null) {
                    player.playSound(sound);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        if (!level.isClientSide()) {
            player.sendSystemMessage(Component.literal("Bottom pos: " + BlockUtils.getBE(TankBE.class, level, pos).getBottomTankPos()));
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        boolean topJoined = level.getBlockState(clickedPos.above()).is(this);
        boolean bottomJoined = level.getBlockState(clickedPos.below()).is(this);
        return state != null ? state.setValue(TOP_JOINED, topJoined).setValue(BOTTOM_JOINED, bottomJoined) : null;
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        TankBE tankBE = BlockUtils.getBE(TankBE.class, level, pos);
        boolean value = neighborState.is(this);
        if (direction == Direction.UP) {
            tankBE.setTopJoined(value);
            return state.setValue(TOP_JOINED, value);
        } else if (direction == Direction.DOWN) {
            tankBE.setBottomJoined(value);
            return state.setValue(BOTTOM_JOINED, value);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        TankBE tankBE = BlockUtils.getBE(TankBE.class, level, pos);
        tankBE.setTopJoined(state.getValue(TOP_JOINED));
        tankBE.setBottomJoined(state.getValue(BOTTOM_JOINED));
        if (!state.getValue(BOTTOM_JOINED) && !state.getValue(TOP_JOINED)) {
            tankBE.setBottomTankPos(pos);
        }

        BlockPos bottomPos = pos;
        if (state.getValue(BOTTOM_JOINED)) {
            while (level.getBlockState(bottomPos.below()).is(this)) {
                bottomPos = bottomPos.below();
            }
        }

        BlockPos curPos = bottomPos;
        while (level.getBlockState(curPos).is(this)) {
            BlockUtils.getBE(TankBE.class, level, curPos).setBottomTankPos(bottomPos);
            curPos = curPos.above();
        }
        BlockPos topPos = curPos.below();
        int yDiff = topPos.getY() - bottomPos.getY();
        BlockUtils.getBE(TankBE.class, level, bottomPos).initTank(yDiff + 1);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (state.getValue(TOP_JOINED) && state.getValue(BOTTOM_JOINED)) {
                splitTank(level, pos);
            } else if (state.getValue(TOP_JOINED) && !state.getValue(BOTTOM_JOINED)) {
                moveFluidsAbove(level, pos);
            } else if (!state.getValue(TOP_JOINED) && state.getValue(BOTTOM_JOINED)) {
                removeFluidFromBottomTank(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private static void removeFluidFromBottomTank(Level level, BlockPos pos) {
        TankBE removedTank = BlockUtils.getBE(TankBE.class, level, pos);
        FluidStack fluidStack = removedTank.getFluidHandler().getFluidInTank(0);
        int tank = removedTank.getBlockPos().getY() - removedTank.getBottomTankPos().getY();
        int prevFluidAmount = tank * BCConfig.tankCapacity;
        int fluidAmount = Math.min(fluidStack.getAmount() - prevFluidAmount, BCConfig.tankCapacity);
        removedTank.getFluidHandler().drain(fluidAmount, IFluidHandler.FluidAction.EXECUTE);
    }

    private static void moveFluidsAbove(Level level, BlockPos pos) {
        TankBE removedTank = BlockUtils.getBE(TankBE.class, level, pos);
        TankBE aboveTank = BlockUtils.getBE(TankBE.class, level, pos.above());
        FluidStack fluidInTank = removedTank.getFluidTank().getFluidInTank(0);
        int amount = Math.max(fluidInTank.getAmount() - BCConfig.tankCapacity, 0);
        aboveTank.initialFluid = fluidInTank.copyWithAmount(amount);
    }

    private static void splitTank(Level level, BlockPos pos) {
        TankBE removedTank = BlockUtils.getBE(TankBE.class, level, pos);
        FluidStack fluidStack = removedTank.getFluidHandler().getFluidInTank(0);
        int tank = removedTank.getBlockPos().getY() - removedTank.getBottomTankPos().getY();

        TankBE topTank = BlockUtils.getBE(TankBE.class, level, pos.above());
        int topFluidAmount = Math.max(fluidStack.getAmount() - ((tank + 1) * BCConfig.tankCapacity), 0);
        topTank.initialFluid = fluidStack.copyWithAmount(topFluidAmount);

        int prevFluidAmount = tank * BCConfig.tankCapacity;
        int fluidAmount = Math.min(fluidStack.getAmount() - prevFluidAmount, BCConfig.tankCapacity);
        BlockPos bottomTankPos = removedTank.getBottomTankPos();

        TankBE bottomTank = BlockUtils.getBE(TankBE.class, level, bottomTankPos);
        bottomTank.initialFluid = removedTank.getFluidHandler().getFluidInTank(0).copyWithAmount(fluidStack.getAmount() - topFluidAmount - fluidAmount);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof TankBE be && BCConfig.tankRetainFluids) {
            ItemStack stack = new ItemStack(this);
            if (!be.getFluidHandler().getFluidInTank(0).isEmpty()) {
                stack.set(BCDataComponents.TANK_CONTENT, SimpleFluidContent.copyOf(be.getFluidHandler().getFluidInTank(0)));
            }
            return List.of(stack);
        }
        return super.getDrops(state, params);
    }
}
