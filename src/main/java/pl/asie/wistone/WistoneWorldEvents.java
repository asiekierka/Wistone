package pl.asie.wistone;

import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class WistoneWorldEvents {
	@SubscribeEvent
	public void onTick(TickEvent.WorldTickEvent event) {
		if(event.phase == Phase.START && !event.world.isRemote) {
			Wistone.instance.getDPWForWorld(event.world).tick();
		}
	}
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		if(event.world.isRemote) return;
		
		Wistone.packetWorlds.put(event.world.provider.dimensionId, new WorldDataManager(event.world));
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		if(event.world.isRemote) return;
		
		Wistone.packetWorlds.remove(event.world.provider.dimensionId);
	}
}
