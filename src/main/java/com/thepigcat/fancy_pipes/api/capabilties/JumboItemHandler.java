/**
 * Mostly from functional storage's BigItemHandler class
 * Credits to buzz and all contributors <3
 */

package com.thepigcat.fancy_pipes.api.capabilties;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepigcat.fancy_pipes.FancyPipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JumboItemHandler implements IItemHandler, INBTSerializable<CompoundTag> {
    public static final Codec<JumboItemHandler> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BigStack.CODEC.listOf().fieldOf("items").forGetter(JumboItemHandler::getItems),
            Codec.INT.fieldOf("slotLimit").forGetter(JumboItemHandler::getSlotLimit)
    ).apply(builder, JumboItemHandler::new));

    public static final String BIG_ITEMHANDLER = "big_itemhandler";
    public static String BIG_ITEMS = "BigItems";
    public static String STACK = "Stack";
    public static String AMOUNT = "Amount";

    private final List<BigStack> items;
    private final int slotLimit;

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
    public CompoundTag serializeNBT(net.minecraft.core.HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        CompoundTag items = new CompoundTag();
        for (int i = 0; i < this.items.size(); i++) {
            CompoundTag bigStack = new CompoundTag();
            bigStack.put(STACK, this.items.get(i).getStack().saveOptional(provider));
            bigStack.putInt(AMOUNT, this.items.get(i).getAmount());
            items.put(i + "", bigStack);
        }
        compoundTag.put(BIG_ITEMS, items);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(net.minecraft.core.HolderLookup.Provider provider, CompoundTag nbt) {
        for (String allKey : nbt.getCompound(BIG_ITEMS).getAllKeys()) {
            this.items.get(Integer.parseInt(allKey)).setStack(deserialize(provider, nbt.getCompound(BIG_ITEMS).getCompound(allKey).getCompound(STACK)));
            this.items.get(Integer.parseInt(allKey)).setAmount(nbt.getCompound(BIG_ITEMS).getCompound(allKey).getInt(AMOUNT));
        }
    }

    public static ItemStack deserialize(HolderLookup.Provider provider, CompoundTag tag) {
        return ItemStack.OPTIONAL_CODEC.decode(RegistryOps.create(NbtOps.INSTANCE, provider), tag).getOrThrow().getFirst();
    }

    public static class BigStack {
        public static final Codec<BigStack> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        ItemStack.CODEC.fieldOf("stack").forGetter(BigStack::getStack),
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
    }
}
