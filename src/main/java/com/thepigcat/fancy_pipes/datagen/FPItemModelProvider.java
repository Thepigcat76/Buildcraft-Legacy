package com.thepigcat.fancy_pipes.datagen;

import com.thepigcat.fancy_pipes.FancyPipes;
import com.thepigcat.fancy_pipes.api.blocks.PipeBlock;
import com.thepigcat.fancy_pipes.registries.FPItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Objects;

public class FPItemModelProvider extends ItemModelProvider {
    public FPItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, FancyPipes.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handHeldItem(FPItems.WRENCH.get());

        blockItems();
    }

    private void blockItems() {
        for (DeferredItem<BlockItem> blockItem : FPItems.BLOCK_ITEMS) {
            if (blockItem.get().getBlock() instanceof PipeBlock) {
                pipeItemModel(blockItem.get());
            } else {
                parentItemBlock(blockItem.get());
            }
        }
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
