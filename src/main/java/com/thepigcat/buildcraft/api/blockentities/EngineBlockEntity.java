package com.thepigcat.buildcraft.api.blockentities;

import com.google.common.collect.ImmutableMap;
import com.thepigcat.buildcraft.api.blocks.EngineBlock;
import com.thepigcat.buildcraft.api.capabilties.IOActions;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class EngineBlockEntity extends ContainerBlockEntity {
    private BlockCapabilityCache<IEnergyStorage, Direction> exportCache;
    private CompoundTag movementData;
    protected boolean active;
    public float movement;
    public float lastMovement;
    private boolean backward;

    public EngineBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        addEnergyStorage(1000);
        this.movement = 0.5f;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (movementData != null) {
            this.backward = movementData.getBoolean("backward");
            this.movement = movementData.getFloat("movement");
            this.lastMovement = movementData.getFloat("lastMovement");
        }

        if (level instanceof ServerLevel serverLevel) {
            Direction facing = getBlockState().getValue(EngineBlock.FACING);
            exportCache = BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, serverLevel, worldPosition, facing);
        }
    }

    @Override
    public void commonTick() {
        super.commonTick();
        lastMovement = movement;
        if (isActive()) {
            if (!backward) {
                movement -= 0.01f;
                if (movement < 0) {
                    this.backward = true;
                }
            } else {
                movement += 0.01f;
                if (movement >= 0.5f) {
                    this.backward = false;
                }
            }
        }

        if (!level.isClientSide()) {
            int extractedEnergy = getEnergyStorage().extractEnergy(5, false);
            exportCache.getCapability().receiveEnergy(extractedEnergy, false);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public <T> Map<Direction, Pair<IOActions, int[]>> getSidedInteractions(BlockCapability<T, @Nullable Direction> capability) {
        if (capability == Capabilities.EnergyStorage.BLOCK) {
            Direction facing = getBlockState().getValue(EngineBlock.FACING);
            return Map.of(facing, Pair.of(IOActions.EXTRACT, new int[]{0}));
        }
        return Map.of();
    }

    public static ImmutableMap<Direction, Pair<IOActions, int[]>> allBoth(int ...slots) {
        return ImmutableMap.of(
                Direction.NORTH, Pair.of(IOActions.BOTH, slots),
                Direction.EAST, Pair.of(IOActions.BOTH, slots),
                Direction.SOUTH, Pair.of(IOActions.BOTH, slots),
                Direction.WEST, Pair.of(IOActions.BOTH, slots),
                Direction.UP, Pair.of(IOActions.BOTH, slots),
                Direction.DOWN, Pair.of(IOActions.BOTH, slots)
        );
    }

    public static ImmutableMap<Direction, Pair<IOActions, int[]>> allInsert(int ...slots) {
        return ImmutableMap.of(
                Direction.NORTH, Pair.of(IOActions.INSERT, slots),
                Direction.EAST, Pair.of(IOActions.INSERT, slots),
                Direction.SOUTH, Pair.of(IOActions.INSERT, slots),
                Direction.WEST, Pair.of(IOActions.INSERT, slots),
                Direction.UP, Pair.of(IOActions.INSERT, slots),
                Direction.DOWN, Pair.of(IOActions.INSERT, slots)
        );
    }

    public static ImmutableMap<Direction, Pair<IOActions, int[]>> allExtract(int ...slots) {
        return ImmutableMap.of(
                Direction.NORTH, Pair.of(IOActions.EXTRACT, slots),
                Direction.EAST, Pair.of(IOActions.EXTRACT, slots),
                Direction.SOUTH, Pair.of(IOActions.EXTRACT, slots),
                Direction.WEST, Pair.of(IOActions.EXTRACT, slots),
                Direction.UP, Pair.of(IOActions.EXTRACT, slots),
                Direction.DOWN, Pair.of(IOActions.EXTRACT, slots)
        );
    }

    @Override
    protected void loadData(CompoundTag tag, HolderLookup.Provider provider) {
        this.movementData = tag.getCompound("movementData");
        this.active = tag.getBoolean("active");
    }

    @Override
    protected void saveData(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag movementData = new CompoundTag();
        movementData.putBoolean("backward", backward);
        movementData.putFloat("movement", movement);
        movementData.putFloat("lastMovement", lastMovement);
        tag.put("movementData", movementData);
        tag.putBoolean("active", active);
    }
}
