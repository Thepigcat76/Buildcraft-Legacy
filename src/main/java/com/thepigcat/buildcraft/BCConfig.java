package com.thepigcat.buildcraft;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = BuildcraftLegacy.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class BCConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue TANK_CAPACITY = BUILDER
            .comment("The maximum amount of fluid a tank can store")
            .defineInRange("tank_capacity", 8_000, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue COMBUSTION_ENGINE_FLUID_CAPACITY = BUILDER
            .comment("The maximum amount of fluid the combustion engine can store")
            .defineInRange("combustion_engine_tank_capacity", 2_000, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.BooleanValue TANK_RETAIN_FLUIDS = BUILDER
            .comment("Whether the tank should keep its fluid after being broken")
            .define("tank_retain_fluid", true);

    private static final ModConfigSpec.BooleanValue CRATE_RETAIN_ITEMS = BUILDER
            .comment("Whether the crate should keep its items after being broken instead of them dropping on the ground")
            .define("crate_retain_items", true);

    private static final ModConfigSpec.IntValue REDSTONE_ENGINE_ENERGY_CAPACITY = engineEnergyCapacity("redstone", 1000);
    private static final ModConfigSpec.IntValue STIRLING_ENGINE_ENERGY_CAPACITY = engineEnergyCapacity("stirling", 5000);
    private static final ModConfigSpec.IntValue COMBUSTION_ENGINE_ENERGY_CAPACITY = engineEnergyCapacity("combustion", 10_000);

    private static final ModConfigSpec.IntValue REDSTONE_ENGINE_ENERGY_PRODUCTION = engineEnergyProduction("redstone", 2);
    private static final ModConfigSpec.IntValue STIRLING_ENGINE_ENERGY_PRODUCTION = engineEnergyProduction("stirling", 20);
    private static final ModConfigSpec.IntValue COMBUSTION_ENGINE_ENERGY_PRODUCTION = engineEnergyProduction("combustion", 40);

    private static final ModConfigSpec.IntValue CRATE_ITEMS = BUILDER
            .comment("The amount of items a crate can store")
            .defineInRange("crate_items", 4096, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int tankCapacity;
    public static int combustionEngineFluidCapacity;
    public static boolean tankRetainFluids;
    public static boolean crateRetainItems;

    public static int redstoneEngineEnergyCapacity;
    public static int stirlingEngineEnergyCapacity;
    public static int combustionEngineEnergyCapacity;

    public static int redstoneEngineEnergyProduction;
    public static int stirlingEngineEnergyProduction;
    public static int combustionEngineEnergyProduction;

    public static int crateItems;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        tankCapacity = TANK_CAPACITY.get();
        combustionEngineFluidCapacity = COMBUSTION_ENGINE_FLUID_CAPACITY.getAsInt();
        tankRetainFluids = TANK_RETAIN_FLUIDS.get();
        crateRetainItems = CRATE_RETAIN_ITEMS.get();

        redstoneEngineEnergyCapacity = REDSTONE_ENGINE_ENERGY_CAPACITY.getAsInt();
        stirlingEngineEnergyCapacity = STIRLING_ENGINE_ENERGY_CAPACITY.getAsInt();
        combustionEngineEnergyCapacity = COMBUSTION_ENGINE_ENERGY_CAPACITY.getAsInt();

        crateItems = CRATE_ITEMS.getAsInt();

        redstoneEngineEnergyProduction = REDSTONE_ENGINE_ENERGY_PRODUCTION.getAsInt();
        stirlingEngineEnergyProduction = STIRLING_ENGINE_ENERGY_PRODUCTION.getAsInt();
        combustionEngineEnergyProduction = COMBUSTION_ENGINE_ENERGY_PRODUCTION.getAsInt();
    }

    private static ModConfigSpec.IntValue engineEnergyProduction(String id, int defaultValue) {
        return BUILDER
                .comment("Energy production of the %s engine".formatted(id))
                .defineInRange("%s_engine_energy_production".formatted(id), defaultValue, 0, Integer.MAX_VALUE);
    }

    private static ModConfigSpec.IntValue engineEnergyCapacity(String id, int defaultValue) {
        return BUILDER
                .comment("Energy capacity of the %s engine".formatted(id))
                .defineInRange("%s_engine_energy_capacity".formatted(id), defaultValue, 0, Integer.MAX_VALUE);
    }
}
