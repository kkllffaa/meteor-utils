package com.franek.meteor_tweaks.utils;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.item.Item;

import java.util.Optional;

public class MyItemUtils {
	
	
	public static boolean switchtoitem(Item item, @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Module> thismodule) {
		FindItemResult result = InvUtils.find(item);
		
		if (result.found()) {
			if (result.isHotbar()) {
				InvUtils.swap(result.getSlot());
				return true;
			}else {
				FindItemResult empty = InvUtils.findEmpty();
				if (empty.found() && empty.isHotbar()) {
					InvUtils.move().from(result.getSlot()).toHotbar(empty.getSlot());
					InvUtils.swap(empty.getSlot());
					return true;
				}else {	thismodule.ifPresent(module -> module.info("no space in hotbar")); return false; }
			}
		}else { thismodule.ifPresent(module -> module.info("no " + item.toString() + " found")); return false; }
	}
	
	
	public static boolean switchtoitem(Item item) {
		return switchtoitem(item, Optional.empty());
	}
}
