package org.halvors.nuclearphysics.common.tile.machine;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.halvors.nuclearphysics.common.ConfigurationManager.General;
import org.halvors.nuclearphysics.common.NuclearPhysics;
import org.halvors.nuclearphysics.common.block.states.BlockStateMachine.EnumMachine;
import org.halvors.nuclearphysics.common.capabilities.energy.EnergyStorage;
import org.halvors.nuclearphysics.common.capabilities.fluid.GasTank;
import org.halvors.nuclearphysics.common.init.ModFluids;
import org.halvors.nuclearphysics.common.init.ModItems;
import org.halvors.nuclearphysics.common.item.reactor.fission.ItemUranium.EnumUranium;
import org.halvors.nuclearphysics.common.network.packet.PacketTileEntity;
import org.halvors.nuclearphysics.common.tile.TileMachine;
import org.halvors.nuclearphysics.common.utility.EnergyUtility;
import org.halvors.nuclearphysics.common.utility.FluidUtility;
import org.halvors.nuclearphysics.common.utility.InventoryUtility;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileGasCentrifuge extends TileMachine {
    // Tank
    private static final String NBT_TANK = "tank";
    private static final String NBT_SLOT_OUTPUT = "slotOutput";
    private static final String NBT_SLOT_Energy = "slotEnergy";
    private static final String NBT_SLOT_FLUID = "slotFluid";

    // Slots
    protected IItemHandlerModifiable inventoryOutput;
    protected IItemHandlerModifiable inventoryEnergy;
    protected IItemHandlerModifiable inventoryFluid;

    private static final int ENERGY_PER_TICK = 20000;
    public static final int TICKS_REQUIRED = 60 * 20;

    public float rotation = 0;

    private final GasTank tank = new GasTank(Fluid.BUCKET_VOLUME * 5) {
        @Override
        public int fill(final FluidStack resource, final boolean doFill) {
            if (resource.isFluidEqual(ModFluids.fluidStackUraniumHexaflouride)) {
                return super.fill(resource, doFill);
            }

            return 0;
        }

        @Override
        public boolean canDrain() {
            return false;
        }
    };

    public TileGasCentrifuge() {
        this(EnumMachine.GAS_CENTRIFUGE);
    }

    public TileGasCentrifuge(final EnumMachine type) {
        super(type);

        energyStorage = new EnergyStorage(ENERGY_PER_TICK * 2);
        setupInventories();
    }

    private void setupInventories() {
        createOutput();
        createEnergy();
        createFluid();
    }

    private void createOutput() {
        inventoryOutput = new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(final int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }

            @Override
            public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
                return stack;
            }
        };
    }

    private void createEnergy() {
        inventoryEnergy = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(final int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }

            @Override
            public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
                if (!EnergyUtility.canBeDischarged(stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private void createFluid() {
        inventoryFluid = new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(final int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }
            @Override
            public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
                if (!(slot == 0 && FluidUtility.isFilledContainer(stack, ModFluids.uraniumHexafluoride))) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    public IItemHandlerModifiable getInventoryOutput() {
        return inventoryOutput;
    }

    public IItemHandlerModifiable getInventoryEnergy() {
        return inventoryEnergy;
    }

    public IItemHandlerModifiable getInventoryFluid() {
        return inventoryFluid;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag) {
        super.readFromNBT(tag);

        CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(tank, null, tag.getTag(NBT_TANK));

        InventoryUtility.readFromNBT(tag, inventoryOutput);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventoryOutput, null, tag.getTag(NBT_SLOT_OUTPUT));
        InventoryUtility.readFromNBT(tag, inventoryEnergy);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventoryEnergy, null, tag.getTag(NBT_SLOT_Energy));
        InventoryUtility.readFromNBT(tag, inventoryFluid);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventoryFluid, null, tag.getTag(NBT_SLOT_FLUID));
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setTag(NBT_TANK, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(tank, null));

        tag.setTag(NBT_SLOT_OUTPUT, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventoryOutput, null));
        tag.setTag(NBT_SLOT_Energy, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventoryEnergy, null));
        tag.setTag(NBT_SLOT_FLUID, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventoryFluid, null));

        return tag;
    }

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) tank;
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) inventoryOutput;
        }

        return super.getCapability(capability, facing);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update() {
        super.update();

        if (operatingTicks > 0) {
            rotation += 0.45;
        } else {
            rotation = 0;
        }

        if (!world.isRemote) {
            EnergyUtility.discharge(inventoryEnergy.getStackInSlot(0), this);

            if (tank != null) {
                FluidUtility.fillOrDrainTank(0, 1, inventoryFluid, tank);
            }

            if (canFunction() && canProcess() && energyStorage.extractEnergy(ENERGY_PER_TICK, true) >= ENERGY_PER_TICK) {
                if (operatingTicks < TICKS_REQUIRED) {
                    operatingTicks++;
                } else {
                    process();
                    reset();
                }

                energyUsed = energyStorage.extractEnergy(ENERGY_PER_TICK, false);
            }

            if (!canProcess()) {
                reset();
            }

            if (world.getWorldTime() % 10 == 0) {
                NuclearPhysics.getPacketHandler().sendToReceivers(new PacketTileEntity(this), this);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handlePacketData(final ByteBuf dataStream) {
        super.handlePacketData(dataStream);

        if (world.isRemote) {
            tank.handlePacketData(dataStream);
        }
    }

    @Override
    public List<Object> getPacketData(final List<Object> objects) {
        super.getPacketData(objects);

        tank.getPacketData(objects);

        return objects;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public IFluidTank getTank() {
        return tank;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.UP ? new int[] { 0, 1 } : new int[] { 2, 3 };
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, EnumFacing side) {
        return slot == 1 && isItemValidForSlot(slot, itemStack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, EnumFacing side) {
        return slot == 2 || slot == 3;
    }
    */

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean canProcess() {
        final FluidStack fluidStack = tank.getFluid();

        return fluidStack != null && fluidStack.amount >= General.uraniumHexaflourideRatio && inventoryOutput.getStackInSlot(0).getCount() < inventoryOutput.getStackInSlot(0).getMaxStackSize() && inventoryOutput.getStackInSlot(1).getCount() < inventoryOutput.getStackInSlot(1).getMaxStackSize();
    }

    public void process() {
        if (canProcess()) {
            tank.drainInternal(General.uraniumHexaflourideRatio, true);

            if (world.rand.nextFloat() > 0.6) {
                insertItem(inventoryOutput, 0, new ItemStack(ModItems.itemUranium, 1, EnumUranium.URANIUM_235.ordinal()));
            } else {
                insertItem(inventoryOutput, 1, new ItemStack(ModItems.itemUranium, 1, EnumUranium.URANIUM_238.ordinal()));
            }
        }
    }

    private void insertItem(IItemHandlerModifiable inventory, int slot, ItemStack item) {
        ItemStack theSlot = inventory.getStackInSlot(slot);
        if (theSlot.isEmpty()){
            inventory.setStackInSlot(slot, item);
        } else {
            theSlot.setCount(Math.min(theSlot.getCount() + item.getCount(), theSlot.getMaxStackSize()));
        }
    }
}
