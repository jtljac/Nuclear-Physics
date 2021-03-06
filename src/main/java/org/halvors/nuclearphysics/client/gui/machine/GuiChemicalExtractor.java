package org.halvors.nuclearphysics.client.gui.machine;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.nuclearphysics.client.gui.GuiMachine;
import org.halvors.nuclearphysics.client.gui.component.GuiFluidGauge;
import org.halvors.nuclearphysics.client.gui.component.GuiProgress;
import org.halvors.nuclearphysics.client.gui.component.GuiSlot;
import org.halvors.nuclearphysics.client.gui.component.GuiSlot.EnumSlotType;
import org.halvors.nuclearphysics.common.container.machine.ContainerChemicalExtractor;
import org.halvors.nuclearphysics.common.tile.machine.TileChemicalExtractor;

@SideOnly(Side.CLIENT)
public class GuiChemicalExtractor extends GuiMachine<TileChemicalExtractor> {
    public GuiChemicalExtractor(final InventoryPlayer inventory, final TileChemicalExtractor tile) {
        super(tile, new ContainerChemicalExtractor(inventory, tile));

        components.add(new GuiSlot(EnumSlotType.BATTERY, this, 79, 49));
        components.add(new GuiSlot(EnumSlotType.NORMAL, this, 52, 24));
        components.add(new GuiSlot(this, 106, 24));
        components.add(new GuiProgress(() -> (double) tile.getOperatingTicks() / TileChemicalExtractor.TICKS_REQUIRED, this, 75, 24));
        components.add(new GuiFluidGauge(tile::getInputTank, this, (xSize / 2) - 80, 18));
        components.add(new GuiSlot(EnumSlotType.LIQUID, this, 24, 18));
        components.add(new GuiSlot(EnumSlotType.LIQUID, this, 24, 49));
        components.add(new GuiSlot(EnumSlotType.LIQUID, this, 134, 18));
        components.add(new GuiSlot(EnumSlotType.LIQUID, this, 134, 49));
        components.add(new GuiFluidGauge(tile::getOutputTank, this, 154, 18));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}
