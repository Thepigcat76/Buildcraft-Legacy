package com.thepigcat.buildcraft.content.menus;

import com.thepigcat.buildcraft.api.menus.BCAbstractContainerMenu;
import com.thepigcat.buildcraft.content.blockentities.StirlingEngineBE;
import com.thepigcat.buildcraft.registries.BCMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class StirlingEngineMenu extends BCAbstractContainerMenu<StirlingEngineBE> {
    public StirlingEngineMenu(int containerId, @NotNull Inventory inv, @NotNull StirlingEngineBE blockEntity) {
        super(BCMenuTypes.STIRLING_ENGINE.get(), containerId, inv, blockEntity);

        addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 8 + 4 * 18, 54 - 18));
        addPlayerHotbar(inv);
        addPlayerInventory(inv);
    }

    public StirlingEngineMenu(int containerId, @NotNull Inventory inv, @NotNull RegistryFriendlyByteBuf friendlyByteBuf) {
        this(containerId, inv, (StirlingEngineBE) inv.player.level().getBlockEntity(friendlyByteBuf.readBlockPos()));
    }

    @Override
    protected int getMergeableSlotCount() {
        return 1;
    }
}
