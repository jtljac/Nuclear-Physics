package org.halvors.nuclearphysics.common.tile.machine;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.halvors.nuclearphysics.common.ConfigurationManager.General;
import org.halvors.nuclearphysics.common.NuclearPhysics;
import org.halvors.nuclearphysics.common.block.states.BlockStateMachine.EnumMachine;
import org.halvors.nuclearphysics.common.capabilities.energy.EnergyStorage;
import org.halvors.nuclearphysics.common.capabilities.fluid.GasTank;
import org.halvors.nuclearphysics.common.capabilities.fluid.LiquidTank;
import org.halvors.nuclearphysics.common.init.ModFluids;
import org.halvors.nuclearphysics.common.network.packet.PacketTileEntity;
import org.halvors.nuclearphysics.common.tile.TileMachine;
import org.halvors.nuclearphysics.common.utility.EnergyUtility;
import org.halvors.nuclearphysics.common.utility.FluidUtility;
import org.halvors.nuclearphysics.common.utility.InventoryUtility;
import org.halvors.nuclearphysics.common.utility.OreDictionaryHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileNuclearBoiler extends TileMachine implements IFluidHandler {
    // Tank tags
    private static final String NBT_TANK_INPUT = "tankInput";
    private static final String NBT_TANK_OUTPUT = "tankInOutput";

    // Inventory Tags
    private static final String NBT_SLOT_INPUT = "slotInput";
    private static final String NBT_SLOT_ENERGY = "slotEnergy";
    private static final String NBT_SLOT_FLUIDS = "slotFluids";

    // Tank stuff
    protected LiquidTank tankInput;
    protected LiquidTank tankOutput;

    // Inventory Stuff
    protected IItemHandlerModifiable inventoryInput;
    protected IItemHandlerModifiable inventoryEnergy;
    protected IItemHandlerModifiable inventoryFluids;

    // Animation
    public float rotation = 0;

    private static final int ENERGY_PER_TICK = 20000;
    public static final int TICKS_REQUIRED = 15 * 20;

    public TileNuclearBoiler() {
        this(EnumMachine.NUCLEAR_BOILER);
    }

    public TileNuclearBoiler(final EnumMachine type) {
        super(type);

        energyStorage = new EnergyStorage(ENERGY_PER_TICK * 2);

        setupInventories();

        tankInput = new LiquidTank(Fluid.BUCKET_VOLUME * 5) {
            @Override
            public int fill(final FluidStack resource, final boolean doFill) {
                if (resource.isFluidEqual(ModFluids.fluidStackWater)) {
                    return super.fill(resource, doFill);
                }

                return 0;
            }
        };

        tankOutput = new GasTank(Fluid.BUCKET_VOLUME * 5) {
            @Override
            public boolean canFill() {
                return false;
            }
        };
    }

    private void setupInventories() {
        createInput();
        createEnergy();
        createFluids();
    }

    private void createInput() {
        inventoryInput = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(final int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }

            @Override
            public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
                if (!(OreDictionaryHelper.isUraniumOre(stack) || OreDictionaryHelper.isYellowCake(stack))) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
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

    private void createFluids() {
        inventoryFluids = new ItemStackHandler(4) {
            @Override
            protected void onContentsChanged(final int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }

            private boolean isItemValidForSlot(final int slot, final ItemStack itemStack) {
                switch (slot) {
                    case 0: // Input tank drain slot.
                        return FluidUtility.isEmptyContainer(itemStack) || FluidUtility.isFilledContainer(itemStack, FluidRegistry.WATER);

                    case 2: // Output tank drain slot.
                        return FluidUtility.isEmptyContainer(itemStack);
                }

                return false;
            }

            @Override
            public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
                if (!isItemValidForSlot(slot, stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    public FluidTank getInputTank() {
        return tankInput;
    }

    public FluidTank getOutputTank() {
        return tankOutput;
    }

    public IItemHandlerModifiable getInventoryInput() {
        return inventoryInput;
    }

    public IItemHandlerModifiable getInventoryEnergy() {
        return inventoryEnergy;
    }

    public IItemHandlerModifiable getInventoryFluids() {
        return inventoryFluids;
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag) {
        super.readFromNBT(tag);

        CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(tankInput, null, tag.getTag(NBT_TANK_INPUT));
        CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(tankOutput, null, tag.getTag(NBT_TANK_OUTPUT));

        InventoryUtility.readFromNBT(tag, inventoryInput);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventoryInput, null, tag.getTag(NBT_SLOT_INPUT));

        InventoryUtility.readFromNBT(tag, inventoryEnergy);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventoryEnergy, null, tag.getTag(NBT_SLOT_ENERGY));

        InventoryUtility.readFromNBT(tag, inventoryFluids);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventoryFluids, null, tag.getTag(NBT_SLOT_FLUIDS));
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(final NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setTag(NBT_TANK_INPUT, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(tankInput, null));
        tag.setTag(NBT_TANK_OUTPUT, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(tankOutput, null));

        tag.setTag(NBT_SLOT_INPUT, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventoryInput, null));
        tag.setTag(NBT_SLOT_ENERGY, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventoryEnergy, null));
        tag.setTag(NBT_SLOT_FLUIDS, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventoryFluids, null));

        return tag;
    }

    @Override
    public void handlePacketData(final ByteBuf dataStream) {
        super.handlePacketData(dataStream);

        if (world.isRemote) {
            tankInput.handlePacketData(dataStream);
            tankOutput.handlePacketData(dataStream);
        }
    }

    @Override
    public List<Object> getPacketData(final List<Object> objects) {
        super.getPacketData(objects);

        tankInput.getPacketData(objects);
        tankOutput.getPacketData(objects);

        return objects;
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
            return (T) this;
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) inventoryInput;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        super.update();

        if (operatingTicks > 0) {
            rotation += 0.1;
        } else {
            rotation = 0;
        }

        if (!world.isRemote) {
            if (world.getWorldTime() % 20 == 0) {
                FluidUtility.transferFluidToNeighbors(world, pos, tankOutput);
            }

            if (getInputTank() != null) {
                FluidUtility.fillOrDrainTank(0, 1, inventoryFluids, getInputTank());
                FluidUtility.fillOrDrainTank(2, 3, inventoryFluids, getOutputTank());
            }

            EnergyUtility.discharge(inventoryEnergy.getStackInSlot(0), this);

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

    // Check all conditions and see if we can start processing
    public boolean canProcess() {
        final FluidStack inputFluidStack = tankInput.getFluid();

        if (inputFluidStack != null && inputFluidStack.amount >= Fluid.BUCKET_VOLUME) {
            final ItemStack itemStack = inventoryInput.getStackInSlot(0);

            if (!itemStack.isEmpty() && (OreDictionaryHelper.isUraniumOre(itemStack) || OreDictionaryHelper.isYellowCake(itemStack))) {
                return tankOutput.getFluidAmount() < tankOutput.getCapacity();
            }
        }

        return false;
    }

    // Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack.
    public void process() {
        if (canProcess()) {
            tankInput.drainInternal(Fluid.BUCKET_VOLUME, true);

            final FluidStack fluidStack = new FluidStack(ModFluids.uraniumHexafluoride, General.uraniumHexaflourideRatio * 2);
            tankOutput.fillInternal(fluidStack, true);

            InventoryUtility.decrStackSize(inventoryInput, 0);
        }
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[] { new FluidTankPropertiesWrapper(tankInput), new FluidTankPropertiesWrapper(tankOutput) };
    }

    @Override
    public int fill(final FluidStack resource, final boolean doFill) {
        return tankInput.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(final FluidStack resource, final boolean doDrain) {
        return tankOutput.drain(resource, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(final int maxDrain, final boolean doDrain) {
        return tankOutput.drain(maxDrain, doDrain);
    }
}
