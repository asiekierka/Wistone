package pl.asie.wistone.gui;

import java.util.List;

public interface ITinyButtonInput {
	public List<String> getTooltip();
	public int getValue();
	public ITinyButtonInput next();
}
