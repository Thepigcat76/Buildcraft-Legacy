package com.thepigcat.buildcraft.registries;

import com.portingdeadmods.portingdeadlibs.utils.FluidRegistrationHelper;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.content.fluids.OilFluid;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class BCFluids {
    public static final FluidRegistrationHelper HELPER = new FluidRegistrationHelper(BCBlocks.BLOCKS, BCItems.ITEMS, BuildcraftLegacy.MODID);

    public static final OilFluid OIL = HELPER.registerFluid(new OilFluid("oil"));
}
