package com.thepigcat.buildcraft.api.capabilties;

import com.thepigcat.buildcraft.data.BCDataComponents;
import com.thepigcat.buildcraft.data.components.BigStackContainerContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JumboItemHandlerItemWrapper extends JumboItemHandler {
    private final ItemStack wrapped;

    public JumboItemHandlerItemWrapper(ItemStack stack) {
        super(getContainerContents(stack).getSlotLimit());
        this.wrapped = stack;
    }

    @Override
    public List<BigStack> getItems() {
        return getContainerContents(this.wrapped).getItems();
    }

    private static @NotNull BigStackContainerContents getContainerContents(ItemStack stack) {
        return stack.getOrDefault(BCDataComponents.CRATE_CONTENT.get(), BigStackContainerContents.EMPTY);
    }
}
