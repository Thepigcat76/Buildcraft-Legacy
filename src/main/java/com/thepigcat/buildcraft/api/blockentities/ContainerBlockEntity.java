package com.thepigcat.buildcraft.api.blockentities;

import com.google.common.collect.ImmutableMap;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.capabilties.IOActions;
import com.thepigcat.buildcraft.api.capabilties.SidedEnergyStorage;
import com.thepigcat.buildcraft.api.capabilties.SidedFluidHandler;
import com.thepigcat.buildcraft.api.capabilties.SidedItemHandler;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class ContainerBlockEntity extends BlockEntity {
    private @Nullable ItemStackHandler itemHandler;
    private @Nullable FluidTank fluidTank;
    private @Nullable EnergyStorage energyStorage;

    public ContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public void commonTick() {
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    public IFluidHandler getFluidHandler() {
        return fluidTank;
    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    protected ItemStackHandler getItemStackHandler() {
        return itemHandler;
    }

    protected @Nullable FluidTank getFluidTank() {
        return fluidTank;
    }

    protected EnergyStorage getEnergyStorageImpl() {
        return energyStorage;
    }

    @Override
    protected final void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(nbt, provider);
        if (this.getFluidTank() != null)
            this.getFluidTank().readFromNBT(provider, nbt.getCompound("fluid_tank"));
        if (this.getItemStackHandler() != null)
            this.getItemStackHandler().deserializeNBT(provider, nbt.getCompound("itemhandler"));
        if (this.getEnergyStorageImpl() != null)
            this.getEnergyStorageImpl().deserializeNBT(provider, nbt.get("energy_storage"));
        loadData(nbt, provider);
    }

    @Override
    protected final void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(nbt, provider);
        if (getFluidTank() != null) {
            CompoundTag tag = new CompoundTag();
            getFluidTank().writeToNBT(provider, tag);
            nbt.put("fluid_tank", tag);
        }
        if (getItemStackHandler() != null)
            nbt.put("itemhandler", getItemStackHandler().serializeNBT(provider));
        if (getEnergyStorageImpl() != null)
            nbt.put("energy_storage", getEnergyStorageImpl().serializeNBT(provider));
        saveData(nbt, provider);
    }

    protected void loadData(CompoundTag tag, HolderLookup.Provider provider) {
    }

    protected void saveData(CompoundTag tag, HolderLookup.Provider provider) {
    }

    protected final void addItemHandler(int slots) {
        addItemHandler(slots, (slot, itemStack) -> true);
    }

    protected final void addItemHandler(int slots, int slotLimit) {
        addItemHandler(slots, slotLimit, (slot, itemStack) -> true);
    }

    protected final void addItemHandler(int slots, BiPredicate<Integer, ItemStack> validation) {
        addItemHandler(slots, 64, validation);
    }

    protected final void addItemHandler(int slots, int slotLimit, BiPredicate<Integer, ItemStack> validation) {
        this.itemHandler = new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                update();
                setChanged();
                onItemsChanged(slot);
                invalidateCapabilities();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return validation.test(slot, stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return slotLimit;
            }

            @Override
            public int getSlots() {
                return slots;
            }
        };
    }

    protected final void addFluidTank(int capacityInMb) {
        addFluidTank(capacityInMb, ignored -> true);
    }

    protected final void addFluidTank(int capacityInMb, Predicate<FluidStack> validation) {
        this.fluidTank = new FluidTank(capacityInMb) {
            @Override
            protected void onContentsChanged() {
                update();
                setChanged();
                onFluidChanged();
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return validation.test(stack);
            }
        };
    }

    protected final void addEnergyStorage(int energyCapacity) {
        this.energyStorage = new EnergyStorage(energyCapacity) {
            @Override
            public int receiveEnergy(int toReceive, boolean simulate) {
                int receivedEnergy = super.receiveEnergy(toReceive, simulate);
                if (receivedEnergy > 0) {
                    update();
                    setChanged();
                    ContainerBlockEntity.this.onEnergyChanged();
                }
                return receivedEnergy;
            }

            @Override
            public int extractEnergy(int toExtract, boolean simulate) {
                int extractedEnergy = super.extractEnergy(toExtract, simulate);
                if (extractedEnergy > 0) {
                    update();
                    setChanged();
                    ContainerBlockEntity.this.onEnergyChanged();
                }
                return extractedEnergy;
            }
        };
    }

    private void update() {
        if (!level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    protected void onItemsChanged(int slot) {
    }

    protected void onFluidChanged() {
    }

    public void onEnergyChanged() {
    }

    public void drop() {
        ItemStack[] stacks = getItemHandlerStacks();
        if (stacks != null) {
            SimpleContainer inventory = new SimpleContainer(stacks);
            Containers.dropContents(this.level, this.worldPosition, inventory);
        }
    }

    public @Nullable ItemStack[] getItemHandlerStacks() {
        IItemHandler itemStackHandler = getItemHandler();

        if (itemStackHandler == null) return null;

        ItemStack[] itemStacks = new ItemStack[itemStackHandler.getSlots()];
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            itemStacks[i] = itemStackHandler.getStackInSlot(i);
        }
        return itemStacks;
    }

    public List<ItemStack> getItemHandlerStacksList() {
        IItemHandler itemStackHandler = getItemHandler();

        if (itemStackHandler == null) return null;

        int slots = itemStackHandler.getSlots();
        ObjectList<ItemStack> itemStacks = new ObjectArrayList<>(slots);
        for (int i = 0; i < slots; i++) {
            ItemStack stack = itemStackHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                itemStacks.add(stack);
            }
        }
        return itemStacks;
    }

    public <T> T getHandlerOnSide(BlockCapability<T, @Nullable Direction> capability, SidedHandlerSupplier<T> handlerSupplier, Direction direction, T baseHandler) {
        if (direction == null) {
            return baseHandler;
        }

        Map<Direction, Pair<IOActions, int[]>> ioPorts = getSidedInteractions(capability);
        if (ioPorts.containsKey(direction)) {
            if (direction == Direction.UP || direction == Direction.DOWN) {
                return handlerSupplier.get(baseHandler, ioPorts.get(direction));
            }

            if (this.getBlockState().hasProperty(BlockStateProperties.FACING)) {
                Direction localDir = this.getBlockState().getValue(BlockStateProperties.FACING);

                return getCapOnSide(handlerSupplier, direction, baseHandler, ioPorts, localDir);
            }

            if (this.getBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                Direction localDir = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);

                return getCapOnSide(handlerSupplier, direction, baseHandler, ioPorts, localDir);
            }

            BuildcraftLegacy.LOGGER.warn("Sided io for non facing block");
        }

        return null;
    }

    @Nullable
    private <T> T getCapOnSide(SidedHandlerSupplier<T> handlerSupplier, Direction direction, T baseHandler, Map<Direction, Pair<IOActions, int[]>> ioPorts, Direction localDir) {
        return switch (localDir) {
            case NORTH -> handlerSupplier.get(baseHandler, ioPorts.get(direction.getOpposite()));
            case EAST -> handlerSupplier.get(baseHandler, ioPorts.get(direction.getClockWise()));
            case SOUTH, DOWN, UP -> handlerSupplier.get(baseHandler, ioPorts.get(direction));
            case WEST -> handlerSupplier.get(baseHandler, ioPorts.get(direction.getCounterClockWise()));
        };
    }

    public IItemHandler getItemHandlerOnSide(Direction direction) {
        return getHandlerOnSide(
                Capabilities.ItemHandler.BLOCK,
                SidedItemHandler::new,
                direction,
                getItemHandler()
        );
    }

    public IFluidHandler getFluidHandlerOnSide(Direction direction) {
        return getHandlerOnSide(
                Capabilities.FluidHandler.BLOCK,
                SidedFluidHandler::new,
                direction,
                getFluidHandler()
        );
    }

    public IEnergyStorage getEnergyStorageOnSide(Direction direction) {
        return getHandlerOnSide(
                Capabilities.EnergyStorage.BLOCK,
                SidedEnergyStorage::new,
                direction,
                getEnergyStorage()
        );
    }

    /**
     * Get the input/output config for the blockenitity.
     * If directions are not defined in the map, they are assumed to be {@link IOActions#NONE} and do not affect any slot.
     *
     * @return Map of directions that each map to a pair that defines the IOAction as well as the tanks that are affected. Return an empty map if you do not have an itemhandler
     */
    public abstract <T> Map<Direction, Pair<IOActions, int[]>> getSidedInteractions(BlockCapability<T, @Nullable Direction> capability);

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

    @FunctionalInterface
    public interface SidedHandlerSupplier<T> {
        T get(T handler, Pair<IOActions, int[]> supportedActions);
    }
}