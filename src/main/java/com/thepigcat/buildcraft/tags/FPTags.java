package com.thepigcat.buildcraft.tags;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public final class FPTags {
    public static final class Items {
        public static final TagKey<Item> GEARS = cTag("gears");
        public static final TagKey<Item> WOODEN_GEAR = cTag("gears/wooden");
        public static final TagKey<Item> STONE_GEAR = cTag("gears/stone");
        public static final TagKey<Item> IRON_GEAR = cTag("gears/iron");
        public static final TagKey<Item> GOLD_GEAR = cTag("gears/gold");
        public static final TagKey<Item> DIAMOND_GEAR = cTag("gears/diamond");

        private static TagKey<Item> modTag(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, name));
        }

        private static TagKey<Item> cTag(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }

    public static final class Fluids {
        public static final TagKey<Fluid> OIL = cTag("oil");

        private static TagKey<Fluid> modTag(String name) {
            return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, name));
        }

        private static TagKey<Fluid> cTag(String name) {
            return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath("c", name));
        }
    }
}
