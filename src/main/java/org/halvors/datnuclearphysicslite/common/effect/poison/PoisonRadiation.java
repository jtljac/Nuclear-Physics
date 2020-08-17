package org.halvors.datnuclearphysicslite.common.effect.poison;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import org.halvors.datnuclearphysicslite.api.effect.poison.EnumPoisonType;
import org.halvors.datnuclearphysicslite.common.ConfigurationManager.General;

import javax.annotation.Nonnull;

public class PoisonRadiation extends PoisonBase {
    public PoisonRadiation() {
        super(true, 78, 147, 49, EnumPoisonType.RADIATION);

        setIconIndex(6, 0);
    }

    @Override
    public void performEffect(@Nonnull final EntityLivingBase entity, final int amplifier) {
        final World world = entity.getEntityWorld();

        if (world.rand.nextFloat() > 0.9 - amplifier * 0.07) {
            entity.attackEntityFrom(damageSource, 1);

            if (entity instanceof EntityPlayer) {
                ((EntityPlayer) entity).addExhaustion(0.01F * (amplifier + 1));
            }
        }
    }

    @Override
    public boolean isReady(final int duration, final int amplifier) {
        return duration % 10 == 0;
    }

    public PoisonRadiation(boolean isBadEffect, int color, EnumPoisonType type) {
        super(isBadEffect, color, type);
    }

    @Override
    public void performPoisonEffect(final EntityLivingBase entity, final int amplifier) {
        if (General.enableRadiationRoisoning) {
            entity.addPotionEffect(new PotionEffect(this, 300 * (amplifier + 1), amplifier));
        }
    }
}