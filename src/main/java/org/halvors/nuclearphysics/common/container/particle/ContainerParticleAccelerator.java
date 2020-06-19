package org.halvors.nuclearphysics.common.container.particle;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.SlotItemHandler;
import org.halvors.nuclearphysics.common.container.ContainerBase;
import org.halvors.nuclearphysics.common.tile.particle.TileParticleAccelerator;

public class ContainerParticleAccelerator extends ContainerBase<TileParticleAccelerator> {
    public ContainerParticleAccelerator(final InventoryPlayer inventoryPlayer, final TileParticleAccelerator tile) {
        super(4, inventoryPlayer, tile);

        // Inputs
        addSlotToContainer(new SlotItemHandler(tile.getInventoryInMatter(), 0, 142, 26));
        addSlotToContainer(new SlotItemHandler(tile.getInventoryInCells(), 0, 142, 51));

        // Output
        addSlotToContainer(new SlotItemHandler(tile.getInventoryOut(), 0, 142, 75));
        addSlotToContainer(new SlotItemHandler(tile.getInventoryOut(), 1, 116, 75));

        addSlotToContainer(new SlotItemHandler(tile.getInventoryEnergy(), 0, 90, 75));

        // Player inventory
        addPlayerInventory(inventoryPlayer.player);
    }
}
