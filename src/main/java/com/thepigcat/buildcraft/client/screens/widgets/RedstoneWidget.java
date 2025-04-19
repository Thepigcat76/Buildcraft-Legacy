package com.thepigcat.buildcraft.client.screens.widgets;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RedstoneWidget extends AbstractWidget {
    public static final ResourceLocation WIDGET_SPRITE = BuildcraftLegacy.rl("widget/widget_redstone_control_right");
    public static final ResourceLocation WIDGET_OPEN_SPRITE = BuildcraftLegacy.rl("widget/redstone_widget_open");
    public static final int WIDGET_WIDTH = 32, WIDGET_HEIGHT = 32;
    public static final int WIDGET_OPEN_WIDTH = 80, WIDGET_OPEN_HEIGHT = 80;

    private final LazyImageButton buttonNoControl;
    private final LazyImageButton buttonLowSignal;
    private final LazyImageButton buttonHighSignal;

    private boolean open;

    public RedstoneWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        int y1 = y + 18;
        this.buttonNoControl = new LazyImageButton(BuildcraftLegacy.rl("redstone"), 16, 16, x + 7, y1, 18, 18, btn -> {});
        this.buttonNoControl.visible = false;
        this.buttonNoControl.setHoverText(Component.literal("Ignored"));
        this.buttonLowSignal = new LazyImageButton(BuildcraftLegacy.rl("redstone_torch_off"), 16, 16, x + 7 + 22, y1, 18, 18, btn -> {});
        this.buttonLowSignal.visible = false;
        this.buttonLowSignal.setHoverText(Component.literal("Low"));
        this.buttonHighSignal = new LazyImageButton(BuildcraftLegacy.rl("redstone_torch_on"), 16, 16, x + 7 + 44, y1, 18, 18, btn -> {});
        this.buttonHighSignal.visible = false;
        this.buttonHighSignal.setHoverText(Component.literal("High"));

        this.open = false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        boolean isHovered = mouseX >= this.getX()
                && mouseY >= this.getY()
                && mouseX < this.getX() + WIDGET_WIDTH
                && mouseY < this.getY() + WIDGET_HEIGHT;

        if (isHovered && !this.buttonNoControl.isHovered() && !this.buttonLowSignal.isHovered() && !this.buttonHighSignal.isHovered()) {
            this.open = !this.open;
            this.buttonNoControl.visible = this.open;
            this.buttonLowSignal.visible = this.open;
            this.buttonHighSignal.visible = this.open;

            if (open) {
                setSize(WIDGET_OPEN_WIDTH, WIDGET_OPEN_HEIGHT);
            } else {
                setSize(WIDGET_WIDTH, WIDGET_HEIGHT);
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        if (open) {
            guiGraphics.blitSprite(WIDGET_OPEN_SPRITE, getX(), getY(), WIDGET_OPEN_WIDTH, WIDGET_OPEN_HEIGHT);

            this.buttonNoControl.render(guiGraphics, i, i1, v);
            this.buttonLowSignal.render(guiGraphics, i, i1, v);
            this.buttonHighSignal.render(guiGraphics, i, i1, v);

            Font font = Minecraft.getInstance().font;

            guiGraphics.drawString(font, Component.literal("Redstone").withStyle(ChatFormatting.GRAY), getX() + 5, getY() + 44, -1);
            //guiGraphics.drawString(font, )
        } else {
            guiGraphics.blitSprite(WIDGET_SPRITE, getX(), getY(), WIDGET_WIDTH, WIDGET_HEIGHT);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    public Rect2i getBounds() {
        return new Rect2i(getX(), getY(), this.getWidth(), this.getHeight());
    }

}
