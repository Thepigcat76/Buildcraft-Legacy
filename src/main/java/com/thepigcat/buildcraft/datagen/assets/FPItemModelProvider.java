package com.thepigcat.buildcraft.datagen.assets;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.blocks.PipeBlock;
import com.thepigcat.buildcraft.registries.FPBlocks;
import com.thepigcat.buildcraft.registries.FPFluids;
import com.thepigcat.buildcraft.registries.FPItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Objects;
import java.util.Set;

public class FPItemModelProvider extends ItemModelProvider {
    private static final Set<Block> DEFAULT_MODEL_BLACKLIST = Set.of(
            FPBlocks.TANK.get(),
            FPBlocks.CRATE.get()
    );

    public FPItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BuildcraftLegacy.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handHeldItem(FPItems.WRENCH.get());
        basicItem(FPItems.WOODEN_GEAR.get());
        basicItem(FPItems.STONE_GEAR.get());
        basicItem(FPItems.IRON_GEAR.get());
        basicItem(FPItems.GOLD_GEAR.get());
        basicItem(FPItems.DIAMOND_GEAR.get());

        bucketItem(FPFluids.OIL_SOURCE.get());

        blockItems();
    }

    private void blockItems() {
        for (DeferredItem<BlockItem> blockItem : FPItems.BLOCK_ITEMS) {
            if (!DEFAULT_MODEL_BLACKLIST.contains(blockItem.get().getBlock())) {
                if (blockItem.get().getBlock() instanceof PipeBlock) {
                    pipeItemModel(blockItem.get());
                } else {
                    parentItemBlock(blockItem.get());
                }
            }
        }
    }

    public void bucketItem(Fluid fluid) {
        withExistingParent(name(fluid.getBucket().asItem()), ResourceLocation.fromNamespaceAndPath("neoforge", "item/bucket_drip"))
                .customLoader(DynamicFluidContainerModelBuilder::begin)
                .fluid(fluid);
    }

    public String name(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    public ItemModelBuilder parentItemBlock(Item item, ResourceLocation loc) {
        ResourceLocation name = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
        return getBuilder(name.toString())
                .parent(new ModelFile.UncheckedModelFile(loc));
    }

    public ItemModelBuilder parentItemBlock(Item item) {
        return parentItemBlock(item, "");
    }

    public ItemModelBuilder parentItemBlock(Item item, String suffix) {
        ResourceLocation name = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
        return getBuilder(name.toString())
                .parent(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(name.getNamespace(), "block/" + name.getPath() + suffix)));
    }

    public void pipeItemModel(Item item) {
        ResourceLocation name = Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(item));
        getBuilder(name.toString())
                .parent(new ModelFile.UncheckedModelFile(modLoc("item/pipe_inventory")))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(name.getNamespace(), "block/" + name.getPath()));
    }

    public ItemModelBuilder handHeldItem(Item item) {
        return handHeldItem(item, "");
    }

    public ItemModelBuilder handHeldItem(Item item, String suffix) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);
        return getBuilder(location +suffix)
                .parent(new ModelFile.UncheckedModelFile("item/handheld"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "item/" + location.getPath()+suffix));
    }
}
