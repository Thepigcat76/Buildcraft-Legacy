package com.thepigcat.buildcraft.content.blockentities;

import com.thepigcat.buildcraft.api.blockentities.EngineBlockEntity;
import com.thepigcat.buildcraft.registries.BCBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneEngineBE extends EngineBlockEntity {
    public RedstoneEngineBE(BlockPos blockPos, BlockState blockState) {
        super(BCBlockEntities.REDSTONE_ENGINE.get(), blockPos, blockState);
    }

    @Override
    public void commonTick() {
        super.commonTick();
        if (isActive() && level.getGameTime() % 10 == 0) {
            getEnergyStorage().receiveEnergy(1, false);
        }
    }
}
