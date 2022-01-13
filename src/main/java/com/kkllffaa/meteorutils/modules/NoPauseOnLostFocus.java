package com.kkllffaa.meteorutils.modules;

import com.kkllffaa.meteorutils.Addon;
import meteordevelopment.meteorclient.systems.modules.Module;

public class NoPauseOnLostFocus extends Module {
 
	public NoPauseOnLostFocus() {
		super(Addon.CATEGORY, "no-pause-on-lost-focus", "allow alt+tab without pause");
		mc.options.pauseOnLostFocus = !isActive();
	}
	
	@Override
	public void onActivate() {
		mc.options.pauseOnLostFocus = false;
	}
	
	@Override
	public void onDeactivate() {
		mc.options.pauseOnLostFocus = true;
	}
	
}
