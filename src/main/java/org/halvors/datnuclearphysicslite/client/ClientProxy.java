package org.halvors.datnuclearphysicslite.client;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.datnuclearphysicslite.client.gui.machine.GuiQuantumAssembler;
import org.halvors.datnuclearphysicslite.client.gui.particle.GuiParticleAccelerator;
import org.halvors.datnuclearphysicslite.client.render.block.machine.RenderQuantumAssembler;
import org.halvors.datnuclearphysicslite.client.render.entity.RenderParticle;
import org.halvors.datnuclearphysicslite.common.CommonProxy;
import org.halvors.datnuclearphysicslite.common.Reference;
import org.halvors.datnuclearphysicslite.common.entity.EntityParticle;
import org.halvors.datnuclearphysicslite.common.tile.machine.TileQuantumAssembler;
import org.halvors.datnuclearphysicslite.common.tile.particle.TileParticleAccelerator;

/**
 * This is the client proxy used only by the client.
 *
 * @author halvors
 */
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy implements IGuiHandler {
	@Override
	public void preInit() {
	    // Register our domain to OBJLoader.
		OBJLoader.INSTANCE.addDomain(Reference.DOMAIN);

		// Register entity renderer.
		RenderingRegistry.registerEntityRenderingHandler(EntityParticle.class, RenderParticle::new);
	}

	@Override
	public void init() {
        // Register special renderer.
		ClientRegistry.bindTileEntitySpecialRenderer(TileQuantumAssembler.class, new RenderQuantumAssembler());
	}

	@Override
	public void registerBlockRenderer(final Block block, final IProperty property, final String name) {
		ModelLoader.setCustomStateMapper(block, (new StateMap.Builder()).withName(property).withSuffix("_" + name).build());
	}

	@Override
	public void registerBlockRendererAndIgnore(final Block block, final IProperty property) {
		ModelLoader.setCustomStateMapper(block, (new StateMap.Builder()).ignore(property).build());
	}

	@Override
	public void registerItemRenderer(final Item item, final int metadata, final String id) {
		registerItemRenderer(item, metadata, id, "inventory");
	}

	@Override
	public void registerItemRenderer(final Item item, final int metadata, final String id, final String variant) {
		ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation(Reference.PREFIX + id, variant));
	}

	@Override
	public Object getClientGuiElement(final int id, final EntityPlayer player, final World world, final int x, final int y, final int z) {
		final BlockPos pos = new BlockPos(x, y, z);
		final TileEntity tile = world.getTileEntity(pos);
		final IBlockState state = world.getBlockState(pos);
		final Block block = state.getBlock();

		if (tile instanceof TileParticleAccelerator) {
			return new GuiParticleAccelerator(player.inventory, (TileParticleAccelerator) tile);
		} else if (tile instanceof TileQuantumAssembler) {
			return new GuiQuantumAssembler(player.inventory, (TileQuantumAssembler) tile);
		}

		return null;
	}

	@Override
	public EntityPlayer getPlayer(final MessageContext context) {
		if (context.side.isServer()) {
			return context.getServerHandler().player;
		} else {
			return Minecraft.getMinecraft().player;
		}
	}

	@Override
	public void addScheduledTask(final Runnable runnable, final IBlockAccess world) {
		if (world == null || isClient()) {
			Minecraft.getMinecraft().addScheduledTask(runnable);
		} else {
			super.addScheduledTask(runnable, world);
		}
	}

	@Override
	public boolean isClient() {
		return !isServer();
	}

	@Override
	public boolean isPaused() {
		final Minecraft minecraft = FMLClientHandler.instance().getClient();
		final IntegratedServer integratedServer = minecraft.getIntegratedServer();

		if (minecraft.isSingleplayer() && integratedServer != null && !integratedServer.getPublic()) {
			final GuiScreen screen = minecraft.currentScreen;

			return screen != null && screen.doesGuiPauseGame();
		}

		return false;
	}
}