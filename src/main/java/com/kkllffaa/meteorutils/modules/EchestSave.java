package com.kkllffaa.meteorutils.modules;

import com.kkllffaa.meteorutils.Addon;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;

import java.io.File;

public class EchestSave extends Module {
	public EchestSave() {
		super(Addon.CATEGORY, "echest-save", "save and restore items in echest after restart or disconnect");
	}
	
	public final Setting<Boolean> respectdifftentnicks = settings.getDefaultGroup().add(new BoolSetting.Builder()
			.name("respect-diffrent-nicks")
			.description("save and load echest files to diffrent folders based on current nick")
			.defaultValue(true)
			.build()
	);
	
	@Override
	public WWidget getWidget(GuiTheme theme) {
		WButton button = theme.button("clear");
		button.action = loc::delete;
		
		return button;
	}
	
	public static final File loc = new File(MeteorClient.FOLDER, "echests");
	
	
}
