package com.kkllffaa.meteorutils.hud;

import com.kkllffaa.meteorutils.Addon;
import com.kkllffaa.meteorutils.modules.OpenAnarchyAutoDupe;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.TickRate;

public class OADupeDisplay extends HudElement {
	public static final HudElementInfo<OADupeDisplay> INFO = new HudElementInfo<>(Addon.HUD_GROUP, "OAdupe diplay", "duplays oa dupe module status.", OADupeDisplay::new);

	//super(hud, "OAdupe diplay", "duplays oa dupe module status", "stage: ", false);
	
	public OADupeDisplay() {
		super(INFO);
	}
	
	@Override
	public void render(HudRenderer renderer) {
		OpenAnarchyAutoDupe mod = Modules.get().get(OpenAnarchyAutoDupe.class);
		if (mod.isActive()) {
			
			
			
			
			
			
			if (mod.lag()) {
				String display = "lag:  "+TickRate.INSTANCE.getTimeSinceLastTick();
				
				setSize(renderer.textWidth(display, true), renderer.textHeight(true));
				renderer.text(display, x, y, Color.RED, true);
			}else {
				String display = "stage: " + mod.getStage();
				
				setSize(renderer.textWidth(display, true), renderer.textHeight(true));
				renderer.text(display, x, y, Color.GREEN, true);
			}
			
			
			
		} else if (isInEditor()) {
			
			setSize(renderer.textWidth("oa dupe info", true), renderer.textHeight(true));
			
			renderer.text("oa dupe info", x, y, Color.WHITE, true);
			
		}
		
	}
}
