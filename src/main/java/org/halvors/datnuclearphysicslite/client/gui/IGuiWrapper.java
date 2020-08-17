package org.halvors.datnuclearphysicslite.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public interface IGuiWrapper {
    void drawTexturedRect(int x, int y, int u, int v, int w, int h);

    void drawTexturedRectFromIcon(int x, int y, TextureAtlasSprite icon, int w, int h);

    void displayTooltip(String text, int xAxis, int yAxis);

    void displayTooltips(List<String> list, int xAxis, int yAxis);

    FontRenderer getFontRenderer();
}
