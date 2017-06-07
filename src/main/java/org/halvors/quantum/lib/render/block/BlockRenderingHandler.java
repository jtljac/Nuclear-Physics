package org.halvors.quantum.lib.render.block;

import com.google.common.collect.Maps;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.halvors.quantum.client.render.machine.RenderCentrifuge;
import org.halvors.quantum.client.render.machine.RenderNuclearBoiler;
import org.halvors.quantum.common.Reference;
import org.halvors.quantum.common.tile.machine.TileCentrifuge;
import org.halvors.quantum.common.tile.machine.TileNuclearBoiler;
import org.halvors.quantum.common.transform.vector.Vector3;
import org.halvors.quantum.lib.item.ISimpleItemRenderer;
import org.halvors.quantum.lib.tile.BlockDummy;
import org.halvors.quantum.lib.tile.TileBlock;
import org.halvors.quantum.lib.utility.RenderUtility;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class BlockRenderingHandler implements ISimpleBlockRenderingHandler {
    public static final BlockRenderingHandler INSTANCE = new BlockRenderingHandler();
    public static final int ID = RenderingRegistry.getNextAvailableRenderId();
    public static final Map<Block, TileEntity> inventoryTileEntities = Maps.newIdentityHashMap();

    public TileEntity getTileEntityForBlock(Block block) {
        TileEntity tileEntity = inventoryTileEntities.get(block);

        if (tileEntity == null) {
            tileEntity = block.createTileEntity(Minecraft.getMinecraft().thePlayer.getEntityWorld(), 0);
            inventoryTileEntities.put(block, tileEntity);
        }

        return tileEntity;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        if (block instanceof BlockDummy) {
            TileBlock tile = ((BlockDummy) block).dummyTile;

            if (tile != null) {
                boolean didRender = true;
                GL11.glEnable(32826);
                GL11.glPushAttrib(262144);
                GL11.glPushMatrix();
                GL11.glTranslated(-0.5D, -0.5D, -0.5D);

                if (tile.getRenderer() != null) {
                    if (!tile.getRenderer().renderItem(new ItemStack(block, 1, metadata))) {
                        if (!tile.getRenderer().renderDynamic(new Vector3(), true, 0.0F)) {
                            if (!tile.customItemRender) {
                                GL11.glTranslated(0.5D, 0.5D, 0.5D);
                                RenderUtility.renderNormalBlockAsItem(block, metadata, renderer);
                            } else {
                                didRender = false;
                            }
                        }
                    }
                } else if (!tile.customItemRender) {
                    GL11.glTranslated(0.5D, 0.5D, 0.5D);
                    RenderUtility.renderNormalBlockAsItem(block, metadata, renderer);
                } else {
                    didRender = false;
                }

                GL11.glPopMatrix();
                GL11.glPopAttrib();

                if (didRender) {
                    return;
                }
            }
        }

        TileEntity renderTile = null;

        if (block.hasTileEntity(metadata)) {
            renderTile = getTileEntityForBlock(block);
        }

        GL11.glEnable(32826);

        if (renderTile != null) {
            GL11.glPushAttrib(262144);
            GL11.glPushMatrix();
            GL11.glTranslated(-0.5D, -0.5D, -0.5D);

            TileEntitySpecialRenderer tesr = TileEntityRendererDispatcher.instance.getSpecialRenderer(renderTile);

            try {
                if (tesr instanceof ISimpleItemRenderer) {
                    ((ISimpleItemRenderer) tesr).renderInventoryItem(new ItemStack(block, 1, metadata));
                } else {
                    tesr.renderTileEntityAt(renderTile, 0, 0, 0, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess access, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if ((block instanceof BlockDummy)) {
            BlockDummy dummy = (BlockDummy) block;
            dummy.inject(access, x, y, z);
            TileBlock tileBlock = dummy.getTile(access, x, y, z);

            if (tileBlock != null) {
                tileBlock.access = access;

                if (tileBlock.getRenderer() != null) {
                    if (!tileBlock.getRenderer().renderStatic(renderer, new Vector3(x, y, z))) {
                        renderer.renderStandardBlock(block, x, y, z);
                    }
                } else if (tileBlock.normalRender || tileBlock.forceStandardRender) {
                    renderer.renderStandardBlock(block, x, y, z);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return ID;
    }
}
