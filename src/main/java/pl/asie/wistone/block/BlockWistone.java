package pl.asie.wistone.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import pl.asie.lib.block.BlockBase;
import pl.asie.wistone.Wistone;

public abstract class BlockWistone extends BlockBase {
	protected IIcon[] icons = new IIcon[6];
	private String sname, fname, bname;
	
	public BlockWistone(String sname, String fname, String bname) {
		super(Material.circuits, Wistone.instance);
		this.setCreativeTab(CreativeTabs.tabRedstone);
		this.setRotation(Rotation.FOUR);
		this.sname = sname;
		this.fname = fname;
		this.bname = bname;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister r) { 
		icons[0] = r.registerIcon("wistone:generic_bottom");
		icons[1] = r.registerIcon("wistone:generic_top");
		icons[2] = r.registerIcon("wistone:" + fname);
		icons[3] = r.registerIcon("wistone:" + bname);
		icons[4] = r.registerIcon("wistone:" + sname);
		icons[5] = icons[4];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getAbsoluteIcon(int side, int metadata) {
		return icons[side % 6];
	}
}
