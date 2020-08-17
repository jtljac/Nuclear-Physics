package org.halvors.datnuclearphysicslite.api.effect.poison;

import net.minecraft.entity.EntityLivingBase;

public interface IPoison {
    boolean isEntityProtected(EntityLivingBase entity, int amplifier);

    void poisonEntity(EntityLivingBase entity, int amplifier);
}
