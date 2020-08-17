package org.halvors.datnuclearphysicslite.common.effect.potion;

import net.minecraft.potion.Potion;
import org.halvors.datnuclearphysicslite.common.Reference;

public abstract class PotionBase extends Potion {
    public PotionBase(final boolean isBadEffect, final int color, final String name) {
        super(isBadEffect, color);

        setPotionName(this, name);
    }

    /**
     * Set the registry name of {@code potion} to {@code potionName} and the unlocalised name to the full registry name.
     *
     * @param potion The potion
     * @param potionName The potion's name
     */
    public static void setPotionName(final Potion potion, final String potionName) {
        potion.setRegistryName(Reference.ID, potionName);
        potion.setPotionName("effect." + potionName);
    }
}
