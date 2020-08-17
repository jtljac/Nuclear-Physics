package org.halvors.datnuclearphysicslite.client.gui.component;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

@SideOnly(Side.CLIENT)
public interface IGuiComponent {
    Rectangle getBounds(int guiWidth, int guiHeight);

    void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight);

    void renderForeground(int xAxis, int yAxis);

    void preMouseClicked(int xAxis, int yAxis, int button);

    void mouseClicked(int xAxis, int yAxis, int button);

    void mouseClickMove(int mouseX, int mouseY, int button, long ticks);

    void mouseReleased(int x, int y, int type);

    void mouseWheel(int x, int y, int delta);
}