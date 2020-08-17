package org.halvors.datnuclearphysicslite.client.render.block;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.datnuclearphysicslite.client.utility.RenderUtility;
import org.halvors.datnuclearphysicslite.common.tile.ITileRotatable;

@SideOnly(Side.CLIENT)
public abstract class RenderTile<T extends TileEntity> extends TileEntitySpecialRenderer<T> {
    @Override
    public void render(final T tile, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
        GlStateManager.pushMatrix();

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        // Translate to the location of our tile entity
        GlStateManager.translate(x, y, z);
        GlStateManager.disableRescaleNormal();

        // Rotate block based on direction.
        if (tile instanceof ITileRotatable) {
            RenderUtility.rotateBlockBasedOnDirection(((ITileRotatable) tile).getFacing());
        }

        render(tile, x, y, z);

        GlStateManager.popMatrix();
    }

    protected abstract void render(final T tile, final double x, final double y, final double z);
}
