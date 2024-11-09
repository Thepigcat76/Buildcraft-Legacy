package com.thepigcat.buildcraft.content.blockentities;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.networking.SyncPipeDirectionPayload;
import com.thepigcat.buildcraft.registries.BCBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExtractItemPipeBE extends ItemPipeBE {
    public ExtractItemPipeBE(BlockPos pos, BlockState blockState) {
        super(BCBlockEntities.EXTRACTING_ITEM_PIPE.get(), pos, blockState);
    }

    @Override
    public void tick() {
        // If pipe can extract, extract from itemhandler on extracting side
        if (!level.isClientSide() && level.getGameTime() % 50 == 0) {
            BlockCapabilityCache<IItemHandler, Direction> cache = capabilityCaches.get(this.extracting);
            if (cache != null) {
                IItemHandler extractingHandler = cache.getCapability();

                if (extractingHandler != null) {
                    ItemStack extractedStack = ItemStack.EMPTY;
                    int extractedSlot = 0;

                    for (int i = 0; i < extractingHandler.getSlots(); i++) {
                        ItemStack stack = extractingHandler.extractItem(i, 64, false);
                        if (!stack.isEmpty()) {
                            extractedStack = stack;
                            extractedSlot = i;
                            break;
                        }
                    }

                    if (!extractedStack.isEmpty()) {
                        ItemStack insertRemainder = itemHandler.insertItem(0, extractedStack, false);
                        extractingHandler.insertItem(extractedSlot, insertRemainder, false);

                        this.from = this.extracting;

                        List<Direction> directions = new ArrayList<>(this.directions);
                        directions.remove(this.extracting);

                        if (!directions.isEmpty()) {
                            this.to = directions.getFirst();
                        }

                        PacketDistributor.sendToAllPlayers(new SyncPipeDirectionPayload(worldPosition, Optional.ofNullable(this.from), Optional.ofNullable(this.to)));
                    }

                }
            }
        }

        // Regular extraction logic
        super.tick();
    }
}
