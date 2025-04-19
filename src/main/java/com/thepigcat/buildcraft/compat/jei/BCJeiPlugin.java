package com.thepigcat.buildcraft.compat.jei;

import com.portingdeadmods.portingdeadlibs.api.client.screens.PDLAbstractContainerScreen;
import com.thepigcat.buildcraft.BuildcraftLegacy;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public final class BCJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = BuildcraftLegacy.rl("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(PDLAbstractContainerScreen.class, new WidgetBounds());
    }
}
