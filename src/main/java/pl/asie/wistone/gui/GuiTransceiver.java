package pl.asie.wistone.gui;

import java.text.NumberFormat;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import pl.asie.lib.block.ContainerBase;
import pl.asie.lib.gui.GuiBase;
import pl.asie.lib.util.EnergyConverter;
import pl.asie.wistone.NetworkHandlerServer;
import pl.asie.wistone.Packets;
import pl.asie.wistone.Wistone;
import pl.asie.wistone.block.TileBaseTransceiverRedstone;
import pl.asie.wistone.block.TileBaseTransceiver;
import pl.asie.wistone.util.RedstoneMode;

public class GuiTransceiver extends GuiWistone {
	private final ResourceLocation texture = new ResourceLocation("wistone", "textures/gui/transmitter.png");
	private final int xSize, ySize;
	private int top, left;
	private final TileBaseTransceiver tile;
	private final World world;
	private final int x, y, z;
	private GuiTinyButton redstoneButton;
	
	public static final NumberFormat ENERGY_FORMAT;
	
	static {
		ENERGY_FORMAT = NumberFormat.getInstance();
		ENERGY_FORMAT.setMinimumFractionDigits(0);
		ENERGY_FORMAT.setMaximumFractionDigits(1);
	}
	
	public GuiTransceiver(World world, int x, int y, int z) {
		super();
		this.xSize = 146;
		this.ySize = 85;
		this.tile = (TileBaseTransceiver)world.getTileEntity(x, y, z);
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		try {
			Wistone.packet.sendToServer(
					Wistone.packet.create(Packets.C2S_TRANSCEIVER_GUI_UPDATE)
						.writeInt(world.provider.dimensionId)
						.writeInt(x)
						.writeInt(y)
						.writeInt(z)
						.writeByte((byte)button.id)
			);
			NetworkHandlerServer.transceiverGuiUpdate(tile, button.id);
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	@Override
	public void initGui() {
		super.initGui();

		this.top = (this.height - this.ySize) / 2;
		this.left = (this.width - this.xSize) / 2;
		
		// Buttons
		this.buttonList.add(new GuiButton(0, this.left + 98, this.top + 8, 20, 20, "-"));
		this.buttonList.add(new GuiButton(1, this.left + 120, this.top + 8, 20, 20, "+"));
		this.buttonList.add(new GuiButton(2, this.left + 98, this.top + 8 + 22, 20, 20, "-"));
		this.buttonList.add(new GuiButton(3, this.left + 120, this.top + 8 + 22, 20, 20, "+"));
		
		int tinyButtonX = this.left + 7;
		if(tile instanceof TileBaseTransceiverRedstone) {
			this.buttonList.add(this.redstoneButton = new GuiTinyButton(4, tinyButtonX, this.top + 69, 0, 10));
			tinyButtonX += 12;
		}
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
        if(redstoneButton != null) redstoneButton.set(((TileBaseTransceiverRedstone)tile).getRedstoneMode());
	}
	
	@Override
    public void drawScreen(int mx, int my, float wat) {
        this.drawDefaultBackground();
		
		this.mc.getTextureManager().bindTexture(texture);
		this.drawTexturedModalRect(this.left, this.top, 0, 0, this.xSize, this.ySize);
		// Energy usage
		int eU = (int)Math.round(tile._client_energy * 59 / tile.getBatteryProvider().getMaxEnergyStored());
		this.drawTexturedModalRect(this.left + 7, this.top + 7, 0, 197 - (59 - eU), 9, 59);
		
		// Labels
		String energyStoredS = ENERGY_FORMAT.format(EnergyConverter.convertEnergy(tile._client_energy, "RF", Wistone.ENERGY_DISPLAY_TYPE)) + " " + Wistone.ENERGY_DISPLAY_TYPE;
		String energyUsageS = ENERGY_FORMAT.format(EnergyConverter.convertEnergy(tile.getEnergyUsage(), "RF", Wistone.ENERGY_DISPLAY_TYPE)) + " "+Wistone.ENERGY_DISPLAY_TYPE+"/t";
		
		this.drawString(fontRendererObj, I18n.format("gui.wistone.freq.short") + ": " + tile.getFrequency() + " Dd", this.left + 22, this.top + 15, 0xFFFFFF);
		this.drawString(fontRendererObj, I18n.format("gui.wistone.strength") + ": " +  String.format("%.1f", tile.getStrength()) + "x", this.left + 22, this.top + 15 + 22, 0xFFFFFF);
		this.drawCenteredString(fontRendererObj, energyStoredS, this.left + 50, this.top + 56, 0xFFFFFF);
		this.drawCenteredString(fontRendererObj, energyUsageS, this.left + 113, this.top + 56,
				tile.getEnergyStored(ForgeDirection.UNKNOWN) < tile.getEnergyUsage() ? 0xEF6060 : 0x70EF70);

		super.drawScreen(mx, my, wat);
		
		
    }
}
