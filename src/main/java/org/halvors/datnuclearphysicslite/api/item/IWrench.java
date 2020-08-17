package org.halvors.datnuclearphysicslite.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IWrench {
    boolean canUseWrench(ItemStack itemStack, EntityPlayer player, BlockPos pos);
}
