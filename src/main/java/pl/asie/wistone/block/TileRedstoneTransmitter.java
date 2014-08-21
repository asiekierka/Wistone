package pl.asie.wistone.block;

import li.cil.oc.api.network.SimpleComponent;
import mods.immibis.redlogic.api.wiring.IBundledEmitter;
import mods.immibis.redlogic.api.wiring.IBundledUpdatable;
import mods.immibis.redlogic.api.wiring.IConnectable;
import mods.immibis.redlogic.api.wiring.IWire;
import mrtjp.projectred.api.IBundledTile;
import mrtjp.projectred.api.ProjectRedAPI;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.lib.api.provider.IBundledRedstoneProvider;
import pl.asie.lib.api.tile.IProvidesBundledRedstone;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.lib.util.BlockCoord;
import pl.asie.wistone.Wistone;
import pl.asie.wistone.api.IDataSource;
import pl.asie.wistone.util.RedstoneMode;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import pl.asie.lib.block.BlockBase;

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")
})
public class TileRedstoneTransmitter extends TileBaseTransceiverRedstone implements IDataSource, IBundledRedstoneProvider, IProvidesBundledRedstone, SimpleComponent {
	public TileRedstoneTransmitter() {
		super(true);
		this.registerBundledRedstoneProvider(this);
	}

	private int rsIn = -1;
	private byte[] bundledIn = new byte[16];
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(worldObj.isRemote) return;
	}
	
	@Override
	public void onRedstoneSignal(int s) {
		super.onRedstoneSignal(s);
		if(worldObj == null || worldObj.isRemote) return;
		
		this.rsIn = s;
		if(this.rsIn > 15) this.rsIn = 15;
		if(this.rsIn < 0) this.rsIn = 0;
		this.markDirty();
	}
	
	@Override
	public byte[] getData() {
		if(this.rsMode.equals(RedstoneMode.REGULAR)) {
			if(rsIn < 0)
				return new byte[]{0};
			else
				return new byte[]{(byte)(rsIn * 17)};
		} else if(this.rsMode.equals(RedstoneMode.BUNDLED)) {
			return bundledIn;
		} else {
			Wistone.instance.log.warn("Redstone mode "+ this.rsMode.name() + " is not handled properly! (getData())");
			return new byte[this.getFrequencyLength()];
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("rv")) this.rsIn = tag.getByte("rv");
		if(tag.hasKey("brv")) this.bundledIn = tag.getByteArray("brv");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(this.rsMode.equals(RedstoneMode.REGULAR) && rsIn != 0)
			tag.setByte("rv", (byte)rsIn);
		else if(this.rsMode.equals(RedstoneMode.BUNDLED) && bundledIn != null && bundledIn.length == 16)
			tag.setByteArray("brv", bundledIn);
	}

	@Override
	public String getComponentName() {
		return "redstone_transmitter";
	}

	@Override
	public boolean canConnectTo(int arg0, int arg1) {
		return true;
	}

	@Override
	public byte[] getBundledOutput(int arg0, int arg1) {
		return null;
	}

	@Override
	public void onBundledInputChange(int arg0, int arg1, byte[] arg2) {
		if(arg2 != null) {
			this.bundledIn = arg2;
			this.markDirty();
		}
	}
}
