package pl.asie.wistone;

import pl.asie.wistone.block.TileBaseTransceiver;
import pl.asie.wistone.gui.GuiTransceiver;
import pl.asie.wistone.util.ContainerNull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class WistoneGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return new ContainerNull();
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		switch(ID) {
		case 0: return new GuiTransceiver(world, x, y, z);
		}
		
		return null;
	}

}
