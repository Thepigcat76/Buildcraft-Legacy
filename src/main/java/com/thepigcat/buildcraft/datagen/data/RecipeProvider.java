package com.thepigcat.buildcraft.datagen.data;

import com.thepigcat.buildcraft.registries.FPBlocks;
import com.thepigcat.buildcraft.registries.FPItems;
import com.thepigcat.buildcraft.tags.FPTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    public RecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FPItems.WRENCH)
                .pattern("I I")
                .pattern(" G ")
                .pattern(" I ")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', FPTags.Items.STONE_GEAR)
                .unlockedBy("has_stone_gear", has(FPTags.Items.STONE_GEAR))
                .save(recipeOutput);

        gearRecipe(recipeOutput, ItemTags.PLANKS, null, FPItems.WOODEN_GEAR);
        gearRecipe(recipeOutput, Tags.Items.COBBLESTONES, FPTags.Items.WOODEN_GEAR, FPItems.STONE_GEAR);
        gearRecipe(recipeOutput, Tags.Items.INGOTS_IRON, FPTags.Items.STONE_GEAR, FPItems.IRON_GEAR);
        gearRecipe(recipeOutput, Tags.Items.INGOTS_GOLD, FPTags.Items.IRON_GEAR, FPItems.GOLD_GEAR);
        gearRecipe(recipeOutput, Tags.Items.GEMS_DIAMOND, FPTags.Items.GOLD_GEAR, FPItems.DIAMOND_GEAR);

        pipeRecipe(recipeOutput, ItemTags.PLANKS, FPBlocks.WOODEN_ITEM_PIPE);
        pipeRecipe(recipeOutput, Tags.Items.COBBLESTONES, FPBlocks.COBBLESTONE_ITEM_PIPE);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, FPBlocks.CRATE)
                .pattern("LSL")
                .pattern("L L")
                .pattern("LSL")
                .define('L', ItemTags.LOGS)
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_log", has(ItemTags.LOGS))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, FPBlocks.TANK)
                .pattern("GGG")
                .pattern("G G")
                .pattern("GGG")
                .define('G', Tags.Items.GLASS_BLOCKS)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput);
    }

    private void pipeRecipe(RecipeOutput recipeOutput, TagKey<Item> material, ItemLike result) {
        String path = material.location().getPath();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result, 8)
                .pattern("MGM")
                .define('M', material)
                .define('G', Tags.Items.GLASS_BLOCKS)
                .unlockedBy("has_"+path, has(material))
                .save(recipeOutput);
    }

    private void gearRecipe(RecipeOutput recipeOutput, TagKey<Item> material, @Nullable TagKey<Item> previous, ItemLike result) {
        String path = material.location().getPath();
        if (previous != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
                    .pattern(" M ")
                    .pattern("MPM")
                    .pattern(" M ")
                    .define('M', material)
                    .define('P', previous)
                    .unlockedBy("has_" + path, has(material))
                    .save(recipeOutput);
        } else {
            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
                    .pattern(" M ")
                    .pattern("M M")
                    .pattern(" M ")
                    .define('M', material)
                    .unlockedBy("has_" + path, has(material))
                    .save(recipeOutput);
        }
    }
}
