package com.thepigcat.fancy_pipes.content.blockentities;

import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.content.blocks.PipeBlock;
import com.thepigcat.fancy_pipes.registries.FPBlockEntities;
import com.thepigcat.fancy_pipes.util.BlockUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PipeBlockEntity extends BlockEntity {
    public Direction extracting;

    private final Map<Direction, BlockCapabilityCache<IItemHandler, Direction>> capabilityCaches;
    private Set<Direction> directions;

    private final ItemStackHandler itemHandler;
    public Direction from;
    public Direction to;

    public PipeBlockEntity(BlockPos pos, BlockState blockState) {
        super(FPBlockEntities.PIPE.get(), pos, blockState);
        this.capabilityCaches = new HashMap<>(6);
        this.directions = new ObjectArraySet<>(6);
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
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel) {
            Direction[] directions = Direction.values();
            for (Direction direction : directions) {
                capabilityCaches.put(direction, BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, serverLevel, worldPosition.relative(direction), direction));
            }
        }
    }

    public void tick() {
        if (!level.isClientSide()) {
            if (this.extracting != null) {
                IItemHandler itemHandler = capabilityCaches.get(this.extracting).getCapability();
                if (itemHandler != null) {
                    ItemStack extractedItem = itemHandler.extractItem(0, 64, false);
                    this.itemHandler.insertItem(0, extractedItem, false);
                    this.from = this.extracting;
                    FancyPipes.LOGGER.debug("Extracting from chest");
                }
            }

            Set<Direction> directions = new ObjectArraySet<>(this.directions);
            directions.remove(this.from);

            for (Direction direction : directions) {
                IItemHandler itemHandler = capabilityCaches.get(direction).getCapability();
                if (itemHandler != null) {
                    ItemStack itemStack = this.itemHandler.extractItem(0, 64, false);
                    itemHandler.insertItem(0, itemStack, false);

                    PipeBlockEntity be = BlockUtils.getBe(PipeBlockEntity.class, level, worldPosition.relative(direction));
                    if (be != null) {
                        be.from = direction.getOpposite();
                    }
                }
            }
//
//            for (Direction direction : Direction.values()) {
//                BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));
//                if (blockEntity instanceof PipeBlockEntity) {
//                    if (itemHandler != null) {
//                        ItemStack stack = itemHandler.getStackInSlot(0).copy();
//                        this.itemHandler.insertItem(0, stack, false);
//                        itemHandler.extractItem(0, stack.getCount(), false);
//                        this.from = direction;
//                    }
//                } else {
//
//                }
//            }
        }
    }

    public void setDirections(Set<Direction> directions) {
        this.directions = directions;
    }

    public Set<Direction> getDirections() {
        return directions;
    }

    public ItemStackHandler getItemHandler(Direction direction) {
        return directions.contains(direction) ? itemHandler : null;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.itemHandler.deserializeNBT(registries, tag.getCompound("item_handler"));
        int extractingIndex = tag.getInt("extracting");
        this.extracting = extractingIndex >= 0 ? Direction.values()[extractingIndex] : null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("item_handler", this.itemHandler.serializeNBT(registries));
        tag.putInt("extracting", this.extracting != null ? this.extracting.ordinal() : -1);
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
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }
}
