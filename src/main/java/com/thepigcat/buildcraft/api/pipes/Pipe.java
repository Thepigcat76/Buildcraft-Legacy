package com.thepigcat.buildcraft.api.pipes;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record Pipe(boolean disabled, ResourceLocation type, float transferSpeed, ResourceLocation texture) {
    public static final Codec<Pipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.BOOL.fieldOf("disabled").forGetter(Pipe::disabled),
            ResourceLocation.CODEC.fieldOf("type").forGetter(Pipe::type),
            Codec.FLOAT.fieldOf("transfer_speed").forGetter(Pipe::transferSpeed),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(Pipe::texture)
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

}
