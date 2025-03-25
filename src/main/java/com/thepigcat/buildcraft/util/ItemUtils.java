package com.thepigcat.buildcraft.util;

import com.thepigcat.buildcraft.api.capabilties.JumboItemHandler.BigStack;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ItemUtils {
    public static int hashStackList(List<BigStack> list) {
        int i = 0;

        for(BigStack bigStack : list) {
            i = i * 31 + ItemStack.hashItemAndComponents(bigStack.getSlotStack());
        }

        return i;
    }

    public static boolean listMatches(List<BigStack> list, List<BigStack> other) {
        if (list.size() != other.size()) {
            return false;
        } else {
            for(int i = 0; i < list.size(); ++i) {
                if (!ItemStack.matches(list.get(i).getSlotStack(), other.get(i).getSlotStack())) {
                    return false;
                }
            }

            return true;
        }
    }

}
