package com.thepigcat.buildcraft.data;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class BCDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, BuildcraftLegacy.MODID);

    public static final Supplier<DataComponentType<ItemContainerContents>> CRATE_CONTENT = DATA_COMPONENTS.register("crate_content",
            () -> DataComponentType.<ItemContainerContents>builder().persistent(ItemContainerContents.CODEC).networkSynchronized(ItemContainerContents.STREAM_CODEC).build());
    public static final Supplier<DataComponentType<SimpleFluidContent>> TANK_CONTENT = DATA_COMPONENTS.register("tank_content",
            () -> DataComponentType.<SimpleFluidContent>builder().persistent(SimpleFluidContent.CODEC).networkSynchronized(SimpleFluidContent.STREAM_CODEC).build());
}
