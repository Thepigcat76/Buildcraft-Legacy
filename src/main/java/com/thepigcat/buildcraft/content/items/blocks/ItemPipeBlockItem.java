package com.thepigcat.buildcraft.content.items.blocks;

import com.thepigcat.buildcraft.PipesRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ItemPipeBlockItem extends BlockItem {
    public ItemPipeBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return PipesRegistry.PIPES.get(this.builtInRegistryHolder().key().location().getPath()).name().map(Component::translatable).orElse((MutableComponent) super.getName(stack));
    }
}
