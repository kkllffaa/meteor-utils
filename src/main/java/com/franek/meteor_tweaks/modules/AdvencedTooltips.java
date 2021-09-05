package com.franek.meteor_tweaks.modules;

import com.franek.meteor_tweaks.Addon;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;

public class AdvencedTooltips extends Module {
 
	public AdvencedTooltips() {
		super(Addon.CATEGORY, "advenced-tooltips", "shows durability, name etc. in tooltip");
		mc.options.advancedItemTooltips = isActive();
	}
	
	@Override
	public void onActivate() {
		Utils.mc.options.advancedItemTooltips = true;
	}
	
	@Override
	public void onDeactivate() {
		Utils.mc.options.advancedItemTooltips = false;
	}
	
}
