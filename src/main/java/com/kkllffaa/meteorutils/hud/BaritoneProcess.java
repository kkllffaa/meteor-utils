package com.kkllffaa.meteorutils.hud;

import baritone.api.BaritoneAPI;
import baritone.api.process.IBaritoneProcess;
import meteordevelopment.meteorclient.systems.hud.HUD;
import meteordevelopment.meteorclient.systems.hud.modules.DoubleTextHudElement;

public class BaritoneProcess extends DoubleTextHudElement {
	public BaritoneProcess(HUD hud) {
		super(hud, "BaritoneProcess", "Displays what baritone is doing.", "BaritoneProcess: ");
	}
	
	@Override
	protected String getRight() {
		IBaritoneProcess process = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingControlManager().mostRecentInControl().orElse(null);
		
		if (process == null) return "";
		
		return process.displayName();
		
		
	}
}
