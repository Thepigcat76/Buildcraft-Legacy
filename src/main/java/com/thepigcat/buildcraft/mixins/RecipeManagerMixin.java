package com.thepigcat.buildcraft.mixins;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.PipesRegistry;
import com.thepigcat.buildcraft.api.pipes.Pipe;
import com.thepigcat.buildcraft.datagen.data.BCRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Shadow
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Shadow private Map<ResourceLocation, RecipeHolder<?>> byName;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void buildcraft$apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        this.byType = HashMultimap.create(this.byType);
        this.byName = new HashMap<>(this.byName);
        RecipeOutput output = new RecipeOutput() {
            @Override
            public Advancement.Builder advancement() {
                return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }

            @Override
            public void accept(ResourceLocation resourceLocation, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder, ICondition... iConditions) {
                RecipeHolder<? extends Recipe<?>> holder = new RecipeHolder<>(resourceLocation, recipe);
                RecipeManagerMixin.this.byType.put(RecipeType.CRAFTING, holder);
                RecipeManagerMixin.this.byName.put(resourceLocation, holder);
            }
        };
        for (Map.Entry<String, Pipe> entry : PipesRegistry.PIPES.entrySet()) {
            if (!entry.getValue().customRecipe()) {
                ResourceLocation rl = BuildcraftLegacy.rl(entry.getKey());
                ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BuiltInRegistries.ITEM.get(rl), 8)
                        .pattern("IGI")
                        .define('I', entry.getValue().ingredient())
                        .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS)
                        .unlockedBy("has_item", BCRecipeProvider.has(entry.getValue().ingredient().getValues()[0] instanceof Ingredient.TagValue(
                                TagKey<Item> tag
                        ) ? tag : Tags.Items.GLASS_BLOCKS_COLORLESS))
                        .save(output);
            }
        }
        this.byType = ImmutableMultimap.copyOf(this.byType);
        this.byName = Collections.unmodifiableMap(this.byName);
    }
}
