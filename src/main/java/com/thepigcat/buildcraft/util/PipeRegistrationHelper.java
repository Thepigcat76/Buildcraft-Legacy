package com.thepigcat.buildcraft.util;

import com.thepigcat.buildcraft.api.pipes.Pipe;
import com.thepigcat.buildcraft.api.pipes.PipeHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class PipeRegistrationHelper {
    private final Map<String, Pipe> pipes;

    public PipeRegistrationHelper() {
        this.pipes = new HashMap<>();
    }

    public PipeHolder register(String name, boolean disabled, float transferSpeed, boolean extracting) {
        String key = name + "_pipe";
        Pipe value = new Pipe(false, transferSpeed, extracting, ResourceLocation.parse(""));
        this.pipes.put(key, value);
        return new PipeHolder(key, value);
    }

    public PipeHolder register(String name, float transferSpeed, boolean extracting) {
        return register(name, false, transferSpeed, extracting);
    }

    public Map<String, Pipe> getPipes() {
        return this.pipes;
    }

}
