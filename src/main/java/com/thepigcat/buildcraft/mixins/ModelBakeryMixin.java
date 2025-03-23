package com.thepigcat.buildcraft.mixins;

import com.mojang.datafixers.util.Either;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.PipesRegistry;
import com.thepigcat.buildcraft.content.blocks.ItemPipeBlock;
import com.thepigcat.buildcraft.content.blocks.TestBlock;
import com.thepigcat.buildcraft.util.ModelUtils;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.client.resources.model.ModelBakery.MODEL_LISTER;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Mutable
    @Shadow
    @Final
    private Map<ResourceLocation, BlockModel> modelResources;

    @Shadow
    abstract UnbakedModel getModel(ResourceLocation modelLocation);

    @Shadow
    protected abstract void registerModelAndLoadDependencies(ModelResourceLocation modelLocation, UnbakedModel model);

    @Shadow
    @Final
    private Map<ModelResourceLocation, UnbakedModel> topLevelModels;

    @Shadow
    @Final
    public static FileToIdConverter MODEL_LISTER;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void buildcraft$init(BlockColors blockColors, ProfilerFiller profilerFiller, Map<ResourceLocation, BlockModel> modelResources, Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources, CallbackInfo ci) {

        this.modelResources = new HashMap<>(this.modelResources);
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof ItemPipeBlock) {
                ResourceLocation location1 = block.builtInRegistryHolder().key().location();
                ResourceLocation location = ResourceLocation.fromNamespaceAndPath(location1.getNamespace(), "block/" + location1.getPath());
                ResourceLocation baseLocation = location.withSuffix("_base");
                ResourceLocation baseLocationFile = MODEL_LISTER.idToFile(baseLocation);
                BlockModel blockModel = BlockModel.fromString(ModelUtils.BLOCK_MODEL_FILE.apply(baseLocation));
                this.modelResources.put(baseLocationFile, blockModel);
                BuildcraftLegacy.LOGGER.debug("The model: {}", this.modelResources.get(baseLocationFile).textureMap);
                ResourceLocation connectionLocation = location.withSuffix("_connection");
                ResourceLocation connectionLocationFile = MODEL_LISTER.idToFile(connectionLocation);
                this.modelResources.put(connectionLocationFile, BlockModel.fromString(ModelUtils.BLOCK_MODEL_FILE.apply(connectionLocation)));
            }
        }

        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof ItemPipeBlock) {
                ResourceLocation location1 = block.builtInRegistryHolder().key().location();
                ResourceLocation location = ResourceLocation.fromNamespaceAndPath(location1.getNamespace(), "block/" + location1.getPath());
                ResourceLocation baseLocation = location.withSuffix("_base");
                ResourceLocation baseLocationFile = MODEL_LISTER.idToFile(baseLocation);
                ResourceLocation connectionLocation = location.withSuffix("_connection");
                ResourceLocation connectionLocationFile = MODEL_LISTER.idToFile(connectionLocation);
                this.registerModelAndLoadDependencies(new ModelResourceLocation(baseLocationFile, ""), this.getModel(baseLocationFile));
                this.registerModelAndLoadDependencies(new ModelResourceLocation(connectionLocationFile, ""), this.getModel(connectionLocationFile));
            }
        }
    }

    @Inject(method = "loadBlockModel", at = @At("HEAD"), cancellable = true)
    private void buildcraft$loadBlockModel(ResourceLocation location, CallbackInfoReturnable<BlockModel> cir) {
        if (location.getNamespace().equals(BuildcraftLegacy.MODID)) {
            BuildcraftLegacy.LOGGER.debug("LOC: {}", location);
            if (location.toString().endsWith("_connection") || location.toString().endsWith("_base")) {
                BuildcraftLegacy.LOGGER.debug("Deez");
                ResourceLocation key = MODEL_LISTER.idToFile(location);
                BuildcraftLegacy.LOGGER.debug("LOOKUP KEY: {}", key);
                BlockModel blockModel = modelResources.get(key);
                blockModel.name = location.toString();
                Material value = new Material(TextureAtlas.LOCATION_BLOCKS, BuildcraftLegacy.rl("block/cobblestone_pipe"));
                blockModel.textureMap.replace("texture", Either.left(value));
                cir.setReturnValue(blockModel);
            }
        }
    }


}
