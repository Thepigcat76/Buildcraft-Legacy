package com.thepigcat.buildcraft.datagen;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.datagen.assets.EnUSLangProvider;
import com.thepigcat.buildcraft.datagen.assets.FPBlockStateProvider;
import com.thepigcat.buildcraft.datagen.assets.FPItemModelProvider;
import com.thepigcat.buildcraft.datagen.data.BlockLootTableProvider;
import com.thepigcat.buildcraft.datagen.data.RecipeProvider;
import com.thepigcat.buildcraft.datagen.data.TagProvider;
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

        generator.addProvider(event.includeClient(), new FPItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new FPBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new EnUSLangProvider(packOutput));

        TagProvider.createTagProviders(generator, packOutput, lookupProvider, existingFileHelper, event.includeServer());
        generator.addProvider(event.includeServer(), new RecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLootTableProvider::new, LootContextParamSets.BLOCK)
        ), lookupProvider));
    }
}
