package com.thepigcat.buildcraft.content.blocks;

import com.mojang.serialization.MapCodec;
import com.thepigcat.buildcraft.api.blockentities.ContainerBlockEntity;
import com.thepigcat.buildcraft.api.blocks.EngineBlock;
import com.thepigcat.buildcraft.content.blockentities.CombustionEngineBE;
import com.thepigcat.buildcraft.content.blockentities.TankBE;
import com.thepigcat.buildcraft.registries.BCBlockEntities;
import com.thepigcat.buildcraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class CombustionEngineBlock extends EngineBlock {
    public CombustionEngineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends ContainerBlockEntity> getBlockEntityType() {
        return BCBlockEntities.COMBUSTION_ENGINE.get();
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CombustionEngineBlock::new);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        IFluidHandler itemFluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);

        if (itemFluidHandler != null) {
            CombustionEngineBE be = BlockUtils.getBe(CombustionEngineBE.class, level, pos);
            IFluidHandler tankFluidHandler = be.getFluidHandler();

            IFluidHandler fluidHandler0 = tankFluidHandler;
            IFluidHandler fluidHandler1 = itemFluidHandler;

            if (!itemFluidHandler.getFluidInTank(0).isEmpty()) {
                fluidHandler0 = itemFluidHandler;
                fluidHandler1 = tankFluidHandler;
            }

            FluidStack drained = fluidHandler0.drain(fluidHandler0.getFluidInTank(0), IFluidHandler.FluidAction.EXECUTE);
            int filled = fluidHandler1.fill(drained, IFluidHandler.FluidAction.EXECUTE);
            fluidHandler0.fill(drained.copyWithAmount(drained.getAmount() - filled), IFluidHandler.FluidAction.EXECUTE);

            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}