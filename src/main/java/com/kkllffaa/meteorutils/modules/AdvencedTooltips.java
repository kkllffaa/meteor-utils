package com.kkllffaa.meteorutils.modules;

import com.kkllffaa.meteorutils.Addon;
import meteordevelopment.meteorclient.systems.modules.Module;

public class AdvencedTooltips extends Module {
 
	public AdvencedTooltips() {
		super(Addon.CATEGORY, "advenced-tooltips", "shows durability, name etc. in tooltip");
		mc.options.advancedItemTooltips = isActive();
	}
	
	@Override
	public void onActivate() {
		mc.options.advancedItemTooltips = true;
	}
	
	@Override
	public void onDeactivate() {
		mc.options.advancedItemTooltips = false;
	}
	
}
