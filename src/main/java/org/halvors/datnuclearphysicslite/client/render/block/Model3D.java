package org.halvors.datnuclearphysicslite.client.render.block;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Model3D {
    public double posX;
    public double posY;
    public double posZ;

    public double minX;
    public double minY;
    public double minZ;
    public double maxX;
    public double maxY;
    public double maxZ;

    public double textureStartX = 0;
    public double textureStartY = 0;
    public double textureStartZ = 0;
    public double textureSizeX = 16;
    public double textureSizeY = 16;
    public double textureSizeZ = 16;
    public double textureOffsetX = 0;
    public double textureOffsetY = 0;
    public double textureOffsetZ = 0;

    public int[] textureFlips = new int[] { 2, 2, 2, 2, 2, 2 };

    private TextureAtlasSprite texture;

    public Model3D(final double minX, final double minY, final double minZ, final double maxX, final double maxY, final double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public TextureAtlasSprite getTexture() {
        return texture;
    }

    public void setTexture(final TextureAtlasSprite texture) {
        this.texture = texture;
    }

    public double sizeX() {
        return maxX - minX;
    }

    public double sizeY() {
        return maxY - minY;
    }

    public double sizeZ() {
        return maxZ - minZ;
    }

    public void render() {
        GlStateManager.translate(minX, minY, minZ);
        RenderResizableCuboid.INSTANCE.renderCube(this);
    }
}