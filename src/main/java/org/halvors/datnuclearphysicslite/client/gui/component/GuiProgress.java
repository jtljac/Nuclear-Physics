package org.halvors.datnuclearphysicslite.client.gui.component;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.datnuclearphysicslite.client.gui.IGuiWrapper;
import org.halvors.datnuclearphysicslite.client.utility.RenderUtility;
import org.halvors.datnuclearphysicslite.common.type.EnumResource;
import org.halvors.datnuclearphysicslite.common.utility.ResourceUtility;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class GuiProgress extends GuiComponent {
    private static final int WIDTH = 22;
    private static final int HEIGHT = 16;

    private final IProgressInfoHandler progressInfoHandler;

    public GuiProgress(final IProgressInfoHandler progressInfoHandler, final IGuiWrapper gui, final int x, final int y) {
        super(ResourceUtility.getResource(EnumResource.GUI_COMPONENT, "progress.png"), gui, x, y);

        this.progressInfoHandler = progressInfoHandler;
    }

    @Override
    public Rectangle getBounds(final int guiWidth, final int guiHeight) {
        return new Rectangle(guiWidth + xLocation, guiHeight + yLocation, WIDTH, HEIGHT);
    }

    @Override
    public void renderBackground(final int xAxis, final int yAxis, final int guiWidth, final int guiHeight) {
        RenderUtility.bindTexture(resource);

        gui.drawTexturedRect(guiWidth + xLocation, guiHeight + yLocation, 0, 0, WIDTH, HEIGHT);

        final int scale = (int) (progressInfoHandler.getProgress() * (WIDTH + 1));

        if (scale > 0) {
            RenderUtility.bindTexture(resource);

            gui.drawTexturedRect(guiWidth + xLocation, guiHeight + yLocation, WIDTH, 0, scale, HEIGHT);
        }
    }

    @Override
    public void renderForeground(final int xAxis, final int yAxis) {

    }

    @Override
    public void preMouseClicked(final int xAxis, final int yAxis, final int button) {

    }

    @Override
    public void mouseClicked(final int xAxis, final int yAxis, final int button) {

    }

    @Override
    public void mouseClickMove(final int mouseX, final int mouseY, final int button, final long ticks) {

    }

    @Override
    public void mouseReleased(final int x, final int y, final int type) {

    }

    @Override
    public void mouseWheel(final int x, final int y, final int delta) {

    }
}
