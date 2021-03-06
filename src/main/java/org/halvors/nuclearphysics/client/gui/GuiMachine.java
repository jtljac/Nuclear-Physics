package org.halvors.nuclearphysics.client.gui;

import net.minecraft.inventory.Container;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.nuclearphysics.client.gui.component.GuiEnergyInfo;
import org.halvors.nuclearphysics.client.gui.component.GuiRedstoneControl;
import org.halvors.nuclearphysics.common.science.unit.UnitDisplay;
import org.halvors.nuclearphysics.common.tile.TileMachine;
import org.halvors.nuclearphysics.common.utility.LanguageUtility;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiMachine<T extends TileMachine> extends GuiComponentContainer<T> {
    protected int titleOffset;

    public GuiMachine(final T tile, final Container container, final int sideOffset) {
        super(tile, container);
        components.add(new GuiEnergyInfo(() -> {
            final List<String> list = new ArrayList<>();
            list.add(LanguageUtility.transelate("gui.using") + ": " + UnitDisplay.getEnergyDisplay(tile.getEnergyUsed()) + "/t");

            final IEnergyStorage energyStorage = tile.getEnergyStorage();

            if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
                list.add(LanguageUtility.transelate("gui.needed") + ": " + UnitDisplay.getEnergyDisplay(energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()));
            } else {
                list.add(LanguageUtility.transelate("gui.buffer") + ": " + UnitDisplay.getEnergyDisplay(energyStorage.getEnergyStored()));
            }

            return list;
        }, this, -26, 142 + sideOffset));
        components.add(new GuiRedstoneControl(tile, this, 176, 142 + sideOffset));
    }

    public GuiMachine(final T tile, final Container container) {
        this(tile, container, 0);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tile.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tile.getName()) / 2), (ySize / 2) - 80 + titleOffset, 0x404040);
        fontRenderer.drawString(LanguageUtility.transelate("container.inventory"), (xSize / 2) - 80, (ySize - 92), 0x404040);

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}
