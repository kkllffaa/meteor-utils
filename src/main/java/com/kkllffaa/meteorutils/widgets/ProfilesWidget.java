package com.kkllffaa.meteorutils.widgets;

import com.kkllffaa.meteorutils.utils.MyUtils;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.systems.profiles.Profile;
import meteordevelopment.meteorclient.systems.profiles.Profiles;
import net.minecraft.item.Items;

public class ProfilesWidget {
	
	
	public static void create(GuiTheme theme, WContainer container) {
		WWindow window = theme.window("profiles");
		window.id = "profiles";
		
		if (theme.categoryIcons()) {
			window.beforeHeaderInit = wContainer -> wContainer.add(theme.item(Items.ENDER_CHEST.getDefaultStack())).pad(2);
		}
		
		container.add(window);
		window.view.hasScrollBar = false;
		
		WTable profiles = window.add(theme.table()).widget();
		
		createprofiles(profiles, theme);
		
	}
	
	protected static void createprofiles(WTable table, GuiTheme theme) {
		// Profiles
		for (Profile profile : Profiles.get()) {
			//name and tooltip
			table.add(theme.label(profile.name)).expandCellX().widget().tooltip =
							MyUtils.printif("accounts ",	profile.accounts)+
							MyUtils.printif("config ",	profile.config)+
							MyUtils.printif("friends ",	profile.friends)+
							MyUtils.printif("macros ",	profile.macros)+
							MyUtils.printif("modules ",	profile.modules)+
							MyUtils.printif("waypoints",	profile.waypoints);
			
			//save
			table.add(theme.button("Save")).widget().action = profile::save;
			
			//load
			table.add(theme.button("Load")).widget().action = profile::load;
			
			//delete
			table.add(theme.minus()).widget().action = () -> {
				Profiles.get().remove(profile);
				table.clear();
				createprofiles(table, theme);
			};
			
			table.row();
		}
	}
}
