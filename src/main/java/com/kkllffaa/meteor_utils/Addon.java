package com.kkllffaa.meteor_utils;

import com.kkllffaa.meteor_utils.commands.AddWaypoint;
import com.kkllffaa.meteor_utils.commands.Disconnect;
import com.kkllffaa.meteor_utils.commands.EchestPreview;
import com.kkllffaa.meteor_utils.commands.Test;
import com.kkllffaa.meteor_utils.hud.BaritoneProcess;
import com.kkllffaa.meteor_utils.hud.ElytraDurability;
import com.kkllffaa.meteor_utils.hud.OADupeDisplay;
import com.kkllffaa.meteor_utils.modules.*;
import com.kkllffaa.meteor_utils.tabs.WaypointsTab;
import com.kkllffaa.meteor_utils.widgets.ProfilesWidget;
import meteordevelopment.meteorclient.MeteorAddon;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.hud.HUD;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Addon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();
	public static final Category CATEGORY = new Category("debil", Items.ACACIA_BOAT.getDefaultStack());
	
	
	@SuppressWarnings({"EmptyClassInitializer", "ClassInitializerMayBeStatic"})
	public static final List<System<?>> mySystems = new ArrayList<>() {{
		//add custom systems here
	}};
	
	public static final List<BiConsumer<GuiTheme, WContainer>> myWidgets = new ArrayList<>() {{
		//add custom widgets in modules hud here
		add(ProfilesWidget::create);
	}};
	
	@Override
	public void onInitialize() {
		LOG.info("initializing meteor tweaks");
		
		
		MeteorClient.EVENT_BUS.registerLambdaFactory("com.franek.meteor_utils", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
		
		
		// Commands
		Commands.get().add(new EchestPreview());
		Commands.get().add(new AddWaypoint());
		Commands.get().add(new Disconnect());
		Commands.get().add(new Test());
		
		// Modules
		Modules.get().add(new ThirdHand());
		Modules.get().add(new NoPortalHitbox());
		Modules.get().add(new OpenAnarchyAutoDupe());
		Modules.get().add(new AutoFarm());
		Modules.get().add(new BetterBookBot());
		Modules.get().add(new NoPauseOnLostFocus());
		Modules.get().add(new AdvencedTooltips());

		
		//HUD
		HUD hud = Modules.get().get(HUD.class);
		hud.elements.add(new BaritoneProcess(hud));
		hud.elements.add(new OADupeDisplay(hud));
		hud.elements.add(new ElytraDurability(hud));
		
		//Tabs
		Tabs.add(new WaypointsTab());
	}
	
	@Override
	public void onRegisterCategories() {
		Modules.registerCategory(CATEGORY);
	}
}
