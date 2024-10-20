package com.thepigcat.fancy_pipes.api.capabilties;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepigcat.fancy_pipes.FancyPipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
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

    public static String BIG_ITEMS = "big_items";
    public static String STACK = "stack";
    public static String AMOUNT = "amount";

    private List<BigStack> items;
    private int slotLimit;

    public JumboItemHandler(int slotLimit) {
        this(1, slotLimit);
    }

    public JumboItemHandler(int slots, int slotLimit) {
        this.items = new ArrayList<>(slots);
        this.slotLimit = slotLimit;
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

    private void onChanged() {
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
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        Optional<Tag> tag = JumboItemHandler.CODEC.encodeStart(NbtOps.INSTANCE, this)
                .resultOrPartial(msg -> FancyPipes.LOGGER.error("Error encoding jumbo item handler, {}", msg));

        tag.ifPresent(value -> compoundTag.put(BIG_ITEMHANDLER, value));
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        Optional<Pair<JumboItemHandler, Tag>> bigStackTagPair = JumboItemHandler.CODEC.decode(NbtOps.INSTANCE, nbt)
                .resultOrPartial(msg -> FancyPipes.LOGGER.error("Error decoding jumbo item handler, {}", msg));

        if (bigStackTagPair.isPresent()) {
            JumboItemHandler itemHandler = bigStackTagPair.get().getFirst();
            this.items = itemHandler.items;
            this.slotLimit = itemHandler.slotLimit;
        }
    }

    public static class BigStack {
        public static final Codec<BigStack> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        ItemStack.CODEC.fieldOf("stack").forGetter(BigStack::getStack),
                        Codec.INT.fieldOf("amount").forGetter(BigStack::getAmount)
                ).apply(builder, BigStack::new)
        );

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
