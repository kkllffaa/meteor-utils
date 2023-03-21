package com.kkllffaa.meteorutils;

import com.kkllffaa.meteorutils.commands.AddWaypoint;
import com.kkllffaa.meteorutils.commands.Disconnect;
import com.kkllffaa.meteorutils.commands.EchestPreview;
import com.kkllffaa.meteorutils.hud.BaritoneProcess;
import com.kkllffaa.meteorutils.hud.ElytraDurability;
import com.kkllffaa.meteorutils.hud.OADupeDisplay;
import com.kkllffaa.meteorutils.modules.*;
import com.kkllffaa.meteorutils.tabs.WaypointsTab;
import com.kkllffaa.meteorutils.widgets.ProfilesWidget;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Addon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();
	public static final Category CATEGORY = new Category("meteor-utils", Items.ACACIA_BOAT.getDefaultStack());
	public static final HudGroup HUD_GROUP = new HudGroup("meteor-utils");
	
	
	
	public static final List<System<?>> mySystems = new ArrayList<>(); //add custom systems here
	public static final List<BiConsumer<GuiTheme, WContainer>> myWidgets = new ArrayList<>(); //add custom widgets in modules hud here
	
	static {
		myWidgets.add(ProfilesWidget::create);
	}
	
	@Override
	public void onInitialize() {
		LOG.info("initializing meteor utils addon");

		
		//MeteorClient.EVENT_BUS.registerLambdaFactory("com.kkllffaa.meteorutils", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
		
		
		// Commands
		Commands.get().add(new EchestPreview());
		Commands.get().add(new AddWaypoint());
		Commands.get().add(new Disconnect());
		//Commands.get().add(new Test());
		
		// Modules
		Modules.get().add(new ThirdHand());
		Modules.get().add(new NoPortalHitbox());
		Modules.get().add(new OpenAnarchyAutoDupe());
		Modules.get().add(new AutoFarm());
		Modules.get().add(new BetterBookBot());
		Modules.get().add(new NoPauseOnLostFocus());
		Modules.get().add(new AdvencedTooltips());
		Modules.get().add(new EchestSave());
		Modules.get().add(new ShulkerDupe());

		
		//HUD
		Hud.get().register(BaritoneProcess.INFO);
		Hud.get().register(OADupeDisplay.INFO);
		Hud.get().register(ElytraDurability.INFO);
		
		//Tabs
		Tabs.add(new WaypointsTab());
		
		
	}
	
	@Override
	public void onRegisterCategories() {
		Modules.registerCategory(CATEGORY);
	}
	
	@Override
	public String getPackage() {
		return "com.kkllffaa.meteorutils";
	}
}
