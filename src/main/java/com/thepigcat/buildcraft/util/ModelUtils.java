package com.thepigcat.buildcraft.util;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.PipesRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class ModelUtils {
    public static final Function<ResourceLocation, String> BLOCK_MODEL_DEFINITION = (pipeId) -> {
        String pipeIdLiteral = pipeId.withPrefix("block/").toString();
        return """
                {"multipart":[{"apply":{"model":"%s_connection"},"when":{"down":"connected"}},
                {"apply":{"model":"%s_connection","x":180},"when":{"up":"connected"}},
                {"apply":{"model":"%s_connection","x":90,"y":180},"when":{"north":"connected"}},
                {"apply":{"model":"%s_connection","x":90,"y":270},"when":{"east":"connected"}},
                {"apply":{"model":"%s_connection","x":90},"when":{"south":"connected"}},
                {"apply":{"model":"%s_connection","x":90,"y":90},"when":{"west":"connected"}},
                {"apply":{"model":"%s_base"}}]}""".formatted(pipeIdLiteral, pipeIdLiteral, pipeIdLiteral, pipeIdLiteral, pipeIdLiteral, pipeIdLiteral, pipeIdLiteral);
    };

    public static final BiFunction<String, String, String> DEFAULT_ITEM_MODEL_FILE = ((parent, pipeId) -> """
            {
              "parent": "%s",
              "textures": {
                "texture": "%s"
              }
            }""".formatted(parent, PipesRegistry.PIPES.get(pipeId).texture().toString()));

    public static final BiFunction<String, ResourceLocation, String> DEFAULT_BLOCK_MODEL_FILE = ((parent, texture) -> """
            {
              "parent": "%s",
              "textures": {
                "texture": "%s"
              }
            }""".formatted(parent, texture.toString()));

    public static ResourceLocation modelLocationToBlockId(ResourceLocation modelLocation) {
        String[] split = modelLocation.getPath().split("/");
        String name = split[split.length - 1];
        return ResourceLocation.fromNamespaceAndPath(modelLocation.getNamespace(), name);
    }

    public enum ParentType implements StringRepresentable {
        BASE("base"),
        CONNECTION("connection");

        private final String parentType;

        ParentType(String parentType) {
            this.parentType = parentType;
        }

        @Override
        public @NotNull String getSerializedName() {
            return parentType;
        }
    }

}
