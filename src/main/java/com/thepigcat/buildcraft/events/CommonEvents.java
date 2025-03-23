package com.thepigcat.buildcraft.events;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.capabilties.JumboItemHandler;
import com.thepigcat.buildcraft.content.blockentities.CrateBE;
import com.thepigcat.buildcraft.content.blocks.CrateBlock;
import com.thepigcat.buildcraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

@EventBusSubscriber(modid = BuildcraftLegacy.MODID)
public final class CommonEvents {
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        if (!level.isClientSide()) {
            BlockPos pos = event.getPos();
            BlockState blockState = level.getBlockState(pos);
            if (blockState.getBlock() instanceof CrateBlock && event.getFace().equals(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))) {
                CrateBE be = BlockUtils.getBE(CrateBE.class, level, pos);
                if (be != null) {
                    JumboItemHandler itemHandler = be.getItemHandler();
                    ItemStack stack = itemHandler.getStackInSlot(0);
                    int count = 1;
                    Player player = event.getEntity();
                    if (player.isShiftKeyDown()) {
                        count = stack.getMaxStackSize();
                    }
                    ItemStack extracted = itemHandler.extractItem(0, count, false);
                    ItemHandlerHelper.giveItemToPlayer(player, extracted);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        LevelAccessor level = event.getLevel();
        if (!level.isClientSide()) {
            BlockPos pos = event.getPos();
            BlockState blockState = level.getBlockState(pos);
            if (blockState.getBlock() instanceof CrateBlock && event.getPlayer().getDirection().equals(blockState.getValue(BlockStateProperties.HORIZONTAL_FACING))) {
                CrateBE be = BlockUtils.getBE(CrateBE.class, level, pos);
                if (be != null) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
