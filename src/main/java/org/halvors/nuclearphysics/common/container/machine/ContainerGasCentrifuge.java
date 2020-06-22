package org.halvors.nuclearphysics.common.container.machine;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.SlotItemHandler;
import org.halvors.nuclearphysics.common.container.ContainerBase;
import org.halvors.nuclearphysics.common.tile.machine.TileGasCentrifuge;

public class ContainerGasCentrifuge extends ContainerBase<TileGasCentrifuge> {
    public ContainerGasCentrifuge(final InventoryPlayer inventoryPlayer, final TileGasCentrifuge tile) {
        super(4, inventoryPlayer, tile);

        // Battery
        addSlotToContainer(new SlotItemHandler(tile.getInventoryEnergy(), 0, 131, 26));


        // Uranium Gas Tank output
        addSlotToContainer(new SlotItemHandler(tile.getInventoryFluid(), 0, 25, 19));

        // Uranium Gas Tank Input
        addSlotToContainer(new SlotItemHandler(tile.getInventoryFluid(), 1, 25, 50));

        // output Uranium 235
        addSlotToContainer(new SlotItemHandler(tile.getInventoryOutput(), 0, 81, 26));

        // Output Uranium 238
        addSlotToContainer(new SlotItemHandler(tile.getInventoryOutput(), 1, 101, 26));

        // Player inventory
        addPlayerInventory(inventoryPlayer.player);
    }
}
