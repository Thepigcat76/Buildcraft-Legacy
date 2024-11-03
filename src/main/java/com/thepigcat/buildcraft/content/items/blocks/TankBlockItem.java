package com.thepigcat.buildcraft.content.items.blocks;

import com.thepigcat.buildcraft.content.blockentities.TankBE;
import com.thepigcat.buildcraft.data.BCDataComponents;
import com.thepigcat.buildcraft.registries.BCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.jetbrains.annotations.Nullable;

public class TankBlockItem extends BlockItem {
    public TankBlockItem(Properties properties) {
        super(BCBlocks.TANK.get(), properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TankBE tankBE) {
            tankBE.getFluidTank().setFluid(stack.getOrDefault(BCDataComponents.TANK_CONTENT, SimpleFluidContent.EMPTY).copy());
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }
}
