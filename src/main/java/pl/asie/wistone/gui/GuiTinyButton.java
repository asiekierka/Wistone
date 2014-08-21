package pl.asie.wistone.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiTinyButton extends GuiButton {
	private ITinyButtonInput input;
	private int textureX, textureY;
	private static final ResourceLocation texture = new ResourceLocation("wistone", "textures/gui/tinybuttons.png");
	
	public GuiTinyButton(int id, int x, int y, int textureX, int textureY) {
		super(id, x, y, 10, 10, "");
		this.textureX = textureX;
		this.textureY = textureY;
	}
	
	public void set(ITinyButtonInput input) {
		this.input = input;
	}
	
	public void drawButton(Minecraft mc, int mx, int my) {
		if(this.visible) {
			GuiWistone displayedScreen = ((GuiWistone)mc.currentScreen);
			
            this.field_146123_n = mx >= this.xPosition && my >= this.yPosition && mx < this.xPosition + this.width && my < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.field_146123_n);
            
			mc.getTextureManager().bindTexture(texture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            this.drawTexturedModalRect(this.xPosition, this.yPosition, (hoverState * 10), 0, 10, 10);
            this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, textureX + (input.getValue() * 6), textureY, 6, 6);
            
            if(this.field_146123_n) { // is hovering
            	displayedScreen.drawTooltip(input.getTooltip(), mx, my);
            }
            this.mouseDragged(mc, mx, my);
		}
	}
}
