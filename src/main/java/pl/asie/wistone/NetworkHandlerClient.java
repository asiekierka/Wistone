package pl.asie.wistone;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.TileEntity;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;
import pl.asie.wistone.block.TileBaseTransceiver;

public class NetworkHandlerClient extends MessageHandlerBase {
	@Override
	public void onMessage(Packet packet, INetHandler handler, EntityPlayer player, int command) throws IOException {
	}
}
