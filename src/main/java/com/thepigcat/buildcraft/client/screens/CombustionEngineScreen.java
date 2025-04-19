package com.thepigcat.buildcraft.client.screens;

import com.portingdeadmods.portingdeadlibs.api.blockentities.ContainerBlockEntity;
import com.portingdeadmods.portingdeadlibs.api.client.screens.PDLAbstractContainerScreen;
import com.portingdeadmods.portingdeadlibs.api.gui.utils.FluidTankRenderer;
import com.portingdeadmods.portingdeadlibs.impl.client.screens.widgets.FluidTankWidget;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.client.screens.widgets.LazyImageButton;
import com.thepigcat.buildcraft.client.screens.widgets.RedstoneWidget;
import com.thepigcat.buildcraft.content.menus.CombustionEngineMenu;
import com.thepigcat.buildcraft.util.CapabilityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.thepigcat.buildcraft.client.screens.StirlingEngineScreen.LIT_PROGRESS_SPRITE;

public class CombustionEngineScreen extends PDLAbstractContainerScreen<CombustionEngineMenu> {
    private RedstoneWidget redstoneWidget;

    public CombustionEngineScreen(CombustionEngineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 176;
        this.inventoryLabelY += 11;
    }

    @Override
    protected void init() {
        super.init();

        int fluidX = this.leftPos + (4 * 18) + 7;
        int fluidY = this.topPos + 19;
        addRenderableWidget(new FluidTankWidget(fluidX, fluidY, FluidTankWidget.TankVariants.SMALL, menu.blockEntity));

        redstoneWidget = new RedstoneWidget(menu.blockEntity, this.leftPos + this.imageWidth, this.topPos + 2, 32, 32);
        redstoneWidget.visitWidgets(this::addRenderableWidget);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, delta, mouseX, mouseY);
        renderLitProgress(guiGraphics);
    }

    protected void renderLitProgress(GuiGraphics pGuiGraphics) {
        int i = this.leftPos;
        int j = this.topPos;
        if (this.menu.getBlockEntity().isActive()) {
            float burnTime = ((float) this.menu.getBlockEntity().getFluidHandler().getFluidInTank(0).getAmount() / this.menu.getBlockEntity().getFluidHandler().getTankCapacity(0));
            int j1 = Mth.ceil(burnTime * 13F);
            pGuiGraphics.blitSprite(LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - j1, i + 80, j + 89 - j1 - 1, 14, j1);
        }
    }

    @Override
    public @NotNull ResourceLocation getBackgroundTexture() {
        return ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "textures/gui/combustion_engine.png");
    }

    public List<Rect2i> getBounds() {
        return this.redstoneWidget != null ? List.of(this.redstoneWidget.getBounds()) : Collections.emptyList();
    }
}
