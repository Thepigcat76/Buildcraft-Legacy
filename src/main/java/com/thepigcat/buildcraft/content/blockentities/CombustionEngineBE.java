package com.thepigcat.buildcraft.content.blockentities;

import com.thepigcat.buildcraft.api.blockentities.EngineBlockEntity;
import com.thepigcat.buildcraft.content.menus.CombustionEngineMenu;
import com.thepigcat.buildcraft.registries.BCBlockEntities;
import com.thepigcat.buildcraft.tags.BCTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CombustionEngineBE extends EngineBlockEntity implements MenuProvider {
    private int burnProgress;

    public CombustionEngineBE(BlockPos blockPos, BlockState blockState) {
        super(BCBlockEntities.COMBUSTION_ENGINE.get(), blockPos, blockState);
        addFluidTank(1000, fluidStack -> fluidStack.is(BCTags.Fluids.COMBUSTION_FUEL));
    }

    @Override
    public void commonTick() {
        super.commonTick();
        IFluidHandler fluidHandler = getFluidHandler();
        if (!fluidHandler.getFluidInTank(0).isEmpty()) {
            burnProgress++;
            if (!level.isClientSide()) {
                getEnergyStorage().receiveEnergy(1, false);
                if (burnProgress % 3 == 0) {
                    fluidHandler.drain(1, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        } else {
            burnProgress = 0;
        }
    }

    @Override
    public boolean isActive() {
        return burnProgress > 0;
    }

    public int getBurnProgress() {
        return burnProgress;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Combustion Engine");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new CombustionEngineMenu(containerId, playerInventory, this);
    }

    @Override
    protected void loadData(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadData(tag, provider);
        this.burnProgress = tag.getInt("burnProgress");
    }

    @Override
    protected void saveData(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveData(tag, provider);
        tag.putInt("burnProgress", burnProgress);
    }
}
