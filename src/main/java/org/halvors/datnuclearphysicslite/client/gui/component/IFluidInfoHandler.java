package org.halvors.datnuclearphysicslite.client.gui.component;

import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IFluidInfoHandler {
    IFluidTank getTank();
}