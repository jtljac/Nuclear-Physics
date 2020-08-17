package org.halvors.datnuclearphysicslite.common.utility;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryHelper {
    // Items
    public static boolean isEmptyCell(final ItemStack itemStack) {
        return hasOreNames(itemStack, "cellEmpty");
    }

    public static boolean isDarkmatterCell(final ItemStack itemStack) {
        return hasOreNames(itemStack, "cellDarkmatter");
    }

    /**
     * Compare to Ore Dict
     */
    public static boolean hasOreNames(final ItemStack itemStack, final String... names) {
        if (!itemStack.isEmpty() && names != null && names.length > 0) {
            for (int id : OreDictionary.getOreIDs(itemStack)) {
                final String name = OreDictionary.getOreName(id);

                for (String compareName : names) {
                    if (name.equals(compareName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}


