package com.thepigcat.fancy_pipes.content.blockentities;

import com.thepigcat.fancy_pipes.networking.SyncPipeDirectionPayload;
import com.thepigcat.fancy_pipes.registries.FPBlockEntities;
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
        super(FPBlockEntities.EXTRACTING_ITEM_PIPE.get(), pos, blockState);
    }

    @Override
    public void tick() {
        // If pipe can extract, extract from itemhandler on extracting side
        if (!level.isClientSide() && level.getGameTime() % 50 == 0) {
            BlockCapabilityCache<IItemHandler, Direction> cache = capabilityCaches.get(this.extracting);
            if (cache != null) {
                IItemHandler extractingHandler = cache.getCapability();
                if (extractingHandler != null) {
                    ItemStack stack = extractingHandler.extractItem(0, 64, false);
                    if (!stack.isEmpty()) {
                        this.itemHandler.insertItem(0, stack, false);

                        this.from = this.extracting;

                        List<Direction> directions = new ArrayList<>(this.directions);
                        directions.remove(this.extracting);

                        if (!directions.isEmpty()) {
                            this.to = directions.getFirst();
                        }

                        PacketDistributor.sendToAllPlayers(new SyncPipeDirectionPayload(this.getBlockPos(), Optional.ofNullable(this.from), Optional.ofNullable(this.to)));
                    }
                }
            }
        }

        // Regular extraction logic
        super.tick();
    }
}
