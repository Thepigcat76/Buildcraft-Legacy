package com.thepigcat.buildcraft.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class BlockUtils {
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> @Nullable T getBE(Class<T> blockEntity, BlockGetter level, BlockPos pos) {
        BlockEntity blockEntity1 = level.getBlockEntity(pos);
        return blockEntity.isInstance(blockEntity1) ? (T) blockEntity1 : null;
    }
}
