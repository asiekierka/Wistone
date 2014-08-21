package pl.asie.wistone.api;

import java.util.HashMap;

import pl.asie.lib.util.BlockMetaPair;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class AntennaManager {
	private static final HashMap<BlockMetaPair, Float> antennaStrength = new HashMap<BlockMetaPair, Float>();
	
	public static float getAntennaStrength(BlockMetaPair bm) {
		return antennaStrength.containsKey(bm) ? antennaStrength.get(bm) : 0.0f;
	}
	
	public static float getAntennaStrength(Block block, int metadata) {
		return getAntennaStrength(new BlockMetaPair(block, metadata));
	}
	
	public static void addAntenna(BlockMetaPair bm, float strength) {
		antennaStrength.put(bm, strength);
	}
	
	public static void addAntenna(Block block, int metadata, float strength) {
		antennaStrength.put(new BlockMetaPair(block, metadata), strength);
	}
	
	public static void addAntenna(String oredictName, float strength) {
		for(ItemStack is: OreDictionary.getOres(oredictName)) {
			if(is != null) {
				if(is.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
					Block b = Block.getBlockFromItem(is.getItem());
					for(int i = 0; i < 16; i++) {
						antennaStrength.put(new BlockMetaPair(b, i), strength);
					}
				} else antennaStrength.put(new BlockMetaPair(is), strength);
			}
		}
	}
}
