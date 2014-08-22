package pl.asie.wistone.block;

import java.util.List;
import java.util.Set;

import li.cil.oc.api.network.SimpleComponent;
import mods.immibis.redlogic.api.wiring.IBundledEmitter;
import mods.immibis.redlogic.api.wiring.IConnectable;
import mods.immibis.redlogic.api.wiring.IWire;
import mrtjp.projectred.api.IBundledTile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.lib.api.tile.IBundledRedstoneProvider;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.lib.util.BlockCoord;
import pl.asie.wistone.Wistone;
import pl.asie.wistone.api.IDataSource;
import pl.asie.wistone.util.ReceptionUtil;
import pl.asie.wistone.util.RedstoneMode;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")
})
public class TileRedstoneReceiver extends TileBaseTransceiverRedstone implements IBundledRedstoneProvider, SimpleComponent {
	public TileRedstoneReceiver() {
		super(false);
		this.registerBundledRedstone(this);
	}
	
	private short rsPrev = -1;
	private short rs;
	private byte[] bundledRs = new byte[16];
	private boolean notify = true;
	
	@Override
	public int requestCurrentRedstoneValue(int side) {
		return rs >> 4;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		if(this.worldObj.isRemote) return;
		
		if(rsMode.equals(RedstoneMode.REGULAR)) {
			if(isRunning)
				rs = (short)(((short)ReceptionUtil.getSignal(worldObj, getLocation(), 
						ReceptionUtil.getSources(worldObj, getLocation(), getFrequency()),
						this.getFrequency(), 1.0f)) & 0xFF);
			else
				rs = 0;
			
			if(rsPrev != rs) {
				rsPrev = rs;
				notify = true;
			}
		} else if(rsMode.equals(RedstoneMode.BUNDLED)) {
			if(isRunning) {
				Set<IDataSource> sources = ReceptionUtil.getSources(worldObj, getLocation(), getFrequency(), getFrequency() + 15);
				for(int i = 0; i < 16; i++) {
					byte nrs = (byte)(((short)ReceptionUtil.getSignal(worldObj, getLocation(), sources,
							this.getFrequency() + i, 1.0f)));
					
					if(bundledRs[i] != nrs) notify = true;
					bundledRs[i] = nrs;
				}
			} else {
				for(int i = 0; i < 16; i++) {
					if(bundledRs[i] != 0) notify = true;
					bundledRs[i] = 0;
				}
			}
		}
		
		if(notify) {
			this.worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, this.blockType);
			notify = false;
		}
	}
	
	@Override
	public void setRedstoneMode(RedstoneMode mode) {
		super.setRedstoneMode(mode);
		if(worldObj.isRemote) return;
		
		if(!rsMode.equals(RedstoneMode.REGULAR)) { rs = 0; rsPrev = -1; }
		if(!rsMode.equals(RedstoneMode.BUNDLED)) {
			for(int i = 0; i < 16; i++) {
				bundledRs[i] = 0;
			}
		}
		this.worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, this.blockType);
	}
	
	@Override
	public String getComponentName() {
		return "redstone_receiver";
	}

	@Override
	public boolean canBundledConnectTo(int arg0, int arg1) {
		return true;
	}

	@Override
	public byte[] getBundledOutput(int arg0, int arg1) {
		return bundledRs;
	}

	@Override
	public void onBundledInputChange(int arg0, int arg1, byte[] arg2) { }
}
