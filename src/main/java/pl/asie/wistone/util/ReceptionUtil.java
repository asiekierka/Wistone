package pl.asie.wistone.util;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import pl.asie.lib.util.BlockCoord;
import pl.asie.wistone.Wistone;
import pl.asie.wistone.api.IDataSource;

public class ReceptionUtil {
	public static int getSignalDistance(World world, BlockCoord target, IDataSource source, float receiverStrength) {
		double distance = source.getLocation().distance(target);
		// When thundering, the signal is going to go crazy.
		if(world.isThundering()) distance *= Wistone.rand.nextDouble() + 0.5;
		distance /= receiverStrength;
		distance /= source.getStrength();
		return (int)Math.round(distance);
	}
	
	public static IDataSource getStrongestSource(World world, BlockCoord location, Set<IDataSource> sources, int frequency, float receiverStrength) {
		IDataSource strongestSource = null;
		int strongestDistance = 100000000;
		for(IDataSource source: sources) {
			if(source.getFrequency() > frequency || (source.getFrequency() + source.getFrequencyLength()) <= frequency) continue;
			int pDist = getSignalDistance(world, location, source, receiverStrength);
			if(pDist < strongestDistance) {
				strongestSource = source;
				strongestDistance = pDist;
			}
		}
		return strongestSource;
	}
	
	public static int getSignal(World world, BlockCoord location, Set<IDataSource> sources, int frequency, float receiverStrength) {
		IDataSource source = getStrongestSource(world, location, sources, frequency, receiverStrength);
		if(source == null) return (byte)getDefaultNoise(world);
		int pDist = getSignalDistance(world, location, source, receiverStrength);
		int signal = source.getData()[frequency - source.getFrequency()] & 0xFF;
		// Noise kicks in at a distance of over 64 distance units, at 576 blocks it becomes unhearable.
		if(pDist > Wistone.MAX_CLEAR_DISTANCE && pDist < Wistone.MAX_DISTANCE) {
			float diff = (float)(pDist - Wistone.MAX_CLEAR_DISTANCE) / Wistone.NOISE_RANGE;
			return (byte)source.applyNoise(signal, diff);
		} else if(pDist <= Wistone.MAX_CLEAR_DISTANCE) return signal; else return (byte)getDefaultNoise(world);
	}

	public static Set<IDataSource> getSources(World world,
			BlockCoord location, int minF, int maxF) {
		return Wistone.instance.getDPWForWorld(world).getSources(location, minF, maxF);
	}
	public static Set<IDataSource> getSources(World world,
			BlockCoord location, int f) {
		return getSources(world, location, f, f);
	}

	public static int getDefaultNoise(World worldObj) {
		return (Wistone.rand.nextInt() & (worldObj.isRaining() ? 0x7F : 0x3F));
	}
	
	public static float getAntennaStrength(Block block, int count) {
		return 1.0f * (count + 1);
	}
}
