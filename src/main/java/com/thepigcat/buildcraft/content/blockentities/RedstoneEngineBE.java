package com.thepigcat.buildcraft.content.blockentities;

import com.thepigcat.buildcraft.BCConfig;
import com.thepigcat.buildcraft.api.blockentities.EngineBlockEntity;
import com.thepigcat.buildcraft.registries.BCBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneEngineBE extends EngineBlockEntity {
    public RedstoneEngineBE(BlockPos blockPos, BlockState blockState) {
        super(BCBlockEntities.REDSTONE_ENGINE.get(), blockPos, blockState);
    }

    @Override
    public void commonTick() {
        super.commonTick();
        if (isActive() && level.getGameTime() % 10 == 0) {
            getEnergyStorage().receiveEnergy(getEnergyProduction(), false);
        }
    }

    @Override
    public int getEnergyCapacity() {
        return BCConfig.redstoneEngineEnergyCapacity;
    }

    @Override
    public int getEnergyProduction() {
        return BCConfig.redstoneEngineEnergyProduction;
    }

    @Override
    public int emitRedstoneLevel() {
        return 0;
    }

    @Override
    public boolean isActive() {
        return getRedstoneSignalStrength() > 0;
    }
}
