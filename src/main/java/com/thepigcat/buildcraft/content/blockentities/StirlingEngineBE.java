package com.thepigcat.buildcraft.content.blockentities;

import com.thepigcat.buildcraft.api.blockentities.EngineBlockEntity;
import com.thepigcat.buildcraft.content.menus.StirlingEngineMenu;
import com.thepigcat.buildcraft.registries.BCBlockEntities;
import com.thepigcat.buildcraft.util.CapabilityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StirlingEngineBE extends EngineBlockEntity implements MenuProvider {
    private int burnTime;
    private int burnProgress;

    public StirlingEngineBE(BlockPos blockPos, BlockState blockState) {
        super(BCBlockEntities.STIRLING_ENGINE.get(), blockPos, blockState);
        addItemHandler(1, (slot, item) -> item.getBurnTime(RecipeType.SMELTING) > 0);
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getBurnProgress() {
        return burnProgress;
    }

    @Override
    public void onItemsChanged(int slot) {
        IItemHandler itemHandler = getItemHandler();
        if (itemHandler != null) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            int burnProgress = stack.getBurnTime(RecipeType.SMELTING);
            if (burnProgress > 0 && this.burnProgress <= 0) {
                this.burnProgress = burnProgress;
                this.burnTime = burnProgress;
                // TODO: Replace shrink with something else
                stack.shrink(1);
            }
        }
    }

    @Override
    public boolean isActive() {
        return getBurnProgress() > 0;
    }

    @Override
    public void commonTick() {
        super.commonTick();
        IItemHandler itemHandler = getItemHandler();
        if (this.burnProgress > 0) {
            burnProgress--;
            getEnergyStorage().receiveEnergy(1, false);
        } else {
            this.burnTime = 0;
            ItemStack stack = itemHandler.getStackInSlot(0);
            int burnProgress = stack.getBurnTime(RecipeType.SMELTING);
            if (burnProgress > 0) {
                this.burnProgress = burnProgress;
                this.burnTime = burnProgress;
                stack.shrink(1);
            }
        }

    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Stirling Engine");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new StirlingEngineMenu(containerId, playerInventory, this);
    }

    @Override
    protected void loadData(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadData(tag, provider);
        this.burnProgress = tag.getInt("burnProgress");
        this.burnTime = tag.getInt("burnTime");
    }

    @Override
    protected void saveData(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveData(tag, provider);
        tag.putInt("burnProgress", burnProgress);
        tag.putInt("burnTime", burnTime);
    }
}
