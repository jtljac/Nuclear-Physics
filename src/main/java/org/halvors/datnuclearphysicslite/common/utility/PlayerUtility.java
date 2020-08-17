package org.halvors.datnuclearphysicslite.common.utility;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.halvors.datnuclearphysicslite.common.NuclearPhysics;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtility {
	public static List<EntityPlayerMP> getPlayers() {
		final List<EntityPlayerMP> playerList = new ArrayList<>();
		final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		if (server != null) {
			return server.getPlayerList().getPlayers();
		}

		return playerList;
	}

	public static boolean isOp(final EntityPlayer player) {
		return player instanceof EntityPlayerMP && ((EntityPlayerMP) player).server.getPlayerList().canSendCommands(player.getGameProfile());
	}

	public static void openGui(final EntityPlayer player, final IBlockAccess world, final BlockPos pos) {
		player.openGui(NuclearPhysics.getInstance(), 0, (World) world, pos.getX(), pos.getY(), pos.getZ());
	}
}
