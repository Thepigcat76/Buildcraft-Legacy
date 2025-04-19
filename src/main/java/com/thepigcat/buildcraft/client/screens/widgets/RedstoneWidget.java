package com.thepigcat.buildcraft.client.screens.widgets;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.blockentities.RedstoneBlockEntity;
import com.thepigcat.buildcraft.networking.RedstoneSignalTypeSyncPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Consumer;

public class RedstoneWidget extends AbstractWidget {
    public static final ResourceLocation WIDGET_SPRITE = BuildcraftLegacy.rl("widget/widget_redstone_control_right");
    public static final ResourceLocation WIDGET_OPEN_SPRITE = BuildcraftLegacy.rl("widget/redstone_widget_open");
    public static final int WIDGET_WIDTH = 32, WIDGET_HEIGHT = 32;
    public static final int WIDGET_OPEN_WIDTH = 80, WIDGET_OPEN_HEIGHT = 80;

    private final LazyImageButton[] buttons;
    private final LazyImageButton buttonNoControl;
    private final LazyImageButton buttonLowSignal;
    private final LazyImageButton buttonHighSignal;
    private boolean open;
    private RedstoneBlockEntity redstoneBlockEntity;

    public RedstoneWidget(RedstoneBlockEntity blockEntity, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        int y1 = y + 28;
        this.buttonNoControl = new LazyImageButton(BuildcraftLegacy.rl("redstone"), 16, 16, x + 7, y1, 18, 18, btn -> {
            PacketDistributor.sendToServer(new RedstoneSignalTypeSyncPayload(this.redstoneBlockEntity.self().getBlockPos(), RedstoneBlockEntity.RedstoneSignalType.IGNORED));
            this.setFocusForButton(RedstoneBlockEntity.RedstoneSignalType.IGNORED);
        });
        this.buttonNoControl.visible = false;
        this.buttonNoControl.setHoverText(Component.literal("Ignored"));
        this.buttonLowSignal = new LazyImageButton(BuildcraftLegacy.rl("redstone_torch_off"), 16, 16, x + 7 + 22, y1, 18, 18, btn -> {
            PacketDistributor.sendToServer(new RedstoneSignalTypeSyncPayload(this.redstoneBlockEntity.self().getBlockPos(), RedstoneBlockEntity.RedstoneSignalType.LOW_SIGNAL));
            this.setFocusForButton(RedstoneBlockEntity.RedstoneSignalType.LOW_SIGNAL);
        });
        this.buttonLowSignal.visible = false;
        this.buttonLowSignal.setHoverText(Component.literal("Low"));
        this.buttonHighSignal = new LazyImageButton(BuildcraftLegacy.rl("redstone_torch_on"), 16, 16, x + 7 + 44, y1, 18, 18, btn -> {
            PacketDistributor.sendToServer(new RedstoneSignalTypeSyncPayload(this.redstoneBlockEntity.self().getBlockPos(), RedstoneBlockEntity.RedstoneSignalType.HIGH_SIGNAL));
            this.setFocusForButton(RedstoneBlockEntity.RedstoneSignalType.HIGH_SIGNAL);
        });
        this.buttonHighSignal.visible = false;
        this.buttonHighSignal.setHoverText(Component.literal("High"));

        this.buttons = new LazyImageButton[]{this.buttonNoControl, this.buttonLowSignal, this.buttonHighSignal};

        this.open = false;
        this.redstoneBlockEntity = blockEntity;

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
                this.setFocusForButtons();
            }

            if (open) {
                this.setSize(WIDGET_OPEN_WIDTH, WIDGET_OPEN_HEIGHT);
            } else {
                this.setSize(WIDGET_WIDTH, WIDGET_HEIGHT);
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);

        for (LazyImageButton button : getButtons()) {
            consumer.accept(button);
        }
    }

    private void setFocusForButton(RedstoneBlockEntity.RedstoneSignalType signalType) {
        for (LazyImageButton redstoneButton : this.buttons) {
            redstoneButton.setFocused(false);
        }
        LazyImageButton redstoneButton = getButtonBySignalType(signalType);
        redstoneButton.setFocused(true);
    }

    private void setFocusForButtons() {
        for (LazyImageButton redstoneButton : this.buttons) {
            redstoneButton.setFocused(false);
        }
        LazyImageButton redstoneButton = getButtonBySignalType(this.redstoneBlockEntity.getRedstoneSignalType());
        redstoneButton.setFocused(true);
    }

    private LazyImageButton getButtonBySignalType(RedstoneBlockEntity.RedstoneSignalType signalType) {
        return switch (signalType) {
            case IGNORED -> this.buttonNoControl;
            case LOW_SIGNAL -> this.buttonLowSignal;
            case HIGH_SIGNAL -> this.buttonHighSignal;
        };
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        if (open) {
            Font font = Minecraft.getInstance().font;

            guiGraphics.blitSprite(WIDGET_OPEN_SPRITE, getX(), getY(), WIDGET_OPEN_WIDTH, WIDGET_OPEN_HEIGHT);

            guiGraphics.renderFakeItem(new ItemStack(Items.REDSTONE), getX() + 3, getY() + 8);
            guiGraphics.drawString(font, Component.literal("Redstone").withStyle(ChatFormatting.WHITE), getX() + 20, getY() + 13, -1);

            guiGraphics.drawString(font, Component.literal("Signal").withStyle(ChatFormatting.GRAY), getX() + 5, getY() + 54, -1);
            RedstoneBlockEntity.RedstoneSignalType signalType = this.redstoneBlockEntity.getRedstoneSignalType();
            if (signalType == null) signalType = RedstoneBlockEntity.RedstoneSignalType.IGNORED;
            guiGraphics.drawString(font, Component.translatable("redstone_signal_type." + BuildcraftLegacy.MODID + "." + signalType.getSerializedName()).withStyle(ChatFormatting.WHITE), getX() + 5, getY() + 54 + font.lineHeight + 2, -1);
        } else {
            guiGraphics.blitSprite(WIDGET_SPRITE, getX(), getY(), WIDGET_WIDTH, WIDGET_HEIGHT);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    public LazyImageButton[] getButtons() {
        return buttons;
    }

    public Rect2i getBounds() {
        return new Rect2i(getX(), getY(), this.getWidth(), this.getHeight());
    }

}
