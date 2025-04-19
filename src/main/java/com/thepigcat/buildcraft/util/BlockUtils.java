package com.thepigcat.buildcraft.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public final class BlockUtils {
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> @Nullable T getBE(Class<T> blockEntity, BlockGetter level, BlockPos pos) {
        BlockEntity blockEntity1 = level.getBlockEntity(pos);
        return blockEntity.isInstance(blockEntity1) ? (T) blockEntity1 : null;
    }

    // TODO: move this to pdl
    public static int calcRedstoneFromTank(IFluidHandler fluidHandler) {
        if (fluidHandler == null) {
            return 0;
        } else {
            int fluidsFound = 0;
            float proportion = 0.0F;

            for(int j = 0; j < fluidHandler.getTanks(); ++j) {
                FluidStack fluidStack = fluidHandler.getFluidInTank(j);
                if (!fluidStack.isEmpty()) {
                    proportion += (float)fluidStack.getAmount() / (float)fluidHandler.getTankCapacity(j);
                    ++fluidsFound;
                }
            }

            proportion /= (float)fluidHandler.getTanks();
            return Mth.floor(proportion * 14.0F) + (fluidsFound > 0 ? 1 : 0);
        }
    }
}
