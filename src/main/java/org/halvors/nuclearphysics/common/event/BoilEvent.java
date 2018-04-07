package org.halvors.nuclearphysics.common.event;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import org.halvors.nuclearphysics.common.init.ModFluids;

public class BoilEvent extends WorldEventBase {
    private final FluidStack fluidStack;
    private final int maxSpread;
    private final boolean reactor;

    /**
     * @param world - The world object
     * @param pos - The position in which the boiling happens.
     * @param fluidStack - The fluid being boiled.
     * @param maxSpread - The maximum distance the evaporated fluid can spread.
     * @param reactor - Determined if heat source if from power generation or a weapon.
     */
    public BoilEvent(IBlockAccess world, BlockPos pos, FluidStack fluidStack, int maxSpread, boolean reactor) {
        super(world, pos);

        this.fluidStack = fluidStack;
        this.maxSpread = maxSpread;
        this.reactor = reactor;
    }

    public BoilEvent(IBlockAccess world, BlockPos pos, FluidStack fluidStack, int maxSpread) {
        this(world, pos, fluidStack, maxSpread, false);
    }

    public FluidStack getFluid() {
        return fluidStack;
    }

    public int getMaxSpread() {
        return maxSpread;
    }

    public boolean isReactor() {
        return reactor;
    }

    // Fluid spread causes loss. Gets the remaining amount of steam left after spreading.
    public FluidStack getGas(int spread) {
        float spreadPercentage = (float) spread / (float) maxSpread;
        FluidStack gasStack = new FluidStack(ModFluids.steam, fluidStack.amount);
        gasStack.amount *= spreadPercentage;

        return gasStack;
    }
}