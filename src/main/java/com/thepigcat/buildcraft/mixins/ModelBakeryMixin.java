package com.thepigcat.buildcraft.mixins;

import com.mojang.datafixers.util.Either;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.PipesRegistry;
import com.thepigcat.buildcraft.api.pipes.Pipe;
import com.thepigcat.buildcraft.content.blocks.ItemPipeBlock;
import com.thepigcat.buildcraft.util.ModelUtils;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Mutable
    @Shadow
    @Final
    private Map<ResourceLocation, BlockModel> modelResources;

    @Shadow
    @Final
    public static FileToIdConverter MODEL_LISTER;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;push(Ljava/lang/String;)V"))
    private void buildcraft$init(BlockColors blockColors, ProfilerFiller profilerFiller, Map<ResourceLocation, BlockModel> modelResources, Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources, CallbackInfo ci) {
        this.modelResources = new HashMap<>(this.modelResources);
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof ItemPipeBlock) {
                ResourceLocation blockId = block.builtInRegistryHolder().key().location();
                ResourceLocation location = blockId.withPath("block/" + blockId.getPath());
                ResourceLocation baseLocation = location.withSuffix("_base");
                ResourceLocation baseLocationFile = MODEL_LISTER.idToFile(baseLocation);
                BlockModel blockModel = BlockModel.fromString(ModelUtils.DEFAULT_BLOCK_MODEL_FILE.apply("buildcraft:block/pipe_base", baseLocation));
                this.modelResources.put(baseLocationFile, blockModel);
                ResourceLocation connectionLocation = location.withSuffix("_connection");
                ResourceLocation connectionLocationFile = MODEL_LISTER.idToFile(connectionLocation);
                this.modelResources.put(connectionLocationFile, BlockModel.fromString(ModelUtils.DEFAULT_BLOCK_MODEL_FILE.apply("buildcraft:block/pipe_connection", connectionLocation)));
            }
        }
        for (Map.Entry<String, Pipe> entry : PipesRegistry.PIPES.entrySet()) {
            Block block = BuiltInRegistries.BLOCK.get(BuildcraftLegacy.rl(entry.getKey()));
            if (block instanceof ItemPipeBlock) {
                ResourceLocation itemId = block.builtInRegistryHolder().key().location();
                ResourceLocation location = itemId.withPath("item/" + itemId.getPath());
                ResourceLocation locationFile = MODEL_LISTER.idToFile(location);
                BlockModel itemModel = BlockModel.fromString(ModelUtils.DEFAULT_ITEM_MODEL_FILE.apply("buildcraft:item/pipe_inventory", itemId.getPath()));
                this.modelResources.put(locationFile, itemModel);
            }
        }

//        for (Block block : BuiltInRegistries.BLOCK) {
//            if (block instanceof ItemPipeBlock) {
//                ResourceLocation location1 = block.builtInRegistryHolder().key().location();
//                ResourceLocation location = ResourceLocation.fromNamespaceAndPath(location1.getNamespace(), "block/" + location1.getPath());
//                ResourceLocation baseLocation = location.withSuffix("_base");
//                ResourceLocation baseLocationFile = MODEL_LISTER.idToFile(baseLocation);
//                ResourceLocation connectionLocation = location.withSuffix("_connection");
//                ResourceLocation connectionLocationFile = MODEL_LISTER.idToFile(connectionLocation);
//                this.registerModelAndLoadDependencies(new ModelResourceLocation(baseLocationFile, ""), this.getModel(baseLocationFile));
//                this.registerModelAndLoadDependencies(new ModelResourceLocation(connectionLocationFile, ""), this.getModel(connectionLocationFile));
//            }
//        }
    }

    // TODO: We dont need to transform the texture map, we can just use the correct texture in the constructor
    @Inject(method = "loadBlockModel", at = @At("HEAD"), cancellable = true)
    private void buildcraft$loadBlockModel(ResourceLocation location, CallbackInfoReturnable<BlockModel> cir) {
        if (location.getNamespace().equals(BuildcraftLegacy.MODID)) {
            ResourceLocation blockId = null;
            if (location.toString().endsWith("_connection")) {
                String path = location.getPath();
                blockId = location.withPath(path.substring("block/".length(), path.length() - "_connection".length()));
            } else if (location.toString().endsWith("_base")) {
                String path = location.getPath();
                blockId = location.withPath(path.substring("block/".length(), path.length() - "_base".length()));
            }
            if (blockId != null) {
                if (BuiltInRegistries.BLOCK.get(blockId) instanceof ItemPipeBlock) {
                    ResourceLocation key = MODEL_LISTER.idToFile(location);
                    BlockModel blockModel = modelResources.get(key);
                    blockModel.name = location.toString();
                    BuildcraftLegacy.LOGGER.debug("PIPES: {}", PipesRegistry.PIPES.keySet());
                    Material value = new Material(InventoryMenu.BLOCK_ATLAS, PipesRegistry.PIPES.get(blockId.getPath()).texture());
                    blockModel.textureMap.replace("texture", Either.left(value));
                    cir.setReturnValue(blockModel);
                }
            }
        }
    }


}
