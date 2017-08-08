package org.halvors.quantum.common.tile.reactor;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.halvors.quantum.common.fluid.tank.GasTank;
import org.halvors.quantum.common.init.QuantumFluids;

import javax.annotation.Nonnull;

public class TileGasFunnel extends TileEntity implements ITickable {
    private final GasTank tank = new GasTank(QuantumFluids.fluidStackSteam.copy(), Fluid.BUCKET_VOLUME * 16) {
        @Override
        public boolean canFill() {
            return false;
        }
    };

    public TileGasFunnel() {

    }

    @Override
    public void update() {
        if (tank.getFluidAmount() > 0) {
            TileEntity tile = world.getTileEntity(pos.up());

            if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                IFluidHandler fluidHandler = getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);
                FluidStack fluidStack = tank.drain(tank.getCapacity(), false);

                if (fluidStack != null) {
                    tank.drain(fluidHandler.fill(fluidStack, true), true);
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(tank, null, tag.getTag("tank"));
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setTag("tank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(tank, null));

        return tag;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nonnull EnumFacing facing) {
        return (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == EnumFacing.DOWN || facing == EnumFacing.UP)) || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nonnull EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == EnumFacing.DOWN || facing == EnumFacing.UP)) {
            return (T) tank;
        }

        return super.getCapability(capability, facing);
    }
}

