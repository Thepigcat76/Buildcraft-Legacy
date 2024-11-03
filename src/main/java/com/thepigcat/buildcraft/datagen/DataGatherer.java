package com.thepigcat.buildcraft.datagen;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.datagen.assets.BCEnUSLangProvider;
import com.thepigcat.buildcraft.datagen.assets.BCBlockStateProvider;
import com.thepigcat.buildcraft.datagen.assets.BCItemModelProvider;
import com.thepigcat.buildcraft.datagen.data.BCBlockLootTableProvider;
import com.thepigcat.buildcraft.datagen.data.BCRecipeProvider;
import com.thepigcat.buildcraft.datagen.data.BCTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = BuildcraftLegacy.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGatherer {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeClient(), new BCItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new BCBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new BCEnUSLangProvider(packOutput));

        BCTagProvider.createTagProviders(generator, packOutput, lookupProvider, existingFileHelper, event.includeServer());
        generator.addProvider(event.includeServer(), new BCRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(), List.of(
                new LootTableProvider.SubProviderEntry(BCBlockLootTableProvider::new, LootContextParamSets.BLOCK)
        ), lookupProvider));
    }
}
