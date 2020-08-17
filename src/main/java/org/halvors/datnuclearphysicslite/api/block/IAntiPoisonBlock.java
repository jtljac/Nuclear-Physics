package org.halvors.datnuclearphysicslite.api.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.halvors.datnuclearphysicslite.api.effect.poison.EnumPoisonType;

public interface IAntiPoisonBlock {
    boolean isPoisonProtective(IBlockAccess world, BlockPos pos, EnumPoisonType type);
}