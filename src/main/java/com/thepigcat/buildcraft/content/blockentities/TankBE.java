package com.thepigcat.buildcraft.content.blockentities;

import com.thepigcat.buildcraft.BCConfig;
import com.thepigcat.buildcraft.data.BCDataComponents;
import com.thepigcat.buildcraft.registries.BCBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TankBE extends BlockEntity {
    private final FluidTank fluidTank;

    private BlockCapabilityCache<IFluidHandler, Direction> bottomCache;

    public TankBE(BlockPos pos, BlockState blockState) {
        super(BCBlockEntities.TANK.get(), pos, blockState);
        this.fluidTank = new FluidTank(BCConfig.tankCapacity) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
                if (!level.isClientSide) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
        };
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public FluidTank getFluidTank(Direction direction) {
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
        if (!level.isClientSide() && !this.fluidTank.getFluidInTank(0).isEmpty()) {
            IFluidHandler fluidHandler = this.bottomCache.getCapability();
            if (fluidHandler != null) {
                FluidStack fluidStack = this.fluidTank.drain(20, IFluidHandler.FluidAction.SIMULATE);
                int filled = fluidHandler.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);

                FluidStack drained = this.fluidTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                fluidHandler.fill(drained, IFluidHandler.FluidAction.EXECUTE);
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

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("fluidTank");
    }

    @Override
    public void saveToItem(ItemStack stack, HolderLookup.Provider registries) {
        CompoundTag compoundtag = this.saveCustomOnly(registries);
        this.removeComponentsFromTag(compoundtag);
        BlockItem.setBlockEntityData(stack, this.getType(), compoundtag);
        stack.applyComponents(this.collectComponents());
        stack.set(BCDataComponents.TANK_CONTENT.get(), SimpleFluidContent.copyOf(this.fluidTank.getFluid()));
    }
}
