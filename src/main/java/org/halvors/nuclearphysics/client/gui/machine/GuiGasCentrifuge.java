package org.halvors.nuclearphysics.client.gui.machine;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.nuclearphysics.client.gui.GuiMachine;
import org.halvors.nuclearphysics.client.gui.component.GuiFluidGauge;
import org.halvors.nuclearphysics.client.gui.component.GuiProgress;
import org.halvors.nuclearphysics.client.gui.component.GuiSlot;
import org.halvors.nuclearphysics.client.gui.component.GuiSlot.EnumSlotType;
import org.halvors.nuclearphysics.common.container.machine.ContainerGasCentrifuge;
import org.halvors.nuclearphysics.common.tile.machine.TileGasCentrifuge;
import org.halvors.nuclearphysics.common.utility.LanguageUtility;

import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiGasCentrifuge extends GuiMachine<TileGasCentrifuge> {
    public GuiGasCentrifuge(final InventoryPlayer inventoryPlayer, final TileGasCentrifuge tile) {
        super(tile, new ContainerGasCentrifuge(inventoryPlayer, tile));

        components.add(new GuiSlot(this, 80, 25));
        components.add(new GuiSlot(this, 100, 25));
        components.add(new GuiSlot(EnumSlotType.BATTERY, this, 130, 25));
        components.add(new GuiProgress(() -> (double) tile.getOperatingTicks() / TileGasCentrifuge.TICKS_REQUIRED, this, 50, 26));
        components.add(new GuiFluidGauge(tile::getTank, this, (xSize / 2) - 80, 18));
        components.add(new GuiSlot(EnumSlotType.LIQUID, this, 24, 18));
        components.add(new GuiSlot(EnumSlotType.LIQUID, this, 24, 49));
    }

    @Override
    public void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        final String displayText;

        if (tile.getOperatingTicks() > 0) {
            displayText = "gui.processing";
        } else if (tile.canProcess()) {
            displayText = "gui.ready";
        } else {
            displayText = "gui.idle";
        }

        fontRenderer.drawString(LanguageUtility.transelate("gui.status") + ": " + LanguageUtility.transelate(displayText), 70, 50, 0x404040);

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}