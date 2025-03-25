package com.thepigcat.buildcraft.api.pipes;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.portingdeadmods.portingdeadlibs.utils.codec.CodecUtils;
import com.thepigcat.buildcraft.registries.BCBlocks;
import com.thepigcat.buildcraft.registries.BCPipeTypes;
import com.thepigcat.buildcraft.util.PipeRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

// TODO: tab ordering, drop item, custom drop, mining tools, custom mining tools, recipe ingredient, custom recipe
public record Pipe(boolean disabled, ResourceLocation type, Optional<String> name, float transferSpeed,
                   List<ResourceLocation> textures, Either<BlockBehaviour.Properties, ResourceLocation> properties,
                   ResourceLocation dropItem, Ingredient ingredient, List<TagKey<Block>> miningTools, int tabOrdering,
                   boolean customBlockModel, boolean customItemModel, boolean customRecipe, boolean customLoottable,
                   boolean customTags) {
    public static final Codec<Pipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.BOOL.optionalFieldOf("disabled", false).forGetter(Pipe::disabled),
            ResourceLocation.CODEC.optionalFieldOf("type", BCPipeTypes.DEFAULT.key()).forGetter(Pipe::type),
            Codec.STRING.optionalFieldOf("name").forGetter(Pipe::name),
            Codec.FLOAT.optionalFieldOf("transfer_speed", 0.1f).forGetter(Pipe::transferSpeed),
            ResourceLocation.CODEC.listOf().optionalFieldOf("textures", List.of()).forGetter(Pipe::textures),
            Codec.either(BlockBehaviour.Properties.CODEC.fieldOf("properties").codec(), ResourceLocation.CODEC.fieldOf("copy_of").codec()).optionalFieldOf("properties", Either.right(ResourceLocation.parse("cobblestone"))).forGetter(Pipe::properties),
            ResourceLocation.CODEC.optionalFieldOf("drop_item", ResourceLocation.parse("air")).forGetter(Pipe::dropItem),
            Ingredient.CODEC.optionalFieldOf("ingredient", Ingredient.EMPTY).forGetter(Pipe::ingredient),
            TagKey.codec(Registries.BLOCK).listOf().optionalFieldOf("mining_tools", List.of()).forGetter(Pipe::miningTools),
            Codec.INT.optionalFieldOf("tab_ordering_priority", -1).forGetter(Pipe::tabOrdering),
            Codec.BOOL.optionalFieldOf("has_custom_block_model", false).forGetter(Pipe::customBlockModel),
            Codec.BOOL.optionalFieldOf("has_custom_item_model", false).forGetter(Pipe::customItemModel),
            Codec.BOOL.optionalFieldOf("has_custom_recipe", false).forGetter(Pipe::customRecipe),
            Codec.BOOL.optionalFieldOf("has_custom_loottable", false).forGetter(Pipe::customLoottable),
            Codec.BOOL.optionalFieldOf("has_custom_tags", false).forGetter(Pipe::customTags)
    ).apply(inst, Pipe::new));

    public static Pipe fromJson(JsonElement json) {
        DataResult<Pair<Pipe, JsonElement>> decodeResult = CODEC.decode(JsonOps.INSTANCE, json);
        if (decodeResult.isSuccess()) {
            return decodeResult.getOrThrow().getFirst();
        }
        return null;
    }

    public JsonElement toJson() {
        DataResult<JsonElement> encodeResult = CODEC.encodeStart(JsonOps.INSTANCE, this);
        if (encodeResult.isSuccess()) {
            return encodeResult.getOrThrow();
        }
        return null;
    }

    public PipeType<?, ?> getType() {
        return PipeRegistrationHelper.PIPE_TYPES.getOrDefault(type, BCPipeTypes.DEFAULT.value());
    }

    public Collection<? extends TagKey<Block>> getMiningTools(){
        return this.miningTools;
    }

}
