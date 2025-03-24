package com.thepigcat.buildcraft.mixins;

import com.google.gson.JsonParser;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.PipesRegistry;
import com.thepigcat.buildcraft.api.pipes.Pipe;
import com.thepigcat.buildcraft.content.blocks.ItemPipeBlock;
import com.thepigcat.buildcraft.registries.BCBlocks;
import com.thepigcat.buildcraft.util.ModelUtils;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(BlockStateModelLoader.class)
public abstract class BlockStateModelLoaderMixin {
    @Mutable
    @Shadow
    @Final
    private Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources;

    @Shadow
    @Final
    public static FileToIdConverter BLOCKSTATE_LISTER;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void buildcraft$init(Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources, ProfilerFiller profiler, UnbakedModel missingModel, BlockColors blockColors, BiConsumer<ModelResourceLocation, UnbakedModel> discoveredModelOutput, CallbackInfo ci) {
        this.blockStateResources = new HashMap<>(blockStateResources);
        for (Map.Entry<String, Pipe> entry : PipesRegistry.PIPES.entrySet()) {
            Block block = BuiltInRegistries.BLOCK.get(BuildcraftLegacy.rl(entry.getKey()));
            ResourceLocation blockId = block.builtInRegistryHolder().key().location();
            Pipe pipe = entry.getValue();
            String modelDef = pipe.getType().blockModelDefinition().apply(pipe, blockId);
            List<BlockStateModelLoader.LoadedJson> value = List.of(new BlockStateModelLoader.LoadedJson("mod/" + BuildcraftLegacy.MODID, JsonParser.parseString(modelDef)));
            this.blockStateResources.put(
                    BLOCKSTATE_LISTER.idToFile(blockId),
                    value
            );
        }
        this.blockStateResources = Collections.unmodifiableMap(this.blockStateResources);
    }

}
