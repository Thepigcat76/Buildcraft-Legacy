package com.thepigcat.buildcraft.client.screens.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

// TODO: Move this to pdl
public class ItemButton extends AbstractButton {
    public static final int ITEM_SIZE = 16;

    private ItemStack item;
    private final Consumer<ItemButton> onPressFunction;

    public ItemButton(Item item, int x, int y, int width, int height, Consumer<ItemButton> onPressFunction) {
        super(x, y, width, height, Component.empty());
        this.item = new ItemStack(item);
        this.onPressFunction = onPressFunction;
    }

    public Item getItem() {
        return item.getItem();
    }

    public void setItem(Item item) {
        this.item = new ItemStack(item);
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.renderItem(this.item, (int) (getX() + ((float) (this.width - ITEM_SIZE) / 2)), (int) (getY() + ((float) (this.height - ITEM_SIZE) / 2)));
    }

    @Override
    public void onPress() {
        onPressFunction.accept(this);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
