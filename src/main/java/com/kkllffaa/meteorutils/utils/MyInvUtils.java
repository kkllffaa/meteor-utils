package com.kkllffaa.meteorutils.utils;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.world.item.Item;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MyInvUtils {

	public static boolean switchtoitem(FindItemResult item, boolean quickmove, boolean force, Module thismodule,
			Item iteminmessage) {
		if (!Utils.canUpdate())
			return false;

		if (item.found()) {
			if (item.isHotbar()) {
				InvUtils.swap(item.slot(), false);
				return true;
			} else {
				FindItemResult empty = InvUtils.findEmpty();
				if (empty.found() && empty.isHotbar()) {
					if (quickmove && !(mc.screen instanceof ContainerScreen))
						InvUtils.shiftClick().from(item.slot()).toHotbar(empty.slot());
					else
						InvUtils.move().from(item.slot()).toHotbar(empty.slot());
					InvUtils.swap(empty.slot(), false);
					return true;
				} else if (force) {
					InvUtils.move().from(item.slot()).toHotbar(mc.player.getInventory().getSelectedSlot());
					return true;
				} else {
					if (thismodule != null)
						thismodule.info("no space in hotbar");
					return false;
				}
			}
		} else {
			if (thismodule != null)
				thismodule.info("no " + (iteminmessage != null ? iteminmessage : "required item") + " found");
			return false;
		}
	}

	public static boolean switchtoitem(Item item, boolean quickmove, boolean force, Module thismodule) {
		if (!Utils.canUpdate() && mc.player.getMainHandItem().getItem() == item)
			return true;
		return switchtoitem(InvUtils.find(item), quickmove, force, thismodule, item);
	}

}
