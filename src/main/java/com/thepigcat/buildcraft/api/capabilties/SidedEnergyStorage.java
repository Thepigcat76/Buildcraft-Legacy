package com.thepigcat.buildcraft.api.capabilties;


import it.unimi.dsi.fastutil.Pair;
import net.neoforged.neoforge.energy.IEnergyStorage;

public record SidedEnergyStorage(IEnergyStorage innerHandler,
                                 IOActions action) implements IEnergyStorage {
    public SidedEnergyStorage(IEnergyStorage innerHandler, Pair<IOActions, int[]> actionSlotsPair) {
        this(innerHandler, actionSlotsPair != null ? actionSlotsPair.left() : IOActions.NONE);
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        return action == IOActions.INSERT || action == IOActions.BOTH ? innerHandler.receiveEnergy(toReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return action == IOActions.EXTRACT || action == IOActions.BOTH ? innerHandler.extractEnergy(toExtract, simulate) : 0;
    }

    @Override
    public int getEnergyStored() {
        return innerHandler.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return innerHandler.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return action == IOActions.EXTRACT || action == IOActions.BOTH;
    }

    @Override
    public boolean canReceive() {
        return action == IOActions.INSERT || action == IOActions.BOTH;
    }
}
