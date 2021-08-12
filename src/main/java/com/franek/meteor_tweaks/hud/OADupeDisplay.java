package com.franek.meteor_tweaks.hud;

import com.franek.meteor_tweaks.modules.OpenAnarchyAutoDupe;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.hud.HUD;
import meteordevelopment.meteorclient.systems.modules.render.hud.modules.DoubleTextHudElement;
import meteordevelopment.meteorclient.utils.world.TickRate;

public class OADupeDisplay extends DoubleTextHudElement {
	public OADupeDisplay(HUD hud) {
		super(hud, "OAdupe diplay", "duplays oa dupe module status", "stage: ", false);
	}
	
	@Override
	protected String getRight() {
		OpenAnarchyAutoDupe mod = Modules.get().get(OpenAnarchyAutoDupe.class);
		
		if (mod.isActive()) {
			visible = true;
			return mod.lag() ? "lag:  "+TickRate.INSTANCE.getTimeSinceLastTick() : String.valueOf(mod.getStage());
		}else {
			visible = false;
			return "";
		}
	}
}
