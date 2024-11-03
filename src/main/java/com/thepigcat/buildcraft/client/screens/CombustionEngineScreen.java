package com.thepigcat.buildcraft.client.screens;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.api.blockentities.ContainerBlockEntity;
import com.thepigcat.buildcraft.api.client.screens.BCAbstractContainerScreen;
import com.thepigcat.buildcraft.api.client.utils.FluidTankRenderer;
import com.thepigcat.buildcraft.api.menus.BCAbstractContainerMenu;
import com.thepigcat.buildcraft.content.blockentities.CombustionEngineBE;
import com.thepigcat.buildcraft.util.CapabilityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.thepigcat.buildcraft.client.screens.StirlingEngineScreen.LIT_PROGRESS_SPRITE;

public class CombustionEngineScreen extends BCAbstractContainerScreen<CombustionEngineBE> {
    private FluidTankRenderer fluidTankRenderer;

    public CombustionEngineScreen(BCAbstractContainerMenu<CombustionEngineBE> menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        ContainerBlockEntity blockEntity = this.getMenu().getBlockEntity();
        this.fluidTankRenderer = new FluidTankRenderer(CapabilityUtils.fluidHandlerCapability(blockEntity).getTankCapacity(0), true, 18, 18);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        int fluidX = this.leftPos + (4 * 18) + 7;
        int fluidY = this.topPos + 54 - 19;
        if (x > fluidX && x < fluidX + 18
                && y > fluidY && y < fluidY + 18) {
            ContainerBlockEntity blockEntity = this.getMenu().getBlockEntity();
            guiGraphics.renderTooltip(Minecraft.getInstance().font, fluidTankRenderer.getTooltip(blockEntity.getFluidHandler().getFluidInTank(0)), Optional.empty(), x, y);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, delta, mouseX, mouseY);
        int fluidX = this.leftPos + (4 * 18) + 7;
        int fluidY = this.topPos + 54 - 19;
        ContainerBlockEntity blockEntity = this.getMenu().getBlockEntity();
        fluidTankRenderer.render(guiGraphics.pose(), fluidX, fluidY, CapabilityUtils.fluidHandlerCapability(blockEntity).getFluidInTank(0));
        renderLitProgress(guiGraphics);
    }

    protected void renderLitProgress(GuiGraphics pGuiGraphics) {
        int i = this.leftPos;
        int j = this.topPos;
        if (this.menu.getBlockEntity().isActive()) {
            float burnTime = ((float) this.menu.getBlockEntity().getBurnProgress() / this.menu.getBlockEntity().getFluidHandler().getTankCapacity(0));
            int j1 = Mth.ceil(burnTime * 13F);
            pGuiGraphics.blitSprite(LIT_PROGRESS_SPRITE, 14, 14, 0, 14 - j1, i + 80, j + 20 + 14 - j1 - 1, 14, j1);
        }
    }

    @Override
    public @NotNull ResourceLocation getBackgroundTexture() {
        return ResourceLocation.fromNamespaceAndPath(BuildcraftLegacy.MODID, "textures/gui/combustion_engine.png");
    }
}
