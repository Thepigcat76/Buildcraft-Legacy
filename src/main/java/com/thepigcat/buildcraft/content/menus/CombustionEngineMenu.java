package com.thepigcat.buildcraft.content.menus;

import com.thepigcat.buildcraft.api.menus.BCAbstractContainerMenu;
import com.thepigcat.buildcraft.content.blockentities.CombustionEngineBE;
import com.thepigcat.buildcraft.content.blockentities.StirlingEngineBE;
import com.thepigcat.buildcraft.registries.BCMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CombustionEngineMenu extends BCAbstractContainerMenu<CombustionEngineBE> {
    public CombustionEngineMenu(int containerId, @NotNull Inventory inv, @NotNull CombustionEngineBE blockEntity) {
        super(BCMenuTypes.COMBUSTION_ENGINE.get(), containerId, inv, blockEntity);

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    public CombustionEngineMenu(int containerId, @NotNull Inventory inv, @NotNull RegistryFriendlyByteBuf friendlyByteBuf) {
        this(containerId, inv, (CombustionEngineBE) inv.player.level().getBlockEntity(friendlyByteBuf.readBlockPos()));
    }

    @Override
    protected int getMergeableSlotCount() {
        return 0;
    }
}