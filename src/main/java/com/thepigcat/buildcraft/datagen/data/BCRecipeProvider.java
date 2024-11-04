package com.thepigcat.buildcraft.datagen.data;

import com.thepigcat.buildcraft.registries.BCBlocks;
import com.thepigcat.buildcraft.registries.BCItems;
import com.thepigcat.buildcraft.tags.BCTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BCRecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    public BCRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BCItems.WRENCH)
                .pattern("I I")
                .pattern(" G ")
                .pattern(" I ")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', BCTags.Items.STONE_GEAR)
                .unlockedBy("has_stone_gear", has(BCTags.Items.STONE_GEAR))
                .save(recipeOutput);

        gearRecipe(recipeOutput, ItemTags.PLANKS, null, BCItems.WOODEN_GEAR);
        gearRecipe(recipeOutput, Tags.Items.COBBLESTONES, BCTags.Items.WOODEN_GEAR, BCItems.STONE_GEAR);
        gearRecipe(recipeOutput, Tags.Items.INGOTS_IRON, BCTags.Items.STONE_GEAR, BCItems.IRON_GEAR);
        gearRecipe(recipeOutput, Tags.Items.INGOTS_GOLD, BCTags.Items.IRON_GEAR, BCItems.GOLD_GEAR);
        gearRecipe(recipeOutput, Tags.Items.GEMS_DIAMOND, BCTags.Items.GOLD_GEAR, BCItems.DIAMOND_GEAR);

        pipeRecipe(recipeOutput, ItemTags.PLANKS, BCBlocks.WOODEN_ITEM_PIPE);
        pipeRecipe(recipeOutput, Tags.Items.COBBLESTONES, BCBlocks.COBBLESTONE_ITEM_PIPE);

        engineRecipe(recipeOutput, ItemTags.PLANKS, BCTags.Items.WOODEN_GEAR, BCBlocks.REDSTONE_ENGINE);
        engineRecipe(recipeOutput, Tags.Items.COBBLESTONES, BCTags.Items.STONE_GEAR, BCBlocks.STIRLING_ENGINE);
        engineRecipe(recipeOutput, Tags.Items.INGOTS_IRON, BCTags.Items.IRON_GEAR, BCBlocks.COMBUSTION_ENGINE);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCBlocks.CRATE)
                .pattern("LSL")
                .pattern("L L")
                .pattern("LSL")
                .define('L', ItemTags.LOGS)
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_log", has(ItemTags.LOGS))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCBlocks.TANK)
                .pattern("GGG")
                .pattern("G G")
                .pattern("GGG")
                .define('G', Tags.Items.GLASS_BLOCKS)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
                .save(recipeOutput);
    }

    private void engineRecipe(RecipeOutput recipeOutput, TagKey<Item> material, TagKey<Item> gear, ItemLike result) {
        String path = material.location().getPath();
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result)
                .pattern("MMM")
                .pattern(" L ")
                .pattern("GPG")
                .define('M', material)
                .define('L', Tags.Items.GLASS_BLOCKS)
                .define('G', gear)
                .define('P', Items.PISTON)
                .unlockedBy("has_"+path, has(material))
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
