package pl.asie.wistone.block;

import net.minecraft.nbt.NBTTagCompound;
import pl.asie.wistone.util.RedstoneMode;

public abstract class TileBaseTransceiverRedstone extends TileBaseTransceiver {
	public TileBaseTransceiverRedstone(boolean isTransmitter) {
		super(isTransmitter);
	}

	protected RedstoneMode rsMode = RedstoneMode.REGULAR;

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("rm")) {
			this.rsMode = RedstoneMode.values()[tag.getByte("rm")];
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setByte("rm", (byte)rsMode.ordinal());
	}
	
	@Override
	public void writeToRemoteNBT(NBTTagCompound tag) {
		super.writeToRemoteNBT(tag);
		tag.setByte("rm", (byte)rsMode.ordinal());
	}

	public RedstoneMode getRedstoneMode() {
		return this.rsMode;
	}

	public void setRedstoneMode(RedstoneMode mode) {
		if(mode != null) this.rsMode = mode;
	}

	@Override
	public short getFrequencyLength() {
		switch(rsMode) {
		case REGULAR: return 1;
		case BUNDLED: return 16;
		case REDNET: return 32;
		default: return 1;
		}
	}
}
