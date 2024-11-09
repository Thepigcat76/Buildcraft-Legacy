/**
 * Mostly from functional storage's BigItemHandler class
 * Credits to buzz and all contributors <3
 */

package com.thepigcat.buildcraft.api.capabilties;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class JumboItemHandler implements IItemHandler, INBTSerializable<Tag> {
    public static final Codec<JumboItemHandler> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BigStack.CODEC.listOf().fieldOf("items").forGetter(JumboItemHandler::getItems),
            Codec.INT.fieldOf("slotLimit").forGetter(JumboItemHandler::getSlotLimit)
    ).apply(builder, JumboItemHandler::new));

    private List<BigStack> items;
    private int slotLimit;

    public JumboItemHandler(int slotLimit) {
        this(1, slotLimit);
    }

    public JumboItemHandler(int slots, int slotLimit) {
        this.slotLimit = slotLimit;
        this.items = new ArrayList<>();
        for (int i = 0; i < slots; i++) {
            this.items.add(i, new BigStack(ItemStack.EMPTY, 0));
        }
    }

    public JumboItemHandler(List<BigStack> items, int slotLimit) {
        this.items = items;
        this.slotLimit = slotLimit;
    }

    @Override
    public int getSlots() {
        return this.items.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.items.get(slot).slotStack;
    }

    public List<BigStack> getItems() {
        return items;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (isItemValid(slot, stack)) {
            BigStack bigStack = this.items.get(slot);
            int inserted = Math.min(getSlotLimit(slot) - bigStack.getAmount(), stack.getCount());
            if (!simulate) {
                if (bigStack.getStack().isEmpty())
                    bigStack.setStack(stack.copyWithCount(stack.getMaxStackSize()));
                bigStack.setAmount(Math.min(bigStack.getAmount() + inserted, getSlotLimit(slot)));
                onChanged();
            }
            if (inserted == stack.getCount()) return ItemStack.EMPTY;
            return stack.copyWithCount(stack.getCount() - inserted);
        }
        return stack;
    }

    public void onChanged() {
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) return ItemStack.EMPTY;

        BigStack bigStack = this.items.get(slot);
        if (bigStack.getStack().isEmpty()) return ItemStack.EMPTY;
        if (bigStack.getAmount() <= amount) {
            ItemStack out = bigStack.getStack().copy();
            int newAmount = bigStack.getAmount();
            if (!simulate) {
                bigStack.setAmount(0);
                bigStack.clear();
                onChanged();
            }
            out.setCount(newAmount);
            return out;
        } else {
            if (!simulate) {
                bigStack.setAmount(bigStack.getAmount() - amount);
                onChanged();
            }
            return bigStack.getStack().copyWithCount(amount);
        }
    }

    public int getSlotLimit() {
        return slotLimit;
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.slotLimit;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= items.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + items.size() + ")");
    }

    @Override
    public Tag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
        DataResult<Tag> tagDataResult = CODEC.encodeStart(NbtOps.INSTANCE, this);
        if (tagDataResult.isSuccess()) {
            return tagDataResult.getOrThrow();
        }
        BuildcraftLegacy.LOGGER.debug("Error encoding jumbo item handler: {}", tagDataResult.error().get().message());
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, Tag nbt) {
        DataResult<Pair<JumboItemHandler, Tag>> pairDataResult = CODEC.decode(NbtOps.INSTANCE, nbt);
        if (pairDataResult.isSuccess()) {
            JumboItemHandler jumboItemHandler = pairDataResult.getOrThrow().getFirst();
            this.items = jumboItemHandler.items;
            this.slotLimit = jumboItemHandler.slotLimit;
        }
    }

    public static class BigStack {
        public static final Codec<BigStack> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        ItemStack.OPTIONAL_CODEC.fieldOf("stack").forGetter(BigStack::getEncodeStack),
                        Codec.INT.fieldOf("amount").forGetter(BigStack::getAmount)
                ).apply(builder, BigStack::new)
        );
        public static final BigStack EMPTY = new BigStack(ItemStack.EMPTY, 0);

        private ItemStack stack;
        private ItemStack slotStack;
        private int amount;

        public BigStack(ItemStack stack, int amount) {
            this.stack = stack.copy();
            this.amount = amount;
            this.slotStack = stack.copyWithCount(amount);
        }

        public ItemStack getStack() {
            return stack;
        }

        public ItemStack getEncodeStack() {
            return stack.copyWithCount(1);
        }

        public void setStack(ItemStack stack) {
            this.stack = stack.copy();
            this.slotStack = stack.copyWithCount(amount);
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
            this.slotStack.setCount(amount);
        }

        public void clear() {
            this.amount = 0;
            this.slotStack = ItemStack.EMPTY;
            this.stack = ItemStack.EMPTY;
        }
    }
}
