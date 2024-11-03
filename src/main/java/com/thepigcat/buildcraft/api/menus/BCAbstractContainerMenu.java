package com.thepigcat.buildcraft.api.menus;

import com.google.common.collect.ImmutableList;
import com.thepigcat.buildcraft.api.blockentities.ContainerBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class BCAbstractContainerMenu<T extends ContainerBlockEntity> extends AbstractContainerMenu {
    public final @NotNull T blockEntity;
    protected final @NotNull Inventory inv;
    private final ContainerLevelAccess access;
    private final ImmutableList<Block> validBlocks;

    public @NotNull T getBlockEntity() {
        return blockEntity;
    }

    public BCAbstractContainerMenu(MenuType<?> menuType, int containerId, @NotNull Inventory inv, @NotNull T blockEntity) {
        super(menuType, containerId);
        this.blockEntity = blockEntity;
        this.inv = inv;
        this.access = ContainerLevelAccess.create(inv.player.level(), blockEntity.getBlockPos());
        this.validBlocks = ImmutableList.copyOf(blockEntity.getType().getValidBlocks());
    }

    protected void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 83 + i * 18));
            }
        }
    }

    protected void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 141));
        }
    }

    protected void addPlayerInventory(Inventory playerInventory, int y) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, y + i * 18));
            }
        }
    }

    protected void addPlayerHotbar(Inventory playerInventory, int y) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, y));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        for (Block block : validBlocks) {
            boolean valid = stillValid(access, player, block);
            if (valid) {
                return true;
            }
        }
        return false;
    }

    // Item quick move code from cofh. Thanks to KingLemming and CofhTeam :3
    protected boolean supportsShiftClick(Player player, int index) {
        return true;
    }

    protected abstract int getMergeableSlotCount();

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (!supportsShiftClick(player, index)) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();

            if (!performMerge(index, stackInSlot)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stackInSlot, stack);

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stackInSlot.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stackInSlot);
        }
        return stack;
    }

    protected boolean performMerge(int index, ItemStack stack) {
        // TODO: Consider reverting or allowing augment shift-click in some cases.
        int invBase = getMergeableSlotCount();
        // int invBase = getSizeInventory() - getNumAugmentSlots();
        int invFull = slots.size();
        int invHotbar = invFull - 9;
        int invPlayer = invHotbar - 27;

        if (index < invPlayer) {
            return moveItemStackTo(stack, invPlayer, invFull, false);
        } else {
            return moveItemStackTo(stack, 0, invBase, false);
        }
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        return mergeItemStack(slots, stack, startIndex, endIndex, reverseDirection);
    }

    public static boolean mergeItemStack(List<Slot> slots, ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {

        boolean successful = false;
        int i = reverseDirection ? endIndex - 1 : startIndex;
        int iterOrder = reverseDirection ? -1 : 1;

        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }
                Slot slot = slots.get(i);
                if (!slot.mayPlace(stack)) {
                    i += iterOrder;
                    continue;
                }
                ItemStack stackInSlot = slot.getItem();
                if (!stackInSlot.isEmpty() && ItemStack.isSameItemSameComponents(stackInSlot, stack)) {
                    int size = stackInSlot.getCount() + stack.getCount();
                    int maxSize = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
                    if (size <= maxSize) {
                        stack.setCount(0);
                        stackInSlot.setCount(size);
                        slot.set(stackInSlot);
                        successful = true;
                    } else if (stackInSlot.getCount() < maxSize) {
                        stack.shrink(maxSize - stackInSlot.getCount());
                        stackInSlot.setCount(maxSize);
                        slot.set(stackInSlot);
                        successful = true;
                    }
                }
                i += iterOrder;
            }
        }
        if (!stack.isEmpty()) {
            i = reverseDirection ? endIndex - 1 : startIndex;
            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }
                Slot slot = slots.get(i);
                ItemStack stackInSlot = slot.getItem();
                if (stackInSlot.isEmpty() && slot.mayPlace(stack)) {
                    int maxSize = Math.min(stack.getMaxStackSize(), slot.getMaxStackSize());
                    int splitSize = Math.min(maxSize, stack.getCount());
                    slot.set(stack.split(splitSize));
                    successful = true;
                }
                i += iterOrder;
            }
        }
        return successful;
    }

    public @NotNull Inventory getInv() {
        return inv;
    }
}