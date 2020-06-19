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
import org.halvors.nuclearphysics.common.capabilities.fluid.LiquidTank;
import org.halvors.nuclearphysics.common.init.ModFluids;
import org.halvors.nuclearphysics.common.init.ModItems;
import org.halvors.nuclearphysics.common.network.packet.PacketTileEntity;
import org.halvors.nuclearphysics.common.tile.TileMachine;
import org.halvors.nuclearphysics.common.utility.EnergyUtility;
import org.halvors.nuclearphysics.common.utility.FluidUtility;
import org.halvors.nuclearphysics.common.utility.InventoryUtility;
import org.halvors.nuclearphysics.common.utility.OreDictionaryHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileChemicalExtractor extends TileMachine implements IFluidHandler {
    private static final String NBT_TANK_INPUT = "tankInput";
    private static final String NBT_TANK_OUTPUT = "tankInOutput";

    private static final String NBT_SLOT_INPUT = "slotInputMatter";
    private static final String NBT_SLOT_OUTPUT = "slotInputCells";
    private static final String NBT_SLOT_ENERGY = "slotEnergy";
    private static final String NBT_SLOT_FLUIDS = "slotOutput";

    private static final int ENERGY_PER_TICK = 20000;
    private static final int EXTRACT_SPEED = 100;
    public static final int TICKS_REQUIRED = 14 * 20;

    // Animation
    public float rotation = 0;

    // Tanks
    private LiquidTank tankInput;
    private LiquidTank tankOutput;

    private IItemHandlerModifiable inventoryInput;
    private IItemHandlerModifiable inventoryOutput;
    private IItemHandlerModifiable inventoryEnergy;
    private IItemHandlerModifiable inventoryFluids;

    public TileChemicalExtractor() {
        this(EnumMachine.CHEMICAL_EXTRACTOR);
    }

    public TileChemicalExtractor(final EnumMachine type) {
        super(type);

        energyStorage = new EnergyStorage(ENERGY_PER_TICK * 2);

        createInventories();

        tankInput = new LiquidTank(Fluid.BUCKET_VOLUME * 10) {
            @Override
            public int fill(final FluidStack resource, final boolean doFill) {
                if (resource.isFluidEqual(ModFluids.fluidStackWater) || resource.isFluidEqual(ModFluids.fluidStackDeuterium)) {
                    return super.fill(resource, doFill);
                }

                return 0;
            }

            // We have to allow draining for containers to work.
            /*
            @Override
            public boolean canDrain() {
                return false;
            }
            */
        };

        tankOutput = new LiquidTank(Fluid.BUCKET_VOLUME * 10) {
            @Override
            public boolean canFill() {
                return false;
            }
        };
    }

    private void createInventories() {
        createInput();
        createOutput();
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
            public ItemStack insertItem(final int slot, @Nonnull final ItemStack itemStack, final boolean simulate) {
                if (!OreDictionaryHelper.isUraniumOre(itemStack)) {
                    return itemStack;
                }

                return super.insertItem(slot, itemStack, simulate);
            }
        };
    }

    private void createOutput() {
        inventoryOutput = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(final int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }

            @Override
            public ItemStack insertItem(final int slot, @Nonnull final ItemStack itemStack, final boolean simulate) {
                return itemStack;
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
            public ItemStack insertItem(final int slot, @Nonnull final ItemStack itemStack, final boolean simulate) {
                if (!EnergyUtility.canBeDischarged(itemStack)) {
                    return itemStack;
                }

                return super.insertItem(slot, itemStack, simulate);
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
                    case 0: // Input tank Fill/Drain slot.
                        return FluidUtility.isEmptyContainer(itemStack) || FluidUtility.isFilledContainer(itemStack, FluidRegistry.WATER) || FluidUtility.isFilledContainer(itemStack, ModFluids.deuterium);
                    case 2: // Output tank Drain slot.
                        return FluidUtility.isEmptyContainer(itemStack);
                }

                return false;
            }

            @Override
            public ItemStack insertItem(final int slot, @Nonnull final ItemStack itemStack, final boolean simulate) {
                if (!isItemValidForSlot(slot, itemStack)) {
                    return itemStack;
                }

                return super.insertItem(slot, itemStack, simulate);
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

    public IItemHandlerModifiable getInventoryOutput() {
        return inventoryOutput;
    }

    public IItemHandlerModifiable getInventoryEnergy() {
        return inventoryEnergy;
    }

    public IItemHandlerModifiable getInventoryFluids() {
        return inventoryFluids;
    }

    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing Facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) this;
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (Facing != null) {
                switch (absoluteFacingToRelative(Facing)) {
                    case UP:
                    case EAST:
                    case NORTH:
                        return (T) inventoryInput;
                    case DOWN:
                    case WEST:
                    case SOUTH:
                        return (T) inventoryOutput;
                }
            }
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag) {
        super.readFromNBT(tag);

        CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(tankInput, null, tag.getTag(NBT_TANK_INPUT));
        CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(tankOutput, null, tag.getTag(NBT_TANK_OUTPUT));

        InventoryUtility.readFromNBT(tag, inventoryInput);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventoryInput, null, tag.getTag(NBT_SLOT_INPUT));
        InventoryUtility.readFromNBT(tag, inventoryOutput);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventoryOutput, null, tag.getTag(NBT_SLOT_OUTPUT));
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
        tag.setTag(NBT_SLOT_OUTPUT, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventoryOutput, null));
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
    public void update() {
        super.update();

        if (operatingTicks > 0) {
            rotation += 0.2;
        } else {
            rotation = 0;
        }

        if (!world.isRemote) {
            if (world.getWorldTime() % 20 == 0) {
                FluidUtility.transferFluidToNeighbors(world, pos, tankOutput);
            }

            if (getInputTank() != null) {
                FluidUtility.fillOrDrainTank(0, 1, inventoryFluids, getInputTank());
            }

            if (getOutputTank() != null) {
                FluidUtility.fillOrDrainTank(2, 3, inventoryFluids, getOutputTank());
            }

            EnergyUtility.discharge(inventoryEnergy.getStackInSlot(0), this);

            if (canFunction() && canProcess() && energyStorage.extractEnergy(ENERGY_PER_TICK, true) >= ENERGY_PER_TICK) {
                if (operatingTicks < TICKS_REQUIRED) {
                    operatingTicks++;
                } else {
                    if (!refineUranium()) {
                        if (!extractTritium()) {
                            extractDeuterium();
                        }
                    }

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


    public boolean canProcess() {
        final FluidStack inputFluidStack = tankInput.getFluid();

        if (inputFluidStack != null) {
            if (inputFluidStack.isFluidEqual(ModFluids.fluidStackWater) && inputFluidStack.amount >= Fluid.BUCKET_VOLUME && OreDictionaryHelper.isUraniumOre(inventoryInput.getStackInSlot(0)) && inventoryOutput.getStackInSlot(0).getCount() + 3 <= inventoryOutput.getStackInSlot(0).getMaxStackSize()) {
                return true;
            }

            if (tankOutput.getFluidAmount() < tankOutput.getCapacity()) {
                final FluidStack outputFluidStack = tankOutput.getFluid();

                if (inputFluidStack.isFluidEqual(ModFluids.fluidStackDeuterium) && inputFluidStack.amount >= General.deutermiumPerTritium * EXTRACT_SPEED) {
                    if (outputFluidStack == null || outputFluidStack.getFluid() == ModFluids.tritium) {
                        return true;
                    }
                }

                if (inputFluidStack.isFluidEqual(ModFluids.fluidStackWater) && inputFluidStack.amount >= General.waterPerDeutermium * EXTRACT_SPEED) {
                    return outputFluidStack == null || outputFluidStack.getFluid() == ModFluids.deuterium;
                }
            }
        }

        return false;
    }

    /*
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack.
     */
    public boolean refineUranium() {
        if (canProcess()) {
            if (OreDictionaryHelper.isUraniumOre(inventoryInput.getStackInSlot(0))) {
                tankInput.drainInternal(Fluid.BUCKET_VOLUME, true);
                InventoryUtility.decrStackSize(inventoryInput, 0);
                ItemStack output = inventoryOutput.getStackInSlot(0);
                if (output.isEmpty()) inventoryOutput.setStackInSlot(0, new ItemStack(ModItems.itemYellowCake, 3));
                else if (output.getCount() + 3 <= output.getMaxStackSize()) output.setCount(output.getCount() + 3);
                else return false;

                return true;
            }
        }

        return false;
    }

    public boolean extractDeuterium() {
        if (canProcess()) {
            final int waterUsage = General.waterPerDeutermium;
            final FluidStack fluidStack = tankInput.drainInternal(waterUsage * EXTRACT_SPEED, false);

            if (fluidStack != null && fluidStack.amount >= 1 && fluidStack.isFluidEqual(ModFluids.fluidStackWater)) {
                if (tankOutput.fillInternal(new FluidStack(ModFluids.deuterium, EXTRACT_SPEED), true) >= EXTRACT_SPEED) {
                    tankInput.drainInternal(waterUsage * EXTRACT_SPEED, true);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean extractTritium() {
        if (canProcess()) {
            final int deuteriumUsage = General.deutermiumPerTritium;
            final FluidStack fluidStack = tankInput.drainInternal(deuteriumUsage * EXTRACT_SPEED, false);

            if (fluidStack != null && fluidStack.amount >= 1 && fluidStack.isFluidEqual(ModFluids.fluidStackDeuterium)) {
                if (tankOutput.fillInternal(new FluidStack(ModFluids.tritium, EXTRACT_SPEED), true) >= EXTRACT_SPEED) {
                    tankInput.drainInternal(deuteriumUsage * EXTRACT_SPEED, true);

                    return true;
                }
            }
        }

        return false;
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
