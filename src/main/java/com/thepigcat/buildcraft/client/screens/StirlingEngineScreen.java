package com.thepigcat.buildcraft.client.screens;

import com.portingdeadmods.portingdeadlibs.api.client.screens.PDLAbstractContainerScreen;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.client.screens.widgets.RedstoneWidget;
import com.thepigcat.buildcraft.content.menus.StirlingEngineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class StirlingEngineScreen extends PDLAbstractContainerScreen<StirlingEngineMenu> {
    public static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.parse("container/smoker/lit_progress");
    private RedstoneWidget redstoneWidget;

    public StirlingEngineScreen(StirlingEngineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();

        redstoneWidget = new RedstoneWidget(menu.blockEntity, this.leftPos + this.imageWidth, this.topPos + 2, 32, 32);
        redstoneWidget.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        renderLitProgress(guiGraphics);
    }

    protected void renderLitProgress(GuiGraphics pGuiGraphics) {
        int i = this.leftPos;
        int j = this.topPos;
        if (this.menu.getBlockEntity().isActive()) {
            float burnTime = ((float) this.menu.getBlockEntity().getBurnProgress() / this.menu.getBlockEntity().getBurnTime());
            int j1 = Mth.ceil(burnTime * 13F);
            pGuiGraphics.blitSprite(LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - j1, i + 80, j + 20 + 14 - j1 - 1, 14, j1);
        }
    }

    @Override
    public @NotNull ResourceLocation getBackgroundTexture() {
        return ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "textures/gui/stirling_engine.png");
    }

    // TODO: Move this to pdl
    public List<Rect2i> getBounds() {
        return this.redstoneWidget != null ? List.of(this.redstoneWidget.getBounds()) : Collections.emptyList();
    }

}
