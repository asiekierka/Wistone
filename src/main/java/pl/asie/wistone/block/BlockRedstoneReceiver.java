package pl.asie.wistone.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.lib.block.BlockBase;
import pl.asie.wistone.Wistone;

public class BlockRedstoneReceiver extends BlockWistone {
	public BlockRedstoneReceiver() {
		super("rr", "rr_front", "rr_back");
		this.setBlockName("wistone.redstoneReceiver");
		this.setGuiID(0);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileRedstoneReceiver();
	}
	
	@Override
	public boolean emitsRedstone(IBlockAccess world, int x, int y, int z, int side) {
		return true;
	}
}
