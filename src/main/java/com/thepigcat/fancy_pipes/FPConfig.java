package com.thepigcat.fancy_pipes;

import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = FancyPipes.MODID, bus = EventBusSubscriber.Bus.MOD)
public class FPConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue TANK_CAPACITY = BUILDER
            .comment("The maximum amount of fluid a tank can store")
            .defineInRange("tankCapacity", 8_000, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int tankCapacity;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        tankCapacity = TANK_CAPACITY.getAsInt();
    }
}
