package pl.asie.wistone.api;

public interface IDataSource extends IDataTransceiver {
	public byte[] getData();
	public boolean canTransmit();
	public int applyNoise(int in, float factor);
}
