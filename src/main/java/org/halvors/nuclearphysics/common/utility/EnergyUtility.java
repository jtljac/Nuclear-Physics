package org.halvors.nuclearphysics.common.utility;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class EnergyUtility {
    /**
     * Universally discharges an item, and updates the TileEntity's energy level.
     * @param energyStack - The itemstack to discharge from
     * @param tile - TileEntity the item is being charged in.
     */
    public static void discharge(final ItemStack energyStack, final TileEntity tile) {
        if (tile != null && tile.hasCapability(CapabilityEnergy.ENERGY, null)) {
            final IEnergyStorage energyStorage = tile.getCapability(CapabilityEnergy.ENERGY, null);
            if (canBeDischarged(energyStack)) {
                if (energyStack.hasCapability(CapabilityEnergy.ENERGY, null)) {
                    final IEnergyStorage itemEnergyStorage = energyStack.getCapability(CapabilityEnergy.ENERGY, null);
                    if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
                        final int needed = Math.round(Math.min(Integer.MAX_VALUE, (energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored())));
                        energyStorage.receiveEnergy(itemEnergyStorage.extractEnergy(needed, false), false);
                    }
                }
            }
        }
    }

    /**
     * Whether or not a defined ItemStack can be discharged for energy in some way.
     * @param itemStack - ItemStack to check
     * @return if the ItemStack can be discharged
     */
    public static boolean canBeDischarged(final ItemStack itemStack) {
        return !itemStack.isEmpty() && itemStack.hasCapability(CapabilityEnergy.ENERGY, null) && itemStack.getCapability(CapabilityEnergy.ENERGY, null).canExtract();
    }
}
