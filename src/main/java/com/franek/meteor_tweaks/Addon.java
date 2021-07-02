package com.franek.meteor_tweaks;

import com.franek.meteor_tweaks.commands.*;
import com.franek.meteor_tweaks.hud.*;
import com.franek.meteor_tweaks.modules.*;
import minegame159.meteorclient.MeteorAddon;
import minegame159.meteorclient.systems.commands.Commands;

import minegame159.meteorclient.systems.modules.Modules;
import minegame159.meteorclient.systems.modules.render.hud.HUD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Addon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();
	
	@Override
	public void onInitialize() {
		LOG.info("initializing meteor addon");
		
		// Commands
		Commands.get().add(new EchestPreview());
		Commands.get().add(new AddWaypoint());
		Commands.get().add(new Disconnect());
		Commands.get().add(new BookBot());
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
