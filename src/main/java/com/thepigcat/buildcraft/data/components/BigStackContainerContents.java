package com.thepigcat.buildcraft.data.components;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepigcat.buildcraft.api.capabilties.JumboItemHandler.BigStack;
import com.thepigcat.buildcraft.util.ItemUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Stream;

public class BigStackContainerContents {
    private static final int NO_SLOT = -1;
    private static final int MAX_SIZE = 256;
    public static final BigStackContainerContents EMPTY = new BigStackContainerContents(NonNullList.create(), 0);
    public static final Codec<BigStackContainerContents> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Slot.CODEC.sizeLimitedListOf(MAX_SIZE).fieldOf("items").forGetter(BigStackContainerContents::asSlots),
            Codec.INT.fieldOf("slot_limit").forGetter(BigStackContainerContents::getSlotLimit)
    ).apply(inst, BigStackContainerContents::fromSlots));
    public static final StreamCodec<RegistryFriendlyByteBuf, BigStackContainerContents> STREAM_CODEC = StreamCodec.composite(
            BigStack.STREAM_CODEC.apply(ByteBufCodecs.list(MAX_SIZE)),
            BigStackContainerContents::getItems,
            ByteBufCodecs.INT,
            BigStackContainerContents::getSlotLimit,
            BigStackContainerContents::new
    );
    private final NonNullList<BigStack> items;
    private final int hashCode;
    private final int slotLimit;

    private BigStackContainerContents(NonNullList<BigStack> items, int slotLimit) {
        if (items.size() > MAX_SIZE) {
            throw new IllegalArgumentException("Got " + items.size() + " items, but maximum is " + MAX_SIZE);
        } else {
            this.items = items;
            this.hashCode = ItemUtils.hashStackList(items);
            this.slotLimit = slotLimit;
        }
    }

    private BigStackContainerContents(int size, int slotLimit) {
        this(NonNullList.withSize(size, BigStack.EMPTY), slotLimit);
    }

    private BigStackContainerContents(List<BigStack> items, int slotLimit) {
        this(items.size(), slotLimit);

        for (int i = 0; i < items.size(); ++i) {
            this.items.set(i, items.get(i));
        }

    }

    public static BigStackContainerContents fromItems(List<BigStack> items, int slotLimit) {
        int i = findLastNonEmptySlot(items);
        if (i == -1) {
            return EMPTY;
        } else {
            BigStackContainerContents itemcontainercontents = new BigStackContainerContents(i + 1, slotLimit);

            for (int j = 0; j <= i; ++j) {
                itemcontainercontents.items.set(j, items.get(j).copy());
            }

            return itemcontainercontents;
        }
    }

    private static BigStackContainerContents fromSlots(List<BigStackContainerContents.Slot> slots, int slotLimit) {
        OptionalInt optionalint = slots.stream().mapToInt(BigStackContainerContents.Slot::index).max();
        if (optionalint.isEmpty()) {
            return EMPTY;
        } else {
            BigStackContainerContents itemcontainercontents = new BigStackContainerContents(optionalint.getAsInt() + 1, slotLimit);

            for (BigStackContainerContents.Slot itemcontainercontents$slot : slots) {
                itemcontainercontents.items.set(itemcontainercontents$slot.index(), itemcontainercontents$slot.item());
            }

            return itemcontainercontents;
        }
    }

    private static int findLastNonEmptySlot(List<BigStack> items) {
        for (int i = items.size() - 1; i >= 0; --i) {
            if (!items.get(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    private List<BigStackContainerContents.Slot> asSlots() {
        List<BigStackContainerContents.Slot> list = new ArrayList();

        for (int i = 0; i < this.items.size(); ++i) {
            BigStack itemstack = (BigStack) this.items.get(i);
            if (!itemstack.isEmpty()) {
                list.add(new BigStackContainerContents.Slot(i, itemstack));
            }
        }

        return list;
    }

    public int getSlotLimit() {
        return slotLimit;
    }

    public void copyInto(NonNullList<BigStack> list) {
        for (int i = 0; i < list.size(); ++i) {
            BigStack itemstack = i < this.items.size() ? this.items.get(i) : BigStack.EMPTY;
            list.set(i, itemstack.copy());
        }

    }

    public BigStack copyOne() {
        return this.items.isEmpty() ? BigStack.EMPTY : (this.items.get(0)).copy();
    }

    public Stream<BigStack> stream() {
        return this.items.stream().map(BigStack::copy);
    }

    public Stream<BigStack> nonEmptyStream() {
        return this.items.stream().filter((p_331322_) -> !p_331322_.isEmpty()).map(BigStack::copy);
    }

    public Iterable<BigStack> nonEmptyItems() {
        return Iterables.filter(this.items, (p_331420_) -> !p_331420_.isEmpty());
    }

    public Iterable<BigStack> nonEmptyItemsCopy() {
        return Iterables.transform(this.nonEmptyItems(), BigStack::copy);
    }

    public NonNullList<BigStack> getItems() {
        return items;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            if (other instanceof BigStackContainerContents itemcontainercontents) {
                return ItemUtils.listMatches(this.items, itemcontainercontents.items);
            }

            return false;
        }
    }

    public int hashCode() {
        return this.hashCode;
    }

    public int getSlots() {
        return this.items.size();
    }

    public BigStack getStackInSlot(int slot) {
        return this.items.get(slot).copy();
    }

    record Slot(int index, BigStack item) {
        public static final Codec<BigStackContainerContents.Slot> CODEC = RecordCodecBuilder.create((p_331695_) -> p_331695_.group(Codec.intRange(0, 255).fieldOf("slot").forGetter(BigStackContainerContents.Slot::index), BigStack.CODEC.fieldOf("item").forGetter(BigStackContainerContents.Slot::item)).apply(p_331695_, BigStackContainerContents.Slot::new));
    }
}
