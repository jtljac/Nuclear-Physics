package org.halvors.nuclearphysics.api.recipe;


import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QuantumAssemblerBlacklist {
    private static final List<ItemStack> RECIPES = new ArrayList<>();

    public static boolean isBlacklisted(ItemStack itemStack) {
        for (ItemStack output : RECIPES) {
            if (output.isItemEqual(itemStack)) {
                return true;
            }
        }

        return false;
    }

    public static void addItemToBlacklist(ItemStack itemStack) {
        if (!itemStack.isEmpty()) {
            if (itemStack.isStackable()) {
                RECIPES.add(itemStack);
            }
        }
    }
}
