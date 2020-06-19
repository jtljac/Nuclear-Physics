package org.halvors.nuclearphysics.common.container.machine;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.SlotItemHandler;
import org.halvors.nuclearphysics.common.container.ContainerBase;
import org.halvors.nuclearphysics.common.tile.machine.TileChemicalExtractor;

public class ContainerChemicalExtractor extends ContainerBase<TileChemicalExtractor> {
    public ContainerChemicalExtractor(final InventoryPlayer inventoryPlayer, final TileChemicalExtractor tile) {
        super(7, inventoryPlayer, tile);

        // Battery
        addSlotToContainer(new SlotItemHandler(tile.getInventoryEnergy(), 0, 80, 50));

        // Process Input (Uranium)
        addSlotToContainer(new SlotItemHandler(tile.getInventoryInput(), 0, 53, 25));

        // Process Output
        addSlotToContainer(new SlotItemHandler(tile.getInventoryOutput(), 0, 107, 25));

        // Fluid input fill
        addSlotToContainer(new SlotItemHandler(tile.getInventoryFluids(), 0, 25, 19));

        // Fluid input drain
        addSlotToContainer(new SlotItemHandler(tile.getInventoryFluids(), 1, 25, 50));

        // Fluid output fill
        addSlotToContainer(new SlotItemHandler(tile.getInventoryFluids(), 2, 135, 19));

        // Fluid output drain
        addSlotToContainer(new SlotItemHandler(tile.getInventoryFluids(), 3, 135, 50));

        // Player inventory
        addPlayerInventory(inventoryPlayer.player);
    }
}