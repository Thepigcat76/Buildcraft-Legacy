package com.thepigcat.fancy_pipes.content.blockentities;

import com.thepigcat.fancy_pipes.FPConfig;
import com.thepigcat.fancy_pipes.registries.FPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class TankBE extends BlockEntity {
    private final FluidTank fluidTank;

    private BlockCapabilityCache<IFluidHandler, Direction> bottomCache;

    public TankBE(BlockPos pos, BlockState blockState) {
        super(FPBlockEntities.TANK.get(), pos, blockState);
        this.fluidTank = new FluidTank(FPConfig.tankCapacity);
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            this.bottomCache = BlockCapabilityCache.create(Capabilities.FluidHandler.BLOCK, serverLevel, worldPosition.below(), Direction.DOWN);
        }
    }

    public void tick() {
        if (level.getGameTime() % 10 == 0 && !level.isClientSide()) {
            if (this.bottomCache.getCapability() != null) {
                IFluidHandler fluidHandler = this.bottomCache.getCapability();

                FluidStack fluidStack = this.fluidTank.drain(20, IFluidHandler.FluidAction.EXECUTE);

                int filled = fluidHandler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);

                this.fluidTank.fill(fluidStack.copyWithAmount(filled), IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.fluidTank.readFromNBT(registries, tag.getCompound("fluidTank"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag fluidTankTag = new CompoundTag();
        this.fluidTank.writeToNBT(registries, fluidTankTag);
        tag.put("fluidTank", fluidTankTag);
    }
}
