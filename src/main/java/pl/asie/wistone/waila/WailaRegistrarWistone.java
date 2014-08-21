package pl.asie.wistone.waila;

import java.util.List;

import net.minecraft.item.ItemStack;
import pl.asie.wistone.block.BlockRedstoneReceiver;
import pl.asie.wistone.block.BlockRedstoneTransmitter;
import pl.asie.wistone.block.BlockWistone;
import pl.asie.wistone.block.TileBaseTransceiver;
import pl.asie.wistone.block.TileBaseTransceiverRedstone;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaRegistrarWistone {
	public static void register(IWailaRegistrar reg) {
		reg.registerSyncedNBTKey("f", TileBaseTransceiver.class);
		reg.registerBodyProvider(new WailaFrequency(), BlockRedstoneTransmitter.class);
		reg.registerBodyProvider(new WailaRedstoneMode(), BlockRedstoneTransmitter.class);
		reg.registerBodyProvider(new WailaFrequency(), BlockRedstoneReceiver.class);
		reg.registerBodyProvider(new WailaRedstoneMode(), BlockRedstoneReceiver.class);
	}
	
	public static class WailaFrequency implements IWailaDataProvider {
		@Override
		public ItemStack getWailaStack(IWailaDataAccessor accessor,
				IWailaConfigHandler config) { return null; }
		
		@Override
		public List<String> getWailaHead(ItemStack itemStack,
				List<String> currenttip, IWailaDataAccessor accessor,
				IWailaConfigHandler config) { return currenttip; }

		@Override
		public List<String> getWailaBody(ItemStack itemStack,
				List<String> currenttip, IWailaDataAccessor accessor,
				IWailaConfigHandler config) {
			currenttip.add("Frequency: " + accessor.getNBTData().getShort("f") + " Dd");
			currenttip.add("Strength: " + String.format("%.1f", ((TileBaseTransceiver)accessor.getTileEntity()).getStrength()) + "x");
			return currenttip;
		}

		@Override
		public List<String> getWailaTail(ItemStack itemStack,
				List<String> currenttip, IWailaDataAccessor accessor,
				IWailaConfigHandler config) { return currenttip; }
	}
	public static class WailaRedstoneMode implements IWailaDataProvider {
		@Override
		public ItemStack getWailaStack(IWailaDataAccessor accessor,
				IWailaConfigHandler config) { return null; }
		
		@Override
		public List<String> getWailaHead(ItemStack itemStack,
				List<String> currenttip, IWailaDataAccessor accessor,
				IWailaConfigHandler config) { return currenttip; }

		@Override
		public List<String> getWailaBody(ItemStack itemStack,
				List<String> currenttip, IWailaDataAccessor accessor,
				IWailaConfigHandler config) {
			currenttip.add("Redstone Mode: " + ((TileBaseTransceiverRedstone)accessor.getTileEntity()).getRedstoneMode().getTooltip().get(0));
			return currenttip;
		}

		@Override
		public List<String> getWailaTail(ItemStack itemStack,
				List<String> currenttip, IWailaDataAccessor accessor,
				IWailaConfigHandler config) { return currenttip; }
	}
}
