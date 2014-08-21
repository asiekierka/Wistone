package pl.asie.wistone;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import pl.asie.lib.network.MessageHandlerBase;
import pl.asie.lib.network.Packet;
import pl.asie.lib.util.WorldUtils;
import pl.asie.wistone.block.TileBaseTransceiver;
import pl.asie.wistone.block.TileBaseTransceiverRedstone;
import pl.asie.wistone.util.RedstoneMode;

public class NetworkHandlerServer extends MessageHandlerBase {
	public static void transceiverGuiUpdate(TileBaseTransceiver tw, int buttonId) {
		switch(buttonId) {
		case 0: tw.setFrequency(tw.getFrequency() - 1); break;
		case 1: tw.setFrequency(tw.getFrequency() + 1); break;
		case 2: tw.setPowerStrength(tw.getPowerStrength() - 0.2f); break;
		case 3: tw.setPowerStrength(tw.getPowerStrength() + 0.2f); break;
		}
		if(buttonId == 4) {
			TileBaseTransceiverRedstone twr = (TileBaseTransceiverRedstone)tw;
			twr.setRedstoneMode((RedstoneMode)(twr.getRedstoneMode().next()));
		}
		if(!tw.getWorldObj().isRemote) {
			tw.markDirty();
			tw.getWorldObj().markBlockForUpdate(tw.xCoord, tw.yCoord, tw.zCoord);
		}
	}
	
	@Override
	public void onMessage(Packet packet, INetHandler handler, EntityPlayer player, int command) throws IOException {
		switch(command) {
		case Packets.C2S_TRANSCEIVER_GUI_UPDATE: {
			TileEntity te = packet.readTileEntityServer();
			if(te != null && (te instanceof TileBaseTransceiver)) {
				transceiverGuiUpdate((TileBaseTransceiver)te, packet.readByte());
			}
		} break;
		}
	}
}
