package com.thepigcat.buildcraft.registries;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.fluids.BaseFluidType;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundAction;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class BCFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, BuildcraftLegacy.MODID);

    public static final Supplier<FluidType> OIL_FLUID_TYPE = register("oil",
            FluidType.Properties.create().lightLevel(2).density(15).viscosity(5).sound(SoundAction.get("drink"),
                    SoundEvents.HONEY_DRINK), new Vec3i(255, 255, 255), new Vec3i(0, 0, 0), FluidTemplate.OIL);

    private static Supplier<FluidType> register(String name, FluidType.Properties properties, Vec3i color, Vec3i fogColor, FluidTemplate template) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(template.still, template.flowing, template.overlay, color, fogColor, properties));
    }

    private static Supplier<FluidType> register(String name, FluidType.Properties properties, Vec3i color, FluidTemplate template) {
        return FLUID_TYPES.register(name, () -> new BaseFluidType(template.still, template.flowing, template.overlay, color, properties));
    }

    public enum FluidTemplate {
        OIL(ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "fluid/fluid_oil"),
                ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "fluid/fluid_oil"),
                ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "fluid/oil_overlay"));

        private final ResourceLocation still;
        private final ResourceLocation flowing;
        private final ResourceLocation overlay;

        FluidTemplate(ResourceLocation still, ResourceLocation flowing, ResourceLocation overlay) {
            this.still = still;
            this.flowing = flowing;
            this.overlay = overlay;
        }
    }
}
