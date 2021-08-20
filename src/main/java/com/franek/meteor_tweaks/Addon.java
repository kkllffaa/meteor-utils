package com.franek.meteor_tweaks;

import com.franek.meteor_tweaks.commands.AddWaypoint;
import com.franek.meteor_tweaks.commands.Disconnect;
import com.franek.meteor_tweaks.commands.EchestPreview;
import com.franek.meteor_tweaks.commands.Test;
import com.franek.meteor_tweaks.hud.BaritoneProcess;
import com.franek.meteor_tweaks.hud.OADupeDisplay;
import com.franek.meteor_tweaks.modules.*;
import meteordevelopment.meteorclient.MeteorAddon;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.hud.HUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class Addon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();
	public static final Category CATEGORY = new Category("debil", Items.ACACIA_BOAT.getDefaultStack());
	public static final MinecraftClient mc = MinecraftClient.getInstance();
	
	//add custom systems here
	@SuppressWarnings({"EmptyClassInitializer", "ClassInitializerMayBeStatic"})
	public static final List<System<?>> mySystems = new ArrayList<>() {{
	
	}};
	
	@Override
	public void onInitialize() {
		LOG.info("initializing meteor tweaks");
		
		
		MeteorClient.EVENT_BUS.registerLambdaFactory("com.franek.meteor_tweaks", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
		
		
		// Commands
		Commands.get().add(new EchestPreview());
		Commands.get().add(new AddWaypoint());
		Commands.get().add(new Disconnect());
		Commands.get().add(new Test());
		
		// Modules
		Modules.get().add(new ThirdHand());
		Modules.get().add(new NoPortalHitbox());
		Modules.get().add(new OpenAnarchyAutoDupe());
		Modules.get().add(new AutoBreak());
		Modules.get().add(new AutoFarm());
		
		
		Modules.get().add(new NoPauseOnLostFocus());
		Modules.get().add(new AdvencedTooltips());
		Modules.get().get(NoPauseOnLostFocus.class).init();
		Modules.get().get(AdvencedTooltips.class).init();

		
		//HUD
		HUD hud = Modules.get().get(HUD.class);
		hud.elements.add(new BaritoneProcess(hud));
		hud.elements.add(new OADupeDisplay(hud));
		
	}
	
	@Override
	public void onRegisterCategories() {
		Modules.registerCategory(CATEGORY);
	}
}
