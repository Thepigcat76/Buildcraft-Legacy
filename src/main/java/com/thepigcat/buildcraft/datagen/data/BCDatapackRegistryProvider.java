package com.thepigcat.buildcraft.datagen.data;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BCDatapackRegistryProvider extends DatapackBuiltinEntriesProvider {
    public BCDatapackRegistryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BCDatapackRegistryProvider.BUILDER, Set.of(BuildcraftLegacy.MODID));
    }

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.STRUCTURE, BCDatapackRegistryProvider::bootstrapStructures)
            .add(Registries.STRUCTURE_SET, BCDatapackRegistryProvider::bootstrapStructureSets);

    private static void bootstrapStructures(BootstrapContext<Structure> context) {
    }

    private static void bootstrapStructureSets(BootstrapContext<StructureSet> context) {
    }
}
