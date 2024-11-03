package com.thepigcat.buildcraft.registries;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class BCItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BuildcraftLegacy.MODID);
    public static final List<DeferredItem<BlockItem>> BLOCK_ITEMS = new ArrayList<>();
    public static final List<DeferredItem<?>> TAB_ITEMS = new ArrayList<>();

    public static final DeferredItem<Item> WRENCH = registerItem("wrench", Item::new);
    public static final DeferredItem<Item> WOODEN_GEAR = registerItem("wooden_gear", Item::new);
    public static final DeferredItem<Item> STONE_GEAR = registerItem("stone_gear", Item::new);
    public static final DeferredItem<Item> IRON_GEAR = registerItem("iron_gear", Item::new);
    public static final DeferredItem<Item> GOLD_GEAR = registerItem("gold_gear", Item::new);
    public static final DeferredItem<Item> DIAMOND_GEAR = registerItem("diamond_gear", Item::new);
    public static final DeferredItem<BucketItem> OIL_BUCKET = registerItem("oil_bucket",
            () -> new BucketItem(BCFluids.OIL_SOURCE.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    private static <T extends Item> DeferredItem<T> registerItem(String name, Function<Item.Properties, T> itemConstructor) {
        DeferredItem<T> item = ITEMS.registerItem(name, itemConstructor, new Item.Properties());
        TAB_ITEMS.add(item);
        return item;
    }

    private static <T extends Item> DeferredItem<T> registerItem(String name, Function<Item.Properties, T> itemConstructor, Item.Properties properties) {
        DeferredItem<T> item = ITEMS.registerItem(name, itemConstructor, properties);
        TAB_ITEMS.add(item);
        return item;
    }

    private static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> itemConstructor) {
        DeferredItem<T> item = ITEMS.register(name, itemConstructor);
        TAB_ITEMS.add(item);
        return item;
    }
}
