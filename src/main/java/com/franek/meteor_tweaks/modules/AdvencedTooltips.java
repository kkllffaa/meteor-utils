package com.franek.meteor_tweaks.modules;

import com.franek.meteor_tweaks.Addon;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;

public class AdvencedTooltips extends Module {
 
	public AdvencedTooltips() {
		super(Addon.CATEGORY, "advenced tooltips", "");
	}
	
	@Override
	public void onActivate() {
		MinecraftClient.getInstance().options.advancedItemTooltips = true;
	}
	
	@Override
	public void onDeactivate() {
		MinecraftClient.getInstance().options.advancedItemTooltips = false;
	}
	
	public void init() {
		mc.options.advancedItemTooltips = isActive();
	}
	
}
