package com.kkllffaa.meteorutils.modules;

import com.kkllffaa.meteorutils.Addon;

import meteordevelopment.meteorclient.events.meteor.MouseClickEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.item.Items.OBSIDIAN;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

// TODO: improve this, add more options to map item->item
public class ThirdHand extends Module {

	private final SettingGroup sgGeneral = settings.getDefaultGroup();

	private final Setting<List<Item>> useditem = sgGeneral.add(new ItemListSetting.Builder()
			.name("used item")
			.description("when you try to use this item it will use other item instead")
			.defaultValue(new ArrayList<>())
			.build());

	private final Setting<Boolean> notify = sgGeneral.add(new BoolSetting.Builder()
			.name("notify")
			.description("Notifies you when you do not have the specified item in your hotbar.")
			.defaultValue(true)
			.build());

	public ThirdHand() {
		super(Addon.CATEGORY, "Third-Hand", "places obi instead of other items.");
	}

	private int swich = -1;

	@EventHandler
	private void onMouseButton(MouseClickEvent event) {
		if (event.action != KeyAction.Press || event.button() != GLFW_MOUSE_BUTTON_RIGHT || mc.screen != null)
			return;
		if (!Utils.canUpdate() || !useditem.get().contains(mc.player.getMainHandItem().getItem()))
			return;
		FindItemResult result = InvUtils.findInHotbar(OBSIDIAN);

		if (!result.found()) {
			if (notify.get())
				warning("Unable to find specified item.");
			return;
		}
		event.cancel();

		swich = mc.player.getInventory().getSelectedSlot();

		InvUtils.swap(result.slot(), false);

		Utils.rightClick();

	}

	@EventHandler
	private void onTick(TickEvent.Post event) {
		if (swich != -1) {
			InvUtils.swap(swich, false);
			swich = -1;
		}
	}
}
