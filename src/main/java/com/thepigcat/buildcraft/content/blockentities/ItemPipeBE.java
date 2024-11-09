package com.thepigcat.buildcraft.content.blockentities;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.blockentities.PipeBlockEntity;
import com.thepigcat.buildcraft.networking.SyncPipeDirectionPayload;
import com.thepigcat.buildcraft.networking.SyncPipeMovementPayload;
import com.thepigcat.buildcraft.registries.BCBlockEntities;
import com.thepigcat.buildcraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemPipeBE extends PipeBlockEntity<IItemHandler> {
    protected final ItemStackHandler itemHandler;

    public Direction from;
    public Direction to;
    public float movement;
    public float lastMovement;

    public ItemPipeBE(BlockPos pos, BlockState blockState) {
        this(BCBlockEntities.ITEM_PIPE.get(), pos, blockState);
    }

    public ItemPipeBE(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
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
        // handle item transmission
        if (!level.isClientSide() && this.movement >= 1f) {
            if (this.to != null) {
                IItemHandler insertingHandler = capabilityCaches.get(this.to).getCapability();
                if (insertingHandler != null) {
                    ItemStack pipeContent = insertingHandler.getStackInSlot(0);

                    if (!(level.getBlockEntity(worldPosition.relative(this.to)) instanceof ItemPipeBE)) {
                        pipeContent = ItemStack.EMPTY;
                    }

                    if (pipeContent.isEmpty()) {
                        ItemStack remainder = insertItems(insertingHandler);
                        BuildcraftLegacy.LOGGER.debug("remainder: {}", remainder);
                        this.itemHandler.insertItem(0, remainder, false);

                        ItemPipeBE blockEntity = BlockUtils.getBE(ItemPipeBE.class, level, worldPosition.relative(this.to));

                        if (blockEntity != null) {
                            moveItemForward(blockEntity);
                        }

                        if (!remainder.isEmpty()) {
                            moveItemBackward();
                        }
                    } else {
                        moveItemBackward();
                    }
                }

            }

        }

        if (!this.itemHandler.getStackInSlot(0).isEmpty()) {
                this.lastMovement = this.movement;
                this.movement += 0.01f;

                if (!level.isClientSide()) {
                    BlockCapabilityCache<IItemHandler, Direction> fromCache = this.capabilityCaches.get(this.from);
                    BlockCapabilityCache<IItemHandler, Direction> toCache = this.capabilityCaches.get(this.to);
                    if (toCache == null || toCache.getCapability() == null) {
                        if (fromCache == null || fromCache.getCapability() == null) {
                            this.lastMovement = 0;
                            this.movement = 0;
                            this.to = null;
                            this.from = null;

                            PacketDistributor.sendToAllPlayers(new SyncPipeDirectionPayload(worldPosition, Optional.empty(), Optional.empty()));
                            PacketDistributor.sendToAllPlayers(new SyncPipeMovementPayload(worldPosition, this.movement, this.lastMovement));
                        } else {
                            moveItemBackward(1 - this.lastMovement, 1 - this.movement);
                        }
                    }
                }

        } else {
            lastMovement = 0;
            movement = 0;
        }
    }

    private void moveItemForward(ItemPipeBE blockEntity) {
        List<Direction> directions = new ArrayList<>(blockEntity.directions);
        directions.remove(this.to.getOpposite());

        blockEntity.from = this.to.getOpposite();

        if (!directions.isEmpty()) {
            int dirIndex = level.random.nextInt(0, directions.size());
            blockEntity.to = directions.get(dirIndex);
        } else {
            blockEntity.to = blockEntity.from;
        }

        blockEntity.lastMovement = Math.abs(1 - this.lastMovement);
        blockEntity.movement = Math.abs(1 - this.movement);

        PacketDistributor.sendToAllPlayers(new SyncPipeMovementPayload(blockEntity.getBlockPos(), blockEntity.movement, blockEntity.lastMovement));
        PacketDistributor.sendToAllPlayers(new SyncPipeDirectionPayload(blockEntity.getBlockPos(), Optional.ofNullable(blockEntity.from), Optional.ofNullable(blockEntity.to)));
    }

    private void moveItemBackward() {
        moveItemBackward(0, 0);
    }

    private void moveItemBackward(float lastMovement, float movement) {
        Direction to = this.to;
        this.to = this.from;
        this.from = to;
        this.lastMovement = lastMovement;
        this.movement = movement;

        PacketDistributor.sendToAllPlayers(new SyncPipeMovementPayload(worldPosition, this.movement, this.lastMovement));

        PacketDistributor.sendToAllPlayers(new SyncPipeDirectionPayload(worldPosition, Optional.ofNullable(this.from), Optional.ofNullable(this.to)));
    }

    /**
     * @return remainder
     */
    private ItemStack insertItems(IItemHandler insertingHandler) {
        // Get stack in pipe (only simulated)
        ItemStack toInsert = itemHandler.extractItem(0, this.itemHandler.getSlotLimit(0), false);

        return ItemHandlerHelper.insertItem(insertingHandler, toInsert, false);
    }

    public IItemHandler getItemHandler(Direction direction) {
        return itemHandler;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.itemHandler.deserializeNBT(registries, tag.getCompound("item_handler"));

        int toIndex = tag.getInt("to");
        if (toIndex != -1) {
            this.to = Direction.values()[toIndex];
        }
        int fromIndex = tag.getInt("from");
        if (fromIndex != -1) {
            this.from = Direction.values()[fromIndex];
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("item_handler", this.itemHandler.serializeNBT(registries));

        tag.putInt("to", this.to != null ? this.to.ordinal() : -1);
        tag.putInt("from", this.from != null ? this.from.ordinal() : -1);
    }
}
