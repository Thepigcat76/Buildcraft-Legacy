package com.thepigcat.buildcraft.content.items.blocks;

import com.thepigcat.buildcraft.BCConfig;
import com.thepigcat.buildcraft.api.capabilties.JumboItemHandler;
import com.thepigcat.buildcraft.content.blockentities.CrateBE;
import com.thepigcat.buildcraft.data.BCDataComponents;
import com.thepigcat.buildcraft.data.components.BigStackContainerContents;
import com.thepigcat.buildcraft.registries.BCBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrateBlockItem extends BlockItem {
    public CrateBlockItem(Properties properties) {
        super(BCBlocks.CRATE.get(), properties);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CrateBE crateBE) {
            crateBE.getItemHandler(null).insertItem(0, stack.get(BCDataComponents.CRATE_CONTENT).copyOne().getSlotStack(), false);
        }
        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if (BCConfig.crateRetainItems) {
            BigStackContainerContents contents = stack.getOrDefault(BCDataComponents.CRATE_CONTENT, BigStackContainerContents.EMPTY);
            if (!contents.getItems().isEmpty()) {
                JumboItemHandler.BigStack stack1 = contents.getStackInSlot(0);
                tooltipComponents.add(Component.literal("%dx ".formatted(stack1.getAmount())).append(stack1.getSlotStack().getHoverName()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
