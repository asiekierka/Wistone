package pl.asie.wistone.block;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pl.asie.lib.block.BlockBase;
import pl.asie.wistone.Wistone;

public class BlockRedstoneTransmitter extends BlockWistone {
	public BlockRedstoneTransmitter() {
		super("rt", "rt_front", "rt_back");
		this.setBlockName("wistone.redstoneTransmitter");
		this.setGuiID(0);
	}

	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1) {
		return new TileRedstoneTransmitter();
	}
	
	@Override
	public boolean receivesRedstone(IBlockAccess world, int x, int y, int z) {
		return true;
	}
}
