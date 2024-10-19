package com.thepigcat.fancy_pipes.registries;

import com.thepigcat.fancy_pipes.FancyPipes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class FPItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FancyPipes.MODID);
    public static final List<DeferredItem<BlockItem>> BLOCK_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<?>> TAB_ITEMS = new ArrayList<>();

    public static final DeferredItem<Item> WRENCH = ITEMS.registerItem("wrench", Item::new);
}
