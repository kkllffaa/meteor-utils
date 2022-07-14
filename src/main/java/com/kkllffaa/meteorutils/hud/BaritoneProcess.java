package com.kkllffaa.meteorutils.hud;

import baritone.api.BaritoneAPI;
import baritone.api.process.IBaritoneProcess;
import com.kkllffaa.meteorutils.Addon;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class BaritoneProcess extends HudElement {
	public static final HudElementInfo<BaritoneProcess> INFO = new HudElementInfo<>(Addon.HUD_GROUP, "BaritoneProcess", "Displays what baritone is doing.", BaritoneProcess::new);
	
	public BaritoneProcess() {
		super(INFO);
	}
	
	@Override
	public void render(HudRenderer renderer) {
		IBaritoneProcess process = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingControlManager().mostRecentInControl().orElse(null);
		String display = isInEditor() ? "BaritoneProcess" : "";
		if (process != null)
			display = "BaritoneProcess: " + process.displayName();
		
		
		
		setSize(renderer.textWidth(display, true), renderer.textHeight(true));
		
		renderer.text(display, x, y, Color.WHITE, true);
	}

}
