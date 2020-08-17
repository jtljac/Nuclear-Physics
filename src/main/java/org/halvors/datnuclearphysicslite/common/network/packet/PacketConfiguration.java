package org.halvors.datnuclearphysicslite.common.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.halvors.datnuclearphysicslite.common.ConfigurationManager;

/**
 * This is a packet that synchronizes the configuration from the server to the client.
 */
public class PacketConfiguration implements IMessage {
	public PacketConfiguration() {

	}

	@Override
	public void fromBytes(final ByteBuf dataStream) {
		ConfigurationManager.readConfiguration(dataStream);
	}

	@Override
	public void toBytes(final ByteBuf dataStream) {
		ConfigurationManager.writeConfiguration(dataStream);
	}

	public static class PacketConfigurationMessage implements IMessageHandler<PacketConfiguration, IMessage> {
		@Override
		public IMessage onMessage(final PacketConfiguration message, final MessageContext messageContext) {
			return null;
		}
	}
}
