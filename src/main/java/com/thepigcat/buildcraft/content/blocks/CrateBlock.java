package com.thepigcat.buildcraft.content.blocks;

import com.mojang.serialization.MapCodec;
import com.thepigcat.buildcraft.BCConfig;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.content.blockentities.CrateBE;
import com.thepigcat.buildcraft.data.BCDataComponents;
import com.thepigcat.buildcraft.data.components.BigStackContainerContents;
import com.thepigcat.buildcraft.registries.BCBlockEntities;
import com.thepigcat.buildcraft.util.BlockUtils;
import com.thepigcat.buildcraft.util.CapabilityUtils;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrateBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CrateBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CrateBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BCBlockEntities.CRATE.get().create(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return stateForFacingPlacement(this, context);
    }

    public static BlockState stateForFacingPlacement(Block block, BlockPlaceContext ctx) {
        return block.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getPlayer().getDirection().getOpposite());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !BCConfig.crateRetainItems) {
            CrateBE crateBE = BlockUtils.getBE(CrateBE.class, level, pos);
            Containers.dropContents(level, pos, NonNullList.of(ItemStack.EMPTY, crateBE.getItemHandler().getStackInSlot(0)));
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof CrateBE be && BCConfig.crateRetainItems) {
            ItemStack stack = new ItemStack(this);
            if (!be.getItemHandler().getStackInSlot(0).isEmpty()) {
                stack.set(BCDataComponents.CRATE_CONTENT, BigStackContainerContents.fromItems(be.getItemHandler().getItems(), be.getItemHandler().getSlotLimit()));
            }
            return List.of(stack);
        }
        return super.getDrops(state, params);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            IItemHandler itemHandler = CapabilityUtils.itemHandlerCapability(be);
            if (itemHandler != null) {
                ItemStack stackInSlot = itemHandler.getStackInSlot(0);
                if (ItemStack.isSameItemSameComponents(stackInSlot.copyWithCount(1), stack.copyWithCount(1))
                        || stackInSlot.isEmpty()) {
                    ItemStack remainder = itemHandler.insertItem(0, stack, false);
                    player.setItemInHand(hand, remainder);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return ItemInteractionResult.FAIL;
    }
}
