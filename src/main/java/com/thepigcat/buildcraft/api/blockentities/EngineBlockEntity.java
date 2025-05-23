package com.thepigcat.buildcraft.api.blockentities;

import com.portingdeadmods.portingdeadlibs.api.blockentities.ContainerBlockEntity;
import com.portingdeadmods.portingdeadlibs.api.utils.IOAction;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.blocks.EngineBlock;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class EngineBlockEntity extends ContainerBlockEntity implements RedstoneBlockEntity {
    private RedstoneSignalType redstoneSignalType;
    private int redstoneSignalStrength;
    private BlockCapabilityCache<IEnergyStorage, Direction> exportCache;
    private CompoundTag movementData;
    protected boolean active;
    public float movement;
    public float lastMovement;
    private boolean backward;

    private Map<Direction, Pair<IOAction, int[]>> sidedInteractions;

    public EngineBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        addEnergyStorage(getEnergyCapacity());
        this.movement = 0.5f;
        this.sidedInteractions = new HashMap<>();
        Direction facing = getBlockState().getValue(EngineBlock.FACING);
        for (Direction dir : Direction.values()) {
            if (dir != facing) {
                this.sidedInteractions.put(dir, Pair.of(IOAction.INSERT, new int[]{0}));
            }
        }
        this.redstoneSignalType = RedstoneSignalType.IGNORED;
    }

    public abstract int getEnergyCapacity();

    public abstract int getEnergyProduction();

    @Override
    public void onLoad() {
        super.onLoad();
        if (movementData != null) {
            this.backward = movementData.getBoolean("backward");
            this.movement = movementData.getFloat("movement");
            this.lastMovement = movementData.getFloat("lastMovement");
        }

        Direction facing = getBlockState().getValue(EngineBlock.FACING);
        initCaches(facing);
    }

    public void initCaches(Direction facing) {
        if (level instanceof ServerLevel serverLevel) {
            this.sidedInteractions = new HashMap<>();
            for (Direction dir : Direction.values()) {
                if (dir != facing) {
                    this.sidedInteractions.put(dir, Pair.of(IOAction.INSERT, new int[]{0}));
                }
            }
            this.exportCache = BlockCapabilityCache.create(Capabilities.EnergyStorage.BLOCK, serverLevel, worldPosition.relative(facing), facing.getOpposite());
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
            IEnergyStorage energyStorage = exportCache.getCapability();
            if (energyStorage != null) {
                int received = energyStorage.receiveEnergy(extractedEnergy, false);
                getEnergyStorage().receiveEnergy(extractedEnergy - received, false);
            }
        }
    }

    @Override
    public void setRedstoneSignalType(RedstoneSignalType redstoneSignalType) {
        this.redstoneSignalType = redstoneSignalType;
    }

    @Override
    public RedstoneSignalType getRedstoneSignalType() {
        return redstoneSignalType;
    }

    public void setRedstoneSignalStrength(int redstoneSignalStrength) {
        this.redstoneSignalStrength = redstoneSignalStrength;
    }

    public int getRedstoneSignalStrength() {
        return redstoneSignalStrength;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public <T> Map<Direction, Pair<IOAction, int[]>> getSidedInteractions(BlockCapability<T, @Nullable Direction> capability) {
        Direction facing = getBlockState().getValue(EngineBlock.FACING);
        if (capability == Capabilities.EnergyStorage.BLOCK) {
            return Map.of(facing, Pair.of(IOAction.EXTRACT, new int[0]));
        }
        return sidedInteractions;
    }

    @Override
    protected void loadData(CompoundTag tag, HolderLookup.Provider provider) {
        this.movementData = tag.getCompound("movementData");
        this.active = tag.getBoolean("active");
        this.redstoneSignalStrength = tag.getInt("signalStrength");
        this.redstoneSignalType = RedstoneSignalType.CODEC.decode(NbtOps.INSTANCE, tag.get("redstone_signal")).result().orElse(com.mojang.datafixers.util.Pair.of(RedstoneSignalType.IGNORED, new CompoundTag())).getFirst();
    }

    @Override
    protected void saveData(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag movementData = new CompoundTag();
        movementData.putBoolean("backward", backward);
        movementData.putFloat("movement", movement);
        movementData.putFloat("lastMovement", lastMovement);
        tag.put("movementData", movementData);
        tag.putBoolean("active", active);
        tag.putInt("signalStrength", this.redstoneSignalStrength);
        Optional<Tag> tag1 = RedstoneSignalType.CODEC.encodeStart(NbtOps.INSTANCE, this.redstoneSignalType).result();
        tag1.ifPresent(value -> {
            tag.put("redstone_signal", value);
        });
    }
}
