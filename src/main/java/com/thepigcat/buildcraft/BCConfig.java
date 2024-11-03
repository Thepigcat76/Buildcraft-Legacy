package com.thepigcat.buildcraft;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = BuildcraftLegacy.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class BCConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue TANK_CAPACITY = BUILDER
            .comment("The maximum amount of fluid a tank can store")
            .defineInRange("tankCapacity", 8_000, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.BooleanValue TANK_RETAIN_FLUIDS = BUILDER
            .comment("Whether the tank should keep its fluid after being broken")
            .define("tankRetainFluid", true);

    private static final ModConfigSpec.BooleanValue CRATE_RETAIN_ITEMS = BUILDER
            .comment("Whether the crate should keep its items after being broken instead of them dropping on the ground")
            .define("crateRetainItems", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int tankCapacity;
    public static boolean tankRetainFluids;
    public static boolean crateRetainItems;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        tankCapacity = TANK_CAPACITY.get();
        tankRetainFluids = TANK_RETAIN_FLUIDS.get();
        crateRetainItems = CRATE_RETAIN_ITEMS.get();
    }
}
