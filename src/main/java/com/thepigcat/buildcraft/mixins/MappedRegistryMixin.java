package com.thepigcat.buildcraft.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.PipesRegistry;
import com.thepigcat.buildcraft.api.pipes.Pipe;
import com.thepigcat.buildcraft.registries.BCPipes;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(MappedRegistry.class)
public class MappedRegistryMixin {
    @Inject(method = "bindTags", at = @At(value = "INVOKE", target = "Ljava/util/IdentityHashMap;<init>(Ljava/util/Map;)V"))
    private void buildcraft$bindTags(Map<TagKey<?>, List<Holder<?>>> tagMap, CallbackInfo ci, @Local(ordinal = 1) Map<Holder.Reference<?>, List<TagKey<?>>> map) {
        Optional<TagKey<?>> tagKey = tagMap.keySet().stream().findFirst();
        if (tagKey.isPresent() && tagKey.get().registry() == Registries.BLOCK) {
            for (Map.Entry<String, Pipe> entry : PipesRegistry.PIPES.entrySet()) {
                Holder.Reference<?> holder = BuiltInRegistries.BLOCK.getHolderOrThrow(ResourceKey.create(Registries.BLOCK, BuildcraftLegacy.rl(entry.getKey())));
                List<TagKey<?>> tagKeys = map.computeIfAbsent(holder, k -> new ArrayList<>());
                tagKeys.addAll(entry.getValue().getMiningTools());
            }
        }
    }
}
