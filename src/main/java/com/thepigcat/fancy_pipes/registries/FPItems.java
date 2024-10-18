package com.thepigcat.fancy_pipes.registries;

import com.thepigcat.fancy_pipes.FancyPipes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class FPItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FancyPipes.MODID);

    public static final DeferredItem<Item> WRENCH = ITEMS.registerItem("wrench", Item::new);
}
