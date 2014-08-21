package pl.asie.wistone.block;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2classic.api.Direction;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.lib.api.tile.IProvidesBattery;
import pl.asie.lib.block.BlockBase;
import pl.asie.lib.block.TileEntityBase;
import pl.asie.lib.tile.BatteryProviderBasic;
import pl.asie.lib.tile.TileMachine;
import pl.asie.lib.util.BlockCoord;
import pl.asie.lib.util.EnergyConverter;
import pl.asie.wistone.Packets;
import pl.asie.wistone.Wistone;
import pl.asie.wistone.api.AntennaManager;
import pl.asie.wistone.api.IDataSource;
import pl.asie.wistone.api.IDataTransceiver;
import pl.asie.wistone.util.EnergyUtil;
import pl.asie.wistone.util.ReceptionUtil;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;

public class TileBaseTransceiver extends TileMachine implements IDataTransceiver, IDataSource, IProvidesBattery {
	private BlockCoord l;
	private int frequency = 1;
	private float powerStrength = 1.0f;
	private float antennaStrength = 1.0f;
	private final boolean isTransmitter;
	
	public double _client_energy = 0.0;
	
	protected boolean isRunning = false;
	protected BatteryProviderBasic battery = new BatteryProviderBasic(10000.0);
	
	public TileBaseTransceiver(boolean isTransmitter) {
		super();
		this.registerBatteryProvider(battery);
		this.isTransmitter = isTransmitter;
	}
	
	// Sending stuff
	
	@Override
	public void validate() {
		super.validate();
		if(worldObj != null && !worldObj.isRemote && isTransmitter) {
			Wistone.instance.registerDataSource(worldObj, this);
		}
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		this.l = null;
		if(worldObj != null && !worldObj.isRemote)
			Wistone.instance.unregisterDataSource(worldObj, this);
	}
	
	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(worldObj != null && !worldObj.isRemote)
			Wistone.instance.unregisterDataSource(worldObj, this);
	}
	
	public boolean canTransmit() {
		return isRunning;
	}
	
	// Base stuff
	
	public BlockCoord getLocation() {
		if(l == null) {
			l = new BlockCoord(xCoord, yCoord, zCoord);
		}
		return l;
	}
	
	protected int ticks = 0;
	
	public void updateEntity() {
		if(worldObj.isRemote) return;
		
		if(ticks == 0)
			worldObj.notifyBlockOfNeighborChange(xCoord, yCoord, zCoord, blockType);
		
		if((ticks & 1) == 0 && _client_energy != this.battery.getEnergyStored()) {
			_client_energy = this.battery.getEnergyStored();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		ticks++;
		
		// Update energy usage
		if(this.battery.getEnergyStored() >= this.getEnergyUsage()) {
			this.battery.extract(-1, this.getEnergyUsage(), false);
			isRunning = true;
		} else isRunning  = false;
		
		if((ticks & 31) == 0 && yCoord < 255) {
			Block ab = worldObj.getBlock(xCoord, yCoord + 1, zCoord);
			int am = worldObj.getBlockMetadata(xCoord, yCoord + 1, zCoord);
			
			if(AntennaManager.getAntennaStrength(ab, am) > 0.01f) {
				float strength = AntennaManager.getAntennaStrength(ab, am);
				int count = 1;
				for(int i = yCoord + 2; i < 256; i++) {
					if(worldObj.getBlock(xCoord, i, zCoord).equals(ab) && worldObj.getBlockMetadata(xCoord, i, zCoord) == am)
						count++;
					else
						break;
				}
				strength *= Math.pow(count + 1, 0.44);
				if(strength < 1.0f) strength = 1.0f;
				if(this.antennaStrength != strength) {
					this.antennaStrength = strength;
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
		}
	}
	
	public int getFrequency() { return frequency; }
	public void setFrequency(int f) {
		if(f < 1) frequency = 1;
		else if(f > (Wistone.MAX_FREQUENCY - this.getFrequencyLength())) frequency = Wistone.MAX_FREQUENCY - this.getFrequencyLength();
		else frequency = f;
	}
	
	@Override
	public float getStrength() {
		return powerStrength * antennaStrength;
	}
	
	public float getPowerStrength() {
		return powerStrength;
	}
	
	public void setPowerStrength(float s) {
		if(s < 0.2f) this.powerStrength = 0.2f;
		else this.powerStrength = s;
	}
	
	public int getEnergyUsage() {
		if(isTransmitter)
			return (int)Math.ceil(EnergyUtil.calculateTransmissionEnergyUsage(this.getFrequency(), powerStrength, this.getFrequencyLength()));
		else
			return (int)Math.ceil(EnergyUtil.calculateReceptionEnergyUsage(this.getFrequency(), powerStrength, this.getFrequencyLength()));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("f")) frequency = tag.getShort("f");
		if(tag.hasKey("st")) powerStrength = tag.getFloat("st");
		// remote only
		if(tag.hasKey("as")) antennaStrength = tag.getFloat("as");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setShort("f", (short)frequency);
		tag.setFloat("st", powerStrength);
	}
	
	@Override
	public int hashCode() { return frequency; }

	@Override
	public short getFrequencyLength() {
		return 1;
	}

	public int applyNoise(int signal, float factor) {
		int noise = ReceptionUtil.getDefaultNoise(worldObj);
		return (int)Math.round((signal * (1.0f - factor)) + (noise * factor));
	}

	// Used by both the GUI and RF APIs
	
	@Override
	public void writeToRemoteNBT(NBTTagCompound tag) {
		super.writeToRemoteNBT(tag);
		tag.setShort("f", (short)frequency);
		tag.setFloat("st", powerStrength);
		tag.setFloat("as", antennaStrength);
		tag.setFloat("ce", (float)this.battery.getEnergyStored());
	}
	
	@Override
	public void readFromRemoteNBT(NBTTagCompound tag) {
		super.readFromRemoteNBT(tag);
		this.antennaStrength = tag.getFloat("as");
		_client_energy = tag.getFloat("ce");
	}

	@Override
	public byte[] getData() {
		return new byte[this.getFrequencyLength()];
	}
	
	// OpenComputers
	
	@Callback(direct = true)
	@Optional.Method(modid = "OpenComputers")
	public Object[] getFrequency(Context c, Arguments a) {
		return new Object[]{this.getFrequency()};
	}
	
	@Callback(direct = true)
	@Optional.Method(modid = "OpenComputers")
	public Object[] getTotalStrength(Context c, Arguments a) {
		return new Object[]{this.getStrength()};
	}
	
	@Callback(direct = true)
	@Optional.Method(modid = "OpenComputers")
	public Object[] getPowerStrength(Context c, Arguments a) {
		return new Object[]{this.getPowerStrength()};
	}

	@Callback(direct = true)
	@Optional.Method(modid = "OpenComputers")
	public Object[] getEnergyUsage(Context c, Arguments a) {
		return new Object[]{this.getEnergyUsage()};
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] setPowerStrength(Context c, Arguments a) {
		if(a.count() == 1 && a.isDouble(0)) this.setPowerStrength((float)a.checkDouble(0));
		return this.getPowerStrength(c, a);
	}
	
	@Callback
	@Optional.Method(modid = "OpenComputers")
	public Object[] setFrequency(Context c, Arguments a) {
		if(a.count() == 1 && a.isInteger(0)) this.setFrequency(a.checkInteger(0));
		return this.getFrequency(c, a);
	}
}
