package pl.asie.wistone;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.world.World;
import pl.asie.lib.util.BlockCoord;
import pl.asie.wistone.api.IDataSource;

public class WorldDataManager {
	private final HashSet<IDataSource> transmitters = new HashSet<IDataSource>();
	private World world;
	
	public WorldDataManager(World world) {
		this.world = world;
	}
	
	public void tick() {
	}
	
	public void register(IDataSource transmitter) {
		transmitters.add(transmitter);
	}
	
	public void unregister(IDataSource transmitter) {
		transmitters.remove(transmitter);
	}
	
	public Set<IDataSource> getSources(BlockCoord location, int minF, int maxF) {
		HashSet<IDataSource> out = new HashSet<IDataSource>();
		for(IDataSource packet: transmitters) {
			if(!packet.canTransmit()) continue;
			int maxFPacket = packet.getFrequency() + packet.getFrequencyLength() - 1;
			if(maxFPacket < minF || packet.getFrequency() > maxF) continue;
			out.add(packet);
		}
		return out;
	}
}
