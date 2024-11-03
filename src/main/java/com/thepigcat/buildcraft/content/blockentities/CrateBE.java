package com.thepigcat.buildcraft.content.blockentities;

import com.thepigcat.buildcraft.api.capabilties.JumboItemHandler;
import com.thepigcat.buildcraft.registries.BCBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrateBE extends BlockEntity {
    private final JumboItemHandler itemHandler;

    public CrateBE(BlockPos pos, BlockState blockState) {
        super(BCBlockEntities.CRATE.get(), pos, blockState);
        this.itemHandler = new JumboItemHandler(4096) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                ItemStack stackInSlot = getStackInSlot(slot);
                return stackInSlot.isEmpty()
                        || (stack.is(stackInSlot.getItem()) && stack.getCount() + stackInSlot.getCount() < getSlotLimit(slot));
            }

            @Override
            public void onChanged() {
                setChanged();
                if (!level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }
        };
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.itemHandler.deserializeNBT(registries, tag.getCompound("item_handler"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("item_handler", this.itemHandler.serializeNBT(registries));
    }

    public JumboItemHandler getItemHandler(Direction direction) {
        return itemHandler;
    }

    public JumboItemHandler getItemHandler() {
        return itemHandler;
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
}
