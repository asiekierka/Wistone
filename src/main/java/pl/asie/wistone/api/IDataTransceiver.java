package pl.asie.wistone.api;

import pl.asie.lib.util.BlockCoord;

public interface IDataTransceiver {
	public float getStrength();
	public int getFrequency();
	public BlockCoord getLocation();
	public short getFrequencyLength();
}
