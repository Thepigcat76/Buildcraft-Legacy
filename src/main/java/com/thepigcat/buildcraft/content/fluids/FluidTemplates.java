package com.thepigcat.buildcraft.content.fluids;

import com.portingdeadmods.portingdeadlibs.api.fluids.FluidTemplate;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import net.minecraft.resources.ResourceLocation;

public enum FluidTemplates implements FluidTemplate {
    OIL(BuildcraftLegacy.rl("fluid/oil_fluid_still"),
            BuildcraftLegacy.rl("fluid/oil_fluid_flow"),
            BuildcraftLegacy.rl("fluid/oil_overlay")),
    WATER(ResourceLocation.parse("block/water_still"),
            ResourceLocation.parse("block/water_flow"),
            BuildcraftLegacy.rl("misc/in_soap_water"));

    private final ResourceLocation still;
    private final ResourceLocation flowing;
    private final ResourceLocation overlay;

    FluidTemplates(ResourceLocation still, ResourceLocation flowing, ResourceLocation overlay) {
        this.still = still;
        this.flowing = flowing;
        this.overlay = overlay;
    }

    @Override
    public ResourceLocation getStillTexture() {
        return still;
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return flowing;
    }

    @Override
    public ResourceLocation getOverlayTexture() {
        return overlay;
    }
}