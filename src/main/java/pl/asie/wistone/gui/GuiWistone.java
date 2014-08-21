package pl.asie.wistone.gui;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;

public abstract class GuiWistone extends GuiScreen {
	public void drawTooltip(List list, int x, int y) {
		this.drawHoveringText(list, x, y, this.fontRendererObj);
	}
	
	@Override
    public boolean doesGuiPauseGame() { return false; }
    
	@Override
    public void updateScreen() {
        super.updateScreen();

        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead)
            this.mc.thePlayer.closeScreen();
    }
}
