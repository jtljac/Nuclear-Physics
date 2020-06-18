package org.halvors.nuclearphysics.common.tile.machine;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.halvors.nuclearphysics.api.recipe.QuantumAssemblerRecipes;
import org.halvors.nuclearphysics.common.NuclearPhysics;
import org.halvors.nuclearphysics.common.block.states.BlockStateMachine.EnumMachine;
import org.halvors.nuclearphysics.common.capabilities.energy.EnergyStorage;
import org.halvors.nuclearphysics.common.init.ModSoundEvents;
import org.halvors.nuclearphysics.common.network.packet.PacketTileEntity;
import org.halvors.nuclearphysics.common.tile.TileInventoryMachine;
import org.halvors.nuclearphysics.common.tile.TileMachine;
import org.halvors.nuclearphysics.common.utility.InventoryUtility;
import org.halvors.nuclearphysics.common.utility.OreDictionaryHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class TileQuantumAssembler extends TileMachine {
    private static final int ENERGY_PER_TICK = 2048000;
    public static final int TICKS_REQUIRED = 180 * 20;

    // Inventory
    private static final String NBT_SLOTS = "slots";
    protected IItemHandlerModifiable inventory;

    // Used for rendering.
    private EntityItem entityItem = null;
    private float rotationYaw1, rotationYaw2, rotationYaw3;

    public TileQuantumAssembler() {
        this(EnumMachine.QUANTUM_ASSEMBLER);
    }

    public TileQuantumAssembler(final EnumMachine type) {
        super(type);

        energyStorage = new EnergyStorage(ENERGY_PER_TICK * 2);
        inventory = new ItemStackHandler(7) {
            @Override
            protected void onContentsChanged(final int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }

            private boolean isItemValidForSlot(final int slot, final ItemStack itemStack) {
                if (slot == 6) {
                    return QuantumAssemblerRecipes.hasRecipe(itemStack);
                }

                return OreDictionaryHelper.isDarkmatterCell(itemStack);
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

    public IItemHandlerModifiable getInventory() {
        return inventory;
    }

    // Capablilities
    @Override
    public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return false;
        else return super.hasCapability(capability, facing);
    }


    // NBT Tags
    @Override
    public void readFromNBT(final NBTTagCompound tag) {
        super.readFromNBT(tag);

        InventoryUtility.readFromNBT(tag, inventory);
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(inventory, null, tag.getTag(NBT_SLOTS));
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setTag(NBT_SLOTS, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(inventory, null));

        return tag;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update() {
        super.update();

        if (!world.isRemote) {
            if (canFunction() && canProcess() && energyStorage.extractEnergy(ENERGY_PER_TICK, true) >= ENERGY_PER_TICK) {
                if (operatingTicks < TICKS_REQUIRED) {
                    operatingTicks++;
                    randomEntropy();
                } else {
                    process();
                    reset();
                }

                energyUsed = energyStorage.extractEnergy(ENERGY_PER_TICK, false);
            } else if (inventory.getStackInSlot(6).isEmpty()) {
                reset();
            }

            if (world.getWorldTime() % 10 == 0) {
                NuclearPhysics.getPacketHandler().sendToReceivers(new PacketTileEntity(this), this);
            }
        } else  {
            if (operatingTicks > 0) {
                if (world.getWorldTime() % 600 == 0) {
                    world.playSound(null, pos, ModSoundEvents.ASSEMBLER, SoundCategory.BLOCKS, 0.7F, 1);
                }

                rotationYaw1 += 3;
                rotationYaw2 += 2;
                rotationYaw3 += 1;
            }

            final ItemStack itemStack = inventory.getStackInSlot(6);

            if (!itemStack.isEmpty()) {
                if (entityItem == null || !itemStack.isItemEqual(entityItem.getItem())) {
                    entityItem = getEntityForItem(itemStack);
                }
            } else {
                entityItem = null;
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean canProcess() {
        final ItemStack itemStack = inventory.getStackInSlot(6);

        if (!itemStack.isEmpty()) {
            if (QuantumAssemblerRecipes.hasRecipe(itemStack)) {
                for (int i = 0; i <= 5; i++) {
                    final ItemStack itemStackInSlot = inventory.getStackInSlot(i);

                    if (!OreDictionaryHelper.isDarkmatterCell(itemStackInSlot)) {
                        return false;
                    }
                }
            }

            return itemStack.getCount() < 64;
        }

        return false;
    }

    // Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack.
    private void process() {
        if (canProcess()) {
            for (int slot = 0; slot <= 5; slot++) {
                if (!inventory.getStackInSlot(slot).isEmpty()) {
                    InventoryUtility.decrStackSize(inventory, slot);
                }
            }

            final ItemStack itemStack = inventory.getStackInSlot(6);

            if (!itemStack.isEmpty()) {
                itemStack.setCount(itemStack.getCount() + 1);
            }
        }
    }

    private void randomEntropy() {
        if (operatingTicks % 500 == 0){
            Random randomat = new Random();
            if (randomat.nextFloat() < 0.1f){
                InventoryUtility.decrStackSize(inventory, randomat.nextInt(6));
            }

        }
    }

    private EntityItem getEntityForItem(final ItemStack itemStack) {
        final EntityItem entityItem = new EntityItem(world, 0, 0, 0, itemStack.copy());
        entityItem.setAgeToCreativeDespawnTime();

        return entityItem;
    }

    public EntityItem getEntityItem() {
        return entityItem;
    }

    public float getRotationYaw1() {
        return rotationYaw1;
    }

    public float getRotationYaw2() {
        return rotationYaw2;
    }

    public float getRotationYaw3() {
        return rotationYaw3;
    }
}
