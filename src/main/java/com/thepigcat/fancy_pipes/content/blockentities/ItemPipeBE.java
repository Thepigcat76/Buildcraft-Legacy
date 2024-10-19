package com.thepigcat.fancy_pipes.content.blockentities;

import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.api.blockentities.PipeBlockEntity;
import com.thepigcat.fancy_pipes.networking.SyncPipeDirectionPayload;
import com.thepigcat.fancy_pipes.networking.SyncPipeMovementPayload;
import com.thepigcat.fancy_pipes.util.BlockUtils;
import com.thepigcat.fancy_pipes.util.CapabilityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemPipeBE extends PipeBlockEntity<IItemHandler> {
    private final ItemStackHandler itemHandler;

    public Direction from;
    public Direction to;
    public float movement;
    public float lastMovement;
    protected long lastWorldTick;

    public ItemPipeBE(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
        this.itemHandler = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (!level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }
        };
    }

    @Override
    protected BlockCapability<IItemHandler, Direction> getCapType() {
        return Capabilities.ItemHandler.BLOCK;
    }

    public void tick() {
        long worldTick = level.getGameTime();
        if (this.lastWorldTick == worldTick)
            return;
        this.lastWorldTick = worldTick;

        // If pipe can extract, extract from itemhandler on extracting side
        if (!level.isClientSide() && level.getGameTime() % 50 == 0) {
            if (this.extracting != null) {
                IItemHandler extractingHandler = capabilityCaches.get(this.extracting).getCapability();
                if (extractingHandler != null) {
                    ItemStack stack = extractingHandler.extractItem(0, 64, false);
                    this.itemHandler.insertItem(0, stack, false);

                    this.from = this.extracting;

                    List<Direction> directions = new ArrayList<>(this.directions);
                    directions.remove(this.extracting);

                    if (!directions.isEmpty()) {
                        this.to = directions.getFirst();
                    }

                    PacketDistributor.sendToAllPlayers(new SyncPipeDirectionPayload(this.getBlockPos(), Optional.ofNullable(this.from), Optional.ofNullable(this.to)));
                }
            }
        }

        // handle item transmission
        if (!level.isClientSide() && this.movement >= 1f) {
            if (this.to != null) {
                IItemHandler insertingHandler = capabilityCaches.get(this.to).getCapability();
                if (insertingHandler != null) {

                    ItemStack stack = this.itemHandler.extractItem(0, 64, false);
                    insertingHandler.insertItem(0, stack, false);

                    ItemPipeBE blockEntity = BlockUtils.getBe(ItemPipeBE.class, level, worldPosition.relative(this.to));


                    if (blockEntity != null) {
                        List<Direction> directions = new ArrayList<>(blockEntity.directions);
                        directions.remove(this.to.getOpposite());

                        FancyPipes.LOGGER.debug("next dirs: {}", directions);

                        blockEntity.from = this.to.getOpposite();

                        if (!directions.isEmpty()) {
                            blockEntity.to = directions.getFirst();
                        }
                        blockEntity.lastMovement = Math.abs(1 - this.lastMovement);
                        blockEntity.movement = Math.abs(1 - this.movement);
                        PacketDistributor.sendToAllPlayers(new SyncPipeMovementPayload(blockEntity.getBlockPos(), blockEntity.movement, blockEntity.lastMovement));

                        PacketDistributor.sendToAllPlayers(new SyncPipeDirectionPayload(blockEntity.getBlockPos(), Optional.ofNullable(blockEntity.from), Optional.ofNullable(blockEntity.to)));
                    }
                }

            }

        }

        if (!this.itemHandler.getStackInSlot(0).isEmpty()) {
            this.lastMovement = this.movement;
            this.movement += 0.01f;
        } else {
            lastMovement = 0;
            movement = 0;
        }
    }

    public ItemStackHandler getItemHandler(Direction direction) {
        return itemHandler;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
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
}
