package com.thepigcat.buildcraft.registries;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class FPFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, BuildcraftLegacy.MODID);

    // TODO: Make burnable
    public static final Supplier<FlowingFluid> OIL_SOURCE = FLUIDS.register("oil",
            () -> new BaseFlowingFluid.Source(FPFluids.OIL_PROPERTIES));
    public static final Supplier<FlowingFluid> OIL_FLOWING = FLUIDS.register("oil_flowing",
            () -> new BaseFlowingFluid.Flowing(FPFluids.OIL_PROPERTIES));

    public static final BaseFlowingFluid.Properties OIL_PROPERTIES = new BaseFlowingFluid.Properties(
            FPFluidTypes.OIL_FLUID_TYPE, OIL_SOURCE, OIL_FLOWING)
            .slopeFindDistance(2).levelDecreasePerBlock(2).block(FPBlocks.OIL_FLUID)
            .bucket(FPItems.OIL_BUCKET);
}
