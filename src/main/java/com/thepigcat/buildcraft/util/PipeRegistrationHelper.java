package com.thepigcat.buildcraft.util;

import com.mojang.datafixers.util.Either;
import com.thepigcat.buildcraft.api.pipes.Pipe;
import com.thepigcat.buildcraft.api.pipes.PipeHolder;
import com.thepigcat.buildcraft.api.pipes.PipeType;
import com.thepigcat.buildcraft.api.pipes.PipeTypeHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class PipeRegistrationHelper {
    public static final Map<ResourceLocation, PipeType<?, ?>> PIPE_TYPES = new HashMap<>();
    private final String modId;
    private final Map<String, Pipe> pipes;

    public PipeRegistrationHelper(String modId) {
        this.modId = modId;
        this.pipes = new HashMap<>();
    }

    public <B extends Block, I extends BlockItem> PipeTypeHolder<B, I> registerPipeType(String name,
                                                                                        Function<BlockBehaviour.Properties, B> blockConstructor,
                                                                                        BiFunction<Block, Item.Properties, I> blockItemConstructor,
                                                                                        BiFunction<Pipe, ResourceLocation, String> blockModelDefinition,
                                                                                        BiFunction<Pipe, ResourceLocation, String> defaultBlockModel,
                                                                                        BiFunction<Pipe, String, String> defaultItemModel,
                                                                                        String... models) {
        ResourceLocation key = ResourceLocation.fromNamespaceAndPath(this.modId, name);
        PipeType<B, I> value = new PipeType<>(blockConstructor, blockItemConstructor, blockModelDefinition, defaultBlockModel, defaultItemModel, List.of(models));
        PIPE_TYPES.put(key, value);
        return new PipeTypeHolder<>(key, value);
    }

    public PipeHolder registerPipe(String id, boolean disabled, PipeTypeHolder<?, ?> pipeType, String name, float transferSpeed,
                                   List<ResourceLocation> textures, Either<BlockBehaviour.Properties, Block> properties,
                                   boolean customBlockModel, boolean customItemModel) {
        String key = id + "_pipe";
        Pipe value = new Pipe(disabled, pipeType.key(), Optional.ofNullable(name), transferSpeed, textures, properties, customBlockModel, customItemModel);
        this.pipes.put(key, value);
        return new PipeHolder(key, value);
    }

    public PipeHolder registerPipe(String id, PipeTypeHolder<?, ?> pipeType, String name, float transferSpeed,
                                   List<ResourceLocation> textures, Either<BlockBehaviour.Properties, Block> properties) {
        return registerPipe(id, false, pipeType, name, transferSpeed, textures, properties, false, false);
    }

    public Map<String, Pipe> getPipes() {
        return this.pipes;
    }

}
