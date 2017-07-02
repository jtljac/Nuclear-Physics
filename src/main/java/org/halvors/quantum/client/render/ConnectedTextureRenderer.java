package org.halvors.quantum.client.render;

/*
@SideOnly(Side.CLIENT)
public class ConnectedTextureRenderer implements ISimpleBlockRenderer {
    private Block block;
    private IIcon edgeTexture;

    public ConnectedTextureRenderer(Block block, String edgeTexture) {
        this.block = block;
        this.edgeTexture = RenderUtility.getIcon(edgeTexture);
    }

    @Override
    public boolean renderItem(ItemStack itemStack) {
        GL11.glPushMatrix();
        BlockRenderUtility.tessellateBlockWithConnectedTextures(itemStack.getMetadata(), block, null, edgeTexture);
        GL11.glPopMatrix();

        return true;
    }

    @Override
    public boolean renderStatic(RenderBlocks renderer, IBlockAccess access, int x, int y, int z) {
        TileEntity tile = access.getTileEntity(x, y, z);
        byte sideMap = 0;

        for (EnumFacing direction : EnumFacing.VALUES) {
            Vector3 check = new Vector3(x, y, z).translate(direction);
            TileEntity checkTile = check.getTileEntity(access);

            if (checkTile != null && checkTile.getClass() == tile.getClass() && check.getBlockMetadata(access) == tile.getBlockMetadata()) {
                sideMap = WorldUtility.setEnableSide(sideMap, direction, true);
            }
        }

        BlockRenderUtility.tessellateBlockWithConnectedTextures(sideMap, access, x, y, z, block, null, edgeTexture);

        return true;
    }
}
*/