package org.halvors.datnuclearphysicslite.client.gui.machine;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.datnuclearphysicslite.client.gui.GuiMachine;
import org.halvors.datnuclearphysicslite.client.gui.component.GuiSlot;
import org.halvors.datnuclearphysicslite.common.container.machine.ContainerQuantumAssembler;
import org.halvors.datnuclearphysicslite.common.tile.machine.TileQuantumAssembler;
import org.halvors.datnuclearphysicslite.common.type.EnumResource;
import org.halvors.datnuclearphysicslite.common.utility.LanguageUtility;
import org.halvors.datnuclearphysicslite.common.utility.ResourceUtility;

@SideOnly(Side.CLIENT)
public class GuiQuantumAssembler extends GuiMachine<TileQuantumAssembler> {
    public GuiQuantumAssembler(final InventoryPlayer inventoryPlayer, final TileQuantumAssembler tile) {
        super(tile, new ContainerQuantumAssembler(inventoryPlayer, tile), 60);

        defaultResource = ResourceUtility.getResource(EnumResource.GUI, "quantum_assembler.png");
        ySize = 230;
        titleOffset = -30;

        components.add(new GuiSlot(this, 79, 39));
        components.add(new GuiSlot(this, 52, 55));
        components.add(new GuiSlot(this, 106, 55));
        components.add(new GuiSlot(this, 52, 87));
        components.add(new GuiSlot(this, 106, 87));
        components.add(new GuiSlot(this, 79, 102));
        components.add(new GuiSlot(this, 79, 71));
    }

    @Override
    public void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        final String displayText;

        if (tile.getOperatingTicks() > 0) {
            displayText = LanguageUtility.transelate("gui.progress") + ": " + (int) (((float) tile.getOperatingTicks() / (float) TileQuantumAssembler.TICKS_REQUIRED) * 100) + "%";
        } else if (tile.canProcess()) {
            displayText = LanguageUtility.transelate("gui.ready");
        } else {
            displayText = LanguageUtility.transelate("gui.idle");
        }

        fontRenderer.drawString(displayText, (xSize / 2) - 80, ySize - 106, 0x404040);

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}