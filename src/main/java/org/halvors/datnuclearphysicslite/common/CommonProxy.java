package org.halvors.datnuclearphysicslite.common;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.halvors.datnuclearphysicslite.common.container.machine.ContainerQuantumAssembler;
import org.halvors.datnuclearphysicslite.common.container.particle.ContainerParticleAccelerator;
import org.halvors.datnuclearphysicslite.common.tile.machine.TileQuantumAssembler;
import org.halvors.datnuclearphysicslite.common.tile.particle.TileParticleAccelerator;

/**
 * This is the common proxy used by both the client and the server.
 *
 * @author halvors
 */
public class CommonProxy implements IGuiHandler {
	public void preInit() {

	}

	public void init() {

	}

	public void postInit() {

	}

	public void registerBlockRenderer(final Block block, final IProperty property, final String name) {

	}

	public void registerBlockRendererAndIgnore(final Block block, final IProperty property) {

	}

	public void registerItemRenderer(final Item item, int metadata, final String id) {

	}

	public void registerItemRenderer(final Item item, int metadata, final String id, final String variant) {

	}

	@Override
	public Object getServerGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		final TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

		if (tile instanceof TileParticleAccelerator) {
			return new ContainerParticleAccelerator(player.inventory, (TileParticleAccelerator) tile);
		} else if (tile instanceof TileQuantumAssembler) {
			return new ContainerQuantumAssembler(player.inventory, (TileQuantumAssembler) tile);
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		return null;
	}

	public EntityPlayer getPlayer(final MessageContext context) {
		return context.getServerHandler().player;
	}

	public void addScheduledTask(final Runnable runnable, final IBlockAccess world) {
		((WorldServer) world).addScheduledTask(runnable);
	}

	public boolean isClient() {
		return false;
	}

	public boolean isServer() {
		return FMLCommonHandler.instance().getSide().isServer();
	}

	public boolean isPaused() {
		return false;
	}
}