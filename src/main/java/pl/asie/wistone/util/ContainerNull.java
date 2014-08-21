package pl.asie.wistone.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerNull extends Container {
	public ContainerNull() {
		
	}
	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}
}
