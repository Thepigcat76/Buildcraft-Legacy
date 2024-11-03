package com.thepigcat.buildcraft.registries;

import com.thepigcat.buildcraft.BuildcraftLegacy;
import com.thepigcat.buildcraft.content.menus.CombustionEngineMenu;
import com.thepigcat.buildcraft.content.menus.StirlingEngineMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class BCMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, BuildcraftLegacy.MODID);

    public static final Supplier<MenuType<StirlingEngineMenu>> STIRLING_ENGINE =
            registerMenuType("stirling_engine", StirlingEngineMenu::new);
    public static final Supplier<MenuType<CombustionEngineMenu>> COMBUSTION_ENGINE =
            registerMenuType("combustion_engine", CombustionEngineMenu::new);

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }
}
