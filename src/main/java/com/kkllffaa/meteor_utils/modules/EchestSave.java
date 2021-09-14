package com.kkllffaa.meteor_utils.modules;

import com.kkllffaa.meteor_utils.Addon;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.NameProtect;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.EChestMemory;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;

import java.io.File;
import java.io.IOException;

public class EchestSave extends Module {
	public EchestSave() {
		super(Addon.CATEGORY, "echest-save", "save and restore items in echest after restart or disconnect");
	}
	
	private final Setting<Boolean> respectdifftentnicks = settings.getDefaultGroup().add(new BoolSetting.Builder()
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
	
	private static final File loc = new File(MeteorClient.FOLDER, "echests");
	private File getFile() {
		if (Utils.mc.player == null || Utils.getWorldName().isEmpty() || Modules.get().isActive(NameProtect.class)) return null;
		if (Utils.mc.isInSingleplayer()) {
			return new File(new File(loc, "s"), Utils.getWorldName());
		}else if (!respectdifftentnicks.get()){
			return new File(new File(loc, "all"), Utils.getWorldName() + ".nbt");
		}else {
			return new File(new File(loc, Utils.mc.player.getGameProfile().getName()), Utils.getWorldName() + ".nbt");
		}
	}
	
	@EventHandler
	private void save(GameLeftEvent event) {
		if (getFile() == null) return;
		if (EChestMemory.ITEMS.stream().allMatch(ItemStack::isEmpty)) return;
		
		NbtCompound tag = new NbtCompound();
		NbtList list = new NbtList();
		for (ItemStack stack : EChestMemory.ITEMS) {
			NbtCompound item = new NbtCompound();
			list.add(stack.writeNbt(item));
		}
		
		tag.put("items", list);
		try {
			if (Utils.mc.player == null) return;
			
			loc.mkdir();
			getFile().getParentFile().mkdir();
			getFile().createNewFile();
			
			NbtIo.write(tag, getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	private void load(GameJoinedEvent event) {
		if (getFile() == null) return;
		
		
		NbtCompound tag = null;
		try {
			tag = NbtIo.read(getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (tag == null) return;
		
		EChestMemory.ITEMS.clear();
		NbtList list = tag.getList("items", 10);
		
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof NbtCompound) {
				EChestMemory.ITEMS.set(i, ItemStack.fromNbt((NbtCompound) list.get(i)));
			}
		}
		
	}
	
}
