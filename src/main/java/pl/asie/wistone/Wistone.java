package pl.asie.wistone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.asie.lib.network.PacketHandler;
import pl.asie.lib.util.EnergyConverter;
import pl.asie.wistone.api.AntennaManager;
import pl.asie.wistone.api.IDataSource;
import pl.asie.wistone.block.BlockRedstoneReceiver;
import pl.asie.wistone.block.BlockRedstoneTransmitter;
import pl.asie.wistone.block.TileRedstoneReceiver;
import pl.asie.wistone.block.TileRedstoneTransmitter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid="wistone", name="Wistone", version="0.0.4", dependencies="required-after:asielib")
public class Wistone {
	public Configuration config;
	public static final Random rand = new Random();
	public static final int MAX_FREQUENCY = 4096;
	public static final int MAX_DISTANCE = 320;
	public static final int MAX_CLEAR_DISTANCE = 64;
	public static final int NOISE_RANGE = MAX_DISTANCE - MAX_CLEAR_DISTANCE;
	public static String ENERGY_DISPLAY_TYPE;
	public static boolean IGNORE_POWER;
	public static HashMap<Integer, WorldDataManager> packetWorlds;
	public Logger log;
	
	public static BlockRedstoneTransmitter rsTransmitter;
	public static BlockRedstoneReceiver rsReceiver;

	public static PacketHandler packet;
	
	@Instance(value="wistone")
	public static Wistone instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		log = LogManager.getLogger("wistone");
		
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		IGNORE_POWER = config.getBoolean("ignorePower", "energy", false, "Set all energy usage to a constant 0 RF/t.");
		ENERGY_DISPLAY_TYPE = config.getString("displayedType", "energy", "RF", "Valid values: [" + StringUtils.join(EnergyConverter.getNames(), ' ') + "]");
		if(!EnergyConverter.isValidEnergyName(ENERGY_DISPLAY_TYPE)) ENERGY_DISPLAY_TYPE = "RF";
		
		GameRegistry.registerBlock(rsTransmitter = new BlockRedstoneTransmitter(), "redstoneTransmitter");
		GameRegistry.registerTileEntity(TileRedstoneTransmitter.class, "redstoneTransmitter");
		
		GameRegistry.registerBlock(rsReceiver = new BlockRedstoneReceiver(), "redstoneReceiver");
		GameRegistry.registerTileEntity(TileRedstoneReceiver.class, "redstoneReceiver");
		
		packet = new PacketHandler("wistone", new NetworkHandlerClient(), new NetworkHandlerServer());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new WistoneGuiHandler());
		
		FMLInterModComms.sendMessage("Waila", "register", "pl.asie.wistone.waila.WailaRegistrarWistone.register");
		
		GameRegistry.addShapedRecipe(new ItemStack(rsTransmitter), "ipi", "grg", "igi", 'i', Items.iron_ingot, 'p', Blocks.piston, 'g', Blocks.glass, 'r', Items.redstone);
		GameRegistry.addShapedRecipe(new ItemStack(rsReceiver), "ipi", "grg", "igi", 'i', Items.iron_ingot, 'p', Blocks.piston, 'g', Blocks.glass, 'r', new ItemStack(Items.dye, 1, 4));
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		WistoneWorldEvents pwm = new WistoneWorldEvents();
		FMLCommonHandler.instance().bus().register(pwm);
		MinecraftForge.EVENT_BUS.register(pwm);
		
		config.save();
		
		// Add antennas
		AntennaManager.addAntenna(Blocks.iron_block, 0, 1.0f);
		AntennaManager.addAntenna(Blocks.gold_block, 0, 1.2f);
		AntennaManager.addAntenna("blockIron", 1.0f);
		AntennaManager.addAntenna("blockGold", 1.2f);
		AntennaManager.addAntenna("blockCopper", 0.8f);
		AntennaManager.addAntenna("blockTin", 0.9f);
	}
	
	@EventHandler
	public void serverStart(FMLServerAboutToStartEvent event) {
		packetWorlds = new HashMap<Integer, WorldDataManager>();
	}
	
	public WorldDataManager getDPWForWorld(World world) {
		if(world.isRemote) {
			log.warn("Something tried to access world " + world.provider.dimensionId + " while world was remote! This is a bug!");
			return null;
		}
		
		if(!packetWorlds.containsKey(world.provider.dimensionId)) {
			Wistone.packetWorlds.put(world.provider.dimensionId, new WorldDataManager(world));
		}
		return packetWorlds.get(world.provider.dimensionId);
	}
	
	public void registerDataSource(World world, IDataSource source) {
		getDPWForWorld(world).register(source);
	}
	
	public void unregisterDataSource(World world, IDataSource source) {
		getDPWForWorld(world).unregister(source);
	}
}
