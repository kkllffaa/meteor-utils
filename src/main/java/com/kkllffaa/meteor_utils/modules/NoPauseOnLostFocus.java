package com.kkllffaa.meteor_utils.modules;

import com.kkllffaa.meteor_utils.Addon;
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
