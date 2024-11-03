package com.thepigcat.buildcraft.datagen.data;

import com.thepigcat.buildcraft.registries.BCBlocks;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

public class BCBlockLootTableProvider extends BlockLootSubProvider {
    private final Set<Block> knownBlocks = new ReferenceOpenHashSet<>();

    public BCBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Collections.emptySet(), FeatureFlags.VANILLA_SET, registries);
    }

    @Override
    protected void generate() {
        dropSelf(BCBlocks.WOODEN_ITEM_PIPE.get());
        dropSelf(BCBlocks.COBBLESTONE_ITEM_PIPE.get());

        dropSelf(BCBlocks.TANK.get());
        dropSelf(BCBlocks.CRATE.get());

        dropSelf(BCBlocks.REDSTONE_ENGINE.get());
        dropSelf(BCBlocks.STIRLING_ENGINE.get());
        dropSelf(BCBlocks.COMBUSTION_ENGINE.get());
    }

    @Override
    public @NotNull Set<Block> getKnownBlocks() {
        return knownBlocks;
    }

    @Override
    protected void add(@NotNull Block block, @NotNull LootTable.Builder table) {
        super.add(block, table);
        knownBlocks.add(block);
    }
}
