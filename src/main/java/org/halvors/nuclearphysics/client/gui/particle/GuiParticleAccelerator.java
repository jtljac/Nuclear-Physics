package org.halvors.nuclearphysics.client.gui.particle;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.nuclearphysics.client.gui.GuiMachine;
import org.halvors.nuclearphysics.client.gui.component.GuiSlot;
import org.halvors.nuclearphysics.common.container.particle.ContainerParticleAccelerator;
import org.halvors.nuclearphysics.common.entity.EntityParticle;
import org.halvors.nuclearphysics.common.science.unit.UnitDisplay;
import org.halvors.nuclearphysics.common.tile.particle.TileParticleAccelerator;
import org.halvors.nuclearphysics.common.type.EnumColor;
import org.halvors.nuclearphysics.common.type.EnumResource;
import org.halvors.nuclearphysics.common.utility.LanguageUtility;
import org.halvors.nuclearphysics.common.utility.ResourceUtility;

@SideOnly(Side.CLIENT)
public class GuiParticleAccelerator extends GuiMachine<TileParticleAccelerator> {
    public GuiParticleAccelerator(final InventoryPlayer inventoryPlayer, final TileParticleAccelerator tile) {
        super(tile, new ContainerParticleAccelerator(inventoryPlayer, tile), 47);

        defaultResource = ResourceUtility.getResource(EnumResource.GUI, "base_tall.png");
        ySize = 217;
        titleOffset = -24;

        components.add(new GuiSlot(this, 141, 25));
        components.add(new GuiSlot(this, 141, 50));
        components.add(new GuiSlot(this, 141, 74));
        components.add(new GuiSlot(this, 115, 74));
        components.add(new GuiSlot(GuiSlot.EnumSlotType.BATTERY, this, 89, 74));
    }

    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        final BlockPos pos = tile.getPos().offset(tile.getFacing().getOpposite());
        final String status;

        if (!EntityParticle.canSpawnParticle(tile.getWorld(), pos)) {
            status = EnumColor.DARK_RED + LanguageUtility.transelate("gui.failedToEmitTryRotating");
        } else if (tile.getEntityParticle() != null && tile.getVelocity() > 0) {
            status = EnumColor.ORANGE + LanguageUtility.transelate("gui.accelerating");
        } else {
            status = EnumColor.DARK_GREEN + LanguageUtility.transelate("gui.idle");
        }

        fontRenderer.drawString(LanguageUtility.transelate("gui.velocity") + ": " + (int) Math.floor((tile.getVelocity() / TileParticleAccelerator.ANTIMATTER_CREATION_SPEED) * 100) + "%", (xSize / 2) - 80, (ySize / 2) - 80, 0x404040);
        fontRenderer.drawString(LanguageUtility.transelate("gui.storedAntimatter") + ": " + tile.getAntimatterCount() + " mg", (xSize / 2) - 80, (ySize / 2) - 68, 0x404040);
        fontRenderer.drawString(LanguageUtility.transelate("gui.energyUsed") + ": " + UnitDisplay.getEnergyDisplay(tile.totalEnergyConsumed), (xSize / 2) - 80, (ySize / 2) - 56, 0x404040);
        fontRenderer.drawString(LanguageUtility.transelate("gui.status") + ": ", (xSize / 2) - 80, (ySize / 2) - 20, 0x404040);
        fontRenderer.drawString(status, (xSize / 2) - 80, (ySize / 2) - 8, 0x404040);
        //fontRendererObj.drawString("Buffer: " + UnitDisplay.getEnergyDisplay(tile.getEnergyStorage().getEnergyStored()) + "/" + UnitDisplay.getEnergyDisplay(tile.getEnergyStorage().getMaxEnergyStored()), (xSize / 2) - 80, 110, 0x404040);
        fontRenderer.drawString(LanguageUtility.transelate("gui.facing") + ": " + tile.getFacing().toString().toUpperCase(), 100, (ySize - 96) + 2, 0x404040);

        super.drawGuiContainerForegroundLayer(x, y);
    }
}