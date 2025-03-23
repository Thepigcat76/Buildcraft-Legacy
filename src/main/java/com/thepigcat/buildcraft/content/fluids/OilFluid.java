package com.thepigcat.buildcraft.content.fluids;

import com.portingdeadmods.portingdeadlibs.api.fluids.PDLFluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Vector4i;

public class OilFluid extends PDLFluid {
    public OilFluid(String name) {
        super(name);
        this.fluidType = registerFluidType(FluidType.Properties.create()
                .canSwim(false)
                .canDrown(true)
                .canExtinguish(false)
                .viscosity(2000)
                .density(2000)
                .canConvertToSource(false), new Vector4i(255, 255, 255, 255), FluidTemplates.OIL);
    }

    @Override
    public BaseFlowingFluid.Properties fluidProperties() {
        return super.fluidProperties().block(this.block).bucket(this.deferredBucket).slopeFindDistance(2).levelDecreasePerBlock(2);
    }

    @Override
    public BlockBehaviour.Properties blockProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).lightLevel(blockState -> 0);
    }
}
