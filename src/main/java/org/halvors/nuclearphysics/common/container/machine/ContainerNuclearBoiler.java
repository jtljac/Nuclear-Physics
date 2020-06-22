package org.halvors.nuclearphysics.common.container.machine;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.SlotItemHandler;
import org.halvors.nuclearphysics.common.container.ContainerBase;
import org.halvors.nuclearphysics.common.tile.machine.TileNuclearBoiler;

public class ContainerNuclearBoiler extends ContainerBase<TileNuclearBoiler> {
    public ContainerNuclearBoiler(final InventoryPlayer inventoryPlayer, final TileNuclearBoiler tile) {
        super(5, inventoryPlayer, tile);

        // Battery
        addSlotToContainer(new SlotItemHandler(tile.getInventoryEnergy(), 0, 56, 26));

        // Yellowcake Input
        addSlotToContainer(new SlotItemHandler(tile.getInventoryInput(), 0, 81, 26));

        // Fluid input fill
        addSlotToContainer(new SlotItemHandler(tile.getInventoryFluids(), 0, 25, 19));

        // Fluid input drain
        addSlotToContainer(new SlotItemHandler(tile.getInventoryFluids(), 1, 25, 50));


        // Fluid input fill
        addSlotToContainer(new SlotItemHandler(tile.getInventoryFluids(), 2, 135, 19));

        // Fluid output drain
        addSlotToContainer(new SlotItemHandler(tile.getInventoryFluids(), 3, 135, 50));

        // Player inventory
        addPlayerInventory(inventoryPlayer.player);
    }
}
