package com.thepigcat.buildcraft.client.screens.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class LazyImageButton extends AbstractButton {
    private final ResourceLocation sprite;
    private final int spriteWidth;
    private final int spriteHeight;
    private final Consumer<LazyImageButton> onPressFunction;
    private Component hoverText = Component.empty();

    public LazyImageButton(ResourceLocation sprite, int spriteWidth, int spriteHeight, int x, int y, int width, int height, Consumer<LazyImageButton> onPressFunction) {
        super(x, y, width, height, Component.empty());
        this.sprite = sprite;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.onPressFunction = onPressFunction;
    }

    public Component getHoverText() {
        return hoverText;
    }

    public void setHoverText(Component hoverText) {
        this.hoverText = hoverText;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        int paddingX = (this.getWidth() - this.spriteWidth) / 2;
        int paddingY = (this.getHeight() - this.spriteHeight) / 2;

        guiGraphics.blitSprite(this.sprite, getX() + paddingX, getY() + paddingY, this.spriteWidth, this.spriteHeight);

        if (this.isHovered()) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, this.hoverText, mouseX, mouseY);
        }

    }

    @Override
    public void onPress() {
        onPressFunction.accept(this);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
