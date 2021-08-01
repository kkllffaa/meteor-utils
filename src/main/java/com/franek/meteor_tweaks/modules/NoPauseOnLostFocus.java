package com.franek.meteor_tweaks.modules;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;

public class NoPauseOnLostFocus extends Module {
 
	public NoPauseOnLostFocus() {
		super(Categories.Misc, "no pause on lost focus", "allow alt+tab without pause");
	}
	
	@Override
	public void onActivate() {
		MinecraftClient.getInstance().options.pauseOnLostFocus = false;
	}
	
	@Override
	public void onDeactivate() {
		MinecraftClient.getInstance().options.pauseOnLostFocus = true;
	}
	
	public void init() {
		mc.options.pauseOnLostFocus = !isActive();
	}
	
}
