package org.halvors.datnuclearphysicslite.api.fluid;

import net.minecraftforge.fluids.FluidStack;

public interface IBoilHandler {
    int receiveGas(FluidStack fluidStack, boolean doTransfer);
}
