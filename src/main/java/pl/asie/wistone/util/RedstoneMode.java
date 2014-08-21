package pl.asie.wistone.util;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.Loader;
import pl.asie.wistone.gui.ITinyButtonInput;

public enum RedstoneMode implements ITinyButtonInput {
	REGULAR,
	BUNDLED,
	REDNET;
	
	private static final boolean shouldBundled = Loader.isModLoaded("ProjRed|Core") || Loader.isModLoaded("RedLogic");
	//private static final boolean shouldRednet = Loader.isModLoaded("MineFactoryReloaded");
	private static final boolean shouldRednet = false;
	
	@Override
	public List<String> getTooltip() {
		ArrayList<String> l = new ArrayList<String>();
		l.add(this.equals(RedstoneMode.REGULAR) ? "Regular Redstone" : (this.equals(RedstoneMode.BUNDLED) ? "Bundled Redstone" : "RedNet (32-bit)"));
		return l;
	}

	@Override
	public int getValue() {
		return this.ordinal();
	}
	
	private RedstoneMode _next(RedstoneMode a) {
		return a.ordinal() == a.values().length - 1 ? REGULAR : a.values()[a.ordinal() + 1];
	}
	
	@Override
	public ITinyButtonInput next() {
		RedstoneMode next = this;
		while(true) {
			next = _next(next);
			if(next == BUNDLED && !shouldBundled) continue;
			if(next == REDNET && !shouldRednet) continue;
			break;
		}
		return next;
	}
}
