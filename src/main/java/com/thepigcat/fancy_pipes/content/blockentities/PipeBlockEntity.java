package com.thepigcat.fancy_pipes.content.blockentities;

import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.FancyPipesClient;
import com.thepigcat.fancy_pipes.content.blocks.PipeBlock;
import com.thepigcat.fancy_pipes.networking.SyncPipeDirectionPayload;
import com.thepigcat.fancy_pipes.registries.FPBlockEntities;
import com.thepigcat.fancy_pipes.util.BlockUtils;
import com.thepigcat.fancy_pipes.util.CapabilityUtils;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
            if (level.getGameTime() % 20 == 0) {
                // If pipe can extract, extract from itemhandler on extracting side
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

                // handle item transmission
                if (this.to != null) {
                    IItemHandler insertingHandler = capabilityCaches.get(this.to).getCapability();
                    if (insertingHandler != null) {
                        ItemStack stack = this.itemHandler.extractItem(0, 64, false);
                        insertingHandler.insertItem(0, stack, false);

                        PipeBlockEntity blockEntity = BlockUtils.getBe(PipeBlockEntity.class, level, worldPosition.relative(this.to));

                        if (blockEntity != null) {
                            List<Direction> directions = new ArrayList<>(this.directions);
                            directions.remove(this.from);

                            blockEntity.from = this.to.getOpposite();

                            if (!directions.isEmpty()) {
                                blockEntity.to = directions.getFirst();
                            } else {
                                FancyPipes.LOGGER.debug("EEE");
                            }

                            PacketDistributor.sendToAllPlayers(new SyncPipeDirectionPayload(blockEntity.getBlockPos(), Optional.ofNullable(blockEntity.from), Optional.ofNullable(blockEntity.to)));
                        }
                    } else {
                        FancyPipes.LOGGER.debug("no tiem: {}", this.worldPosition.relative(this.to));
                    }
                }
            }
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
