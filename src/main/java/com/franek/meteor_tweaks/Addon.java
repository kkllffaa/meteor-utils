package com.franek.meteor_tweaks;

import com.franek.meteor_tweaks.commands.*;
import com.franek.meteor_tweaks.modules.*;
import com.franek.meteor_tweaks.hud.*;
import meteordevelopment.meteorclient.MeteorAddon;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.hud.HUD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Addon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();
	
	//add custom systems here
	public static final List<System<?>> mySystems = new ArrayList<>() {{
		//add(new ChestMemory());
	}};
	
	@Override
	public void onInitialize() {
		LOG.info("initializing meteor addon");
		
		
		// Commands
		Commands.get().add(new EchestPreview());
		Commands.get().add(new AddWaypoint());
		Commands.get().add(new Disconnect());
		Commands.get().add(new Test());
		
		// Modules
		Modules.get().add(new Strafe());
		Modules.get().add(new ThirdHand());
		Modules.get().add(new NoPortalHitbox());
		Modules.get().add(new Ble());
		
		//HUD
		HUD hud = Modules.get().get(HUD.class);
		hud.elements.add(new BaritoneProcess(hud));
		
	}
}
