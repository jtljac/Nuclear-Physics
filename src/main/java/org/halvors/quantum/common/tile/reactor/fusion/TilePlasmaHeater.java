package org.halvors.quantum.common.tile.reactor.fusion;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import org.halvors.quantum.Quantum;
import org.halvors.quantum.common.base.tile.ITileNetworkable;
import org.halvors.quantum.common.network.NetworkHandler;
import org.halvors.quantum.common.network.packet.PacketTileEntity;
import org.halvors.quantum.common.tile.TileElectricInventory;
import org.halvors.quantum.lib.prefab.tile.TileElectrical;

import java.util.List;

public class TilePlasmaHeater extends TileElectricInventory implements ITileNetworkable, IFluidHandler, IEnergyReceiver {
    public static long power = 10000000000L;
    public static int plasmaHeatAmount = 100; //@Config

    public final FluidTank tankInputDeuterium = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 10);
    public final FluidTank tankInputTritium = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 10);
    public final FluidTank tankOutput = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 10);

    public float rotation = 0;

    public TilePlasmaHeater() {
        energyStorage = new EnergyStorage((int) power, (int) power / 20);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        rotation += energyStorage.getEnergyStored() / 10000F;

        if (!worldObj.isRemote) {
            if (energyStorage.extractEnergy(energyStorage.getMaxExtract(), true) >= energyStorage.getMaxExtract()) {
                if (tankInputDeuterium.getFluidAmount() >= plasmaHeatAmount && tankInputTritium.getFluidAmount() >= plasmaHeatAmount) {
                    tankInputDeuterium.drain(plasmaHeatAmount, true);
                    tankInputTritium.drain(plasmaHeatAmount, true);
                    tankOutput.fill(new FluidStack(Quantum.fluidPlasma, plasmaHeatAmount), true);

                    energyStorage.extractEnergy(energyStorage.getMaxExtract(), false);
                }
            }
        }

        if (worldObj.getWorldTime() % 80 == 0) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

            NetworkHandler.sendToReceivers(new PacketTileEntity(this), this);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        NBTTagCompound deuterium = tagCompound.getCompoundTag("tankInputDeuterium");
        tankInputDeuterium.setFluid(FluidStack.loadFluidStackFromNBT(deuterium));

        NBTTagCompound tritium = tagCompound.getCompoundTag("tankInputTritium");
        tankInputTritium.setFluid(FluidStack.loadFluidStackFromNBT(tritium));

        NBTTagCompound output = tagCompound.getCompoundTag("tankOutput");
        tankOutput.setFluid(FluidStack.loadFluidStackFromNBT(output));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        if (tankInputDeuterium.getFluid() != null) {
            NBTTagCompound compound = new NBTTagCompound();
            tankInputDeuterium.getFluid().writeToNBT(compound);
            tagCompound.setTag("tankInputDeuterium", compound);
        }

        if (tankInputTritium.getFluid() != null) {
            NBTTagCompound compound = new NBTTagCompound();
            tankInputTritium.getFluid().writeToNBT(compound);
            tagCompound.setTag("tankInputTritium", compound);
        }

        if (tankOutput.getFluid() != null) {
            NBTTagCompound compound = new NBTTagCompound();
            tankOutput.getFluid().writeToNBT(compound);
            tagCompound.setTag("tankOutput", compound);
        }
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) throws Exception {
        if (worldObj.isRemote) {
            if (dataStream.readBoolean()) {
                tankInputDeuterium.setFluid(FluidStack.loadFluidStackFromNBT(NetworkHandler.readNBTTag(dataStream)));
            }

            if (dataStream.readBoolean()) {
                tankInputTritium.setFluid(FluidStack.loadFluidStackFromNBT(NetworkHandler.readNBTTag(dataStream)));
            }

            if (dataStream.readBoolean()) {
                tankOutput.setFluid(FluidStack.loadFluidStackFromNBT(NetworkHandler.readNBTTag(dataStream)));
            }
        }
    }

    @Override
    public List<Object> getPacketData(List<Object> objects) {
        if (tankInputDeuterium.getFluid() != null) {
            objects.add(true);

            NBTTagCompound compoundDeuterium = new NBTTagCompound();
            tankInputDeuterium.getFluid().writeToNBT(compoundDeuterium);
            objects.add(compoundDeuterium);
        } else {
            objects.add(false);
        }

        if (tankInputTritium.getFluid() != null) {
            objects.add(true);

            NBTTagCompound compoundTritium = new NBTTagCompound();
            tankInputTritium.getFluid().writeToNBT(compoundTritium);
            objects.add(compoundTritium);
        } else {
            objects.add(false);
        }

        if (tankOutput.getFluid() != null) {
            objects.add(true);

            NBTTagCompound compoundOutput = new NBTTagCompound();
            tankOutput.getFluid().writeToNBT(compoundOutput);
            objects.add(compoundOutput);
        } else {
            objects.add(false);
        }

        return objects;
    }

    /*
    @Override
    public void read(ByteBuf data, EntityPlayer player, PacketType) {
        try {
            readFromNBT(PacketHandler.readNBTTagCompound(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);

        //return ResonantInduction.PACKET_TILE.getPacket(this, nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tagCompound);
    }

    @Override
    public float addInformation(HashMap<String, Integer> map, EntityPlayer player) {
        if (energy != null) {
            map.put(LanguageUtility.localize("tooltip.energy") + ": " + UnitDisplay.getDisplay(energy.getEnergy(), Unit.JOULES), 0xFFFFFF);
        }

        if (tankInputDeuterium.getFluidAmount() > 0) {
            map.put(LanguageUtility.localize("fluid.deuterium") + ": " + tankInputDeuterium.getFluidAmount() + " L", 0xFFFFFF);
        }

        if (tankInputTritium.getFluidAmount() > 0) {
            map.put(LanguageUtility.localize("fluid.tritium") + ": " + tankInputTritium.getFluidAmount() + " L", 0xFFFFFF);
        }

        if (tankOutput.getFluidAmount() > 0) {
            map.put(LanguageUtility.localize("fluid.plasma") + ": " + tankOutput.getFluidAmount() + " L", 0xFFFFFF);
        }

        return 1.5f;
    }
    */

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource.isFluidEqual(Quantum.fluidStackDeuterium)) {
            return tankInputDeuterium.fill(resource, doFill);
        }

        if (resource.isFluidEqual(Quantum.fluidStackTritium)) {
            return tankInputTritium.fill(resource, doFill);
        }

        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tankOutput.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid.getID() == Quantum.fluidDeuterium.getID() || fluid.getID() == Quantum.fluidTritium.getID();
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return fluid.getID() == Quantum.fluidPlasma.getID();
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { tankInputDeuterium.getInfo(), tankInputTritium.getInfo(), tankOutput.getInfo() };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        if (tankInputDeuterium.getFluidAmount() > 0 && tankInputTritium.getFluidAmount() > 0) {
            return super.receiveEnergy(from, maxReceive, simulate);
        }

        return 0;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }
}
