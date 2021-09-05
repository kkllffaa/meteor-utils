package com.franek.meteor_tweaks.modules;

import com.franek.meteor_tweaks.Addon;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;

public class NoPauseOnLostFocus extends Module {
 
	public NoPauseOnLostFocus() {
		super(Addon.CATEGORY, "no-pause-on-lost-focus", "allow alt+tab without pause");
		mc.options.pauseOnLostFocus = !isActive();
	}
	
	@Override
	public void onActivate() {
		Utils.mc.options.pauseOnLostFocus = false;
	}
	
	@Override
	public void onDeactivate() {
		Utils.mc.options.pauseOnLostFocus = true;
	}
	
}
