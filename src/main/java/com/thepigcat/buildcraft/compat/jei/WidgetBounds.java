package com.thepigcat.buildcraft.compat.jei;

import com.portingdeadmods.portingdeadlibs.api.client.screens.PDLAbstractContainerScreen;
import com.thepigcat.buildcraft.client.screens.StirlingEngineScreen;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

import java.util.List;

public class WidgetBounds implements IGuiContainerHandler<PDLAbstractContainerScreen<?>> {
    @Override
    public List<Rect2i> getGuiExtraAreas(PDLAbstractContainerScreen<?> containerScreen) {
        if (containerScreen instanceof StirlingEngineScreen screen) {
            return screen.getBounds();
        }
        return IGuiContainerHandler.super.getGuiExtraAreas(containerScreen);
    }
}
