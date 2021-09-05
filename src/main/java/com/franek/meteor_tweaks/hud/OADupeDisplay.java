package com.franek.meteor_tweaks.hud;

import com.franek.meteor_tweaks.modules.OpenAnarchyAutoDupe;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.hud.HUD;
import meteordevelopment.meteorclient.systems.modules.render.hud.modules.DoubleTextHudElement;
import meteordevelopment.meteorclient.utils.render.color.Color;
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
			if (mod.lag()) {
				rightColor = new Color(1f, 0f, 0f, 1f);
				return "lag:  "+TickRate.INSTANCE.getTimeSinceLastTick();
			}else {
				rightColor = new Color(0f, 1f, 0f, 1f);
				return String.valueOf(mod.getStage());
			}
		}else if (isInEditor()) {
			visible = true;
			rightColor = new Color(0f, 0f, 0f, 1f); return "oa dupe info";
		}else {
			visible = false;
			return "";
		}
	}
}
