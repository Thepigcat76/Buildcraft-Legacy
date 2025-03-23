package com.thepigcat.buildcraft.util;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class ModelUtils {
    public static final Function<String, String> BLOCK_MODEL_DEFINITION = (s) -> """
            {"multipart":[{"apply":{"model":"buildcraft:block/%s_connection"},"when":{"down":"connected"}},
            {"apply":{"model":"buildcraft:block/%s_connection","x":180},"when":{"up":"connected"}},
            {"apply":{"model":"buildcraft:block/%s_connection","x":90,"y":180},"when":{"north":"connected"}},
            {"apply":{"model":"buildcraft:block/%s_connection","x":90,"y":270},"when":{"east":"connected"}},
            {"apply":{"model":"buildcraft:block/%s_connection","x":90},"when":{"south":"connected"}},
            {"apply":{"model":"buildcraft:block/%s_connection","x":90,"y":90},"when":{"west":"connected"}},
            {"apply":{"model":"buildcraft:block/%s_base"}}]}""".formatted(s, s, s, s, s, s, s);

    public static final Function<ResourceLocation, String> BLOCK_MODEL_FILE = (rl) -> """
            {
              "parent": "buildcraft:block/pipe_base",
              "textures": {
                "texture": "%s"
              }
            }""".formatted(rl.toString());

    public static ResourceLocation modelLocationToBlockId(ResourceLocation modelLocation) {
        String[] split = modelLocation.getPath().split("/");
        String name = split[split.length - 1];
        return ResourceLocation.fromNamespaceAndPath(modelLocation.getNamespace(), name);
    }
}
