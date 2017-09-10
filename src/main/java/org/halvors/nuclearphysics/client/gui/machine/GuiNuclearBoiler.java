package org.halvors.nuclearphysics.client.gui.machine;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.nuclearphysics.client.gui.GuiComponentContainer;
import org.halvors.nuclearphysics.client.gui.component.*;
import org.halvors.nuclearphysics.client.gui.component.GuiSlot.SlotType;
import org.halvors.nuclearphysics.common.container.machine.ContainerNuclearBoiler;
import org.halvors.nuclearphysics.common.tile.machine.TileGasCentrifuge;
import org.halvors.nuclearphysics.common.tile.machine.TileNuclearBoiler;
import org.halvors.nuclearphysics.common.utility.LanguageUtility;
import org.halvors.nuclearphysics.common.utility.energy.UnitDisplay;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiNuclearBoiler extends GuiMachine<TileNuclearBoiler> {
    public GuiNuclearBoiler(InventoryPlayer inventoryPlayer, TileNuclearBoiler tile) {
        super(tile, new ContainerNuclearBoiler(inventoryPlayer, tile));

        components.add(new GuiSlot(SlotType.BATTERY, this, 55, 25));
        components.add(new GuiSlot(SlotType.NORMAL, this, 80, 25));
        components.add(new GuiProgress(() -> (double) tile.operatingTicks / tile.ticksRequired, this, 110, 26));
        components.add(new GuiFluidGauge(tile::getInputTank, this, (xSize / 2) - 80, 18));
        components.add(new GuiSlot(SlotType.LIQUID, this, 24, 18));
        components.add(new GuiSlot(SlotType.LIQUID, this, 24, 49));
        components.add(new GuiFluidGauge(tile::getOutputTank, this, 154, 18));
        components.add(new GuiSlot(SlotType.GAS, this, 134, 49));
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        List<String> list = LanguageUtility.splitStringPerWord(LanguageUtility.transelate(tile.getBlockType().getUnlocalizedName() + "." + tile.getType().ordinal() + ".text"), 4);

        for (int i = 0; i < list.size(); i++) {
            fontRendererObj.drawString(list.get(i), (xSize / 2) - 80, 85 + i * 9, 0x404040);
        }

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}