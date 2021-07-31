package com.franek.meteor_tweaks;

import com.franek.meteor_tweaks.commands.*;
import com.franek.meteor_tweaks.modules.*;
import com.franek.meteor_tweaks.hud.*;
import meteordevelopment.meteorclient.MeteorAddon;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.hud.HUD;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class Addon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();
	public static final MinecraftClient mc = MinecraftClient.getInstance();
	
	//add custom systems here
	@SuppressWarnings({"EmptyClassInitializer", "ClassInitializerMayBeStatic"})
	public static final List<System<?>> mySystems = new ArrayList<>() {{
	
	}};
	
	@Override
	public void onInitialize() {
		LOG.info("initializing meteor addon");
		
		
		MeteorClient.EVENT_BUS.registerLambdaFactory("com.franek.meteor_tweaks", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
		
		
		// Commands
		Commands.get().add(new EchestPreview());
		Commands.get().add(new AddWaypoint());
		Commands.get().add(new Disconnect());
		
		// Modules
		Modules.get().add(new ThirdHand());
		Modules.get().add(new NoPortalHitbox());
		
		
		Modules.get().add(new NoPauseOnLostFocus());
		Modules.get().get(NoPauseOnLostFocus.class).init();
		Modules.get().add(new AdvencedTooltips());
		Modules.get().get(AdvencedTooltips.class).init();

		
		//HUD
		HUD hud = Modules.get().get(HUD.class);
		hud.elements.add(new BaritoneProcess(hud));
		
	}
}
