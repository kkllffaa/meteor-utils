package com.kkllffaa.meteorutils.mixins;

import com.kkllffaa.meteorutils.modules.EchestSave;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.NameProtect;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.player.EChestMemory;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.kkllffaa.meteorutils.modules.EchestSave.loc;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(EChestMemory.class) //in mixin because GameJoinedEvent dont fire on modules
public abstract class EchestMemoryMixin {
	
	@Shadow @Final public static DefaultedList<ItemStack> ITEMS;
	
	private static File getSaveFile() {
		if (!Modules.get().isActive(EchestSave.class)) return null;
		if (mc.player == null || Utils.getFileWorldName().isEmpty() || Modules.get().isActive(NameProtect.class)) return null;
		if (mc.isInSingleplayer()) {
			return new File(new File(loc, "s"), Utils.getFileWorldName() + ".nbt");
		}else if (!Modules.get().get(EchestSave.class).respectdifftentnicks.get()){
			return new File(new File(loc, "all"), Utils.getFileWorldName() + ".nbt");
		}else {
			return new File(new File(loc, mc.player.getGameProfile().getName()), Utils.getFileWorldName() + ".nbt");
		}
	}
	
	@EventHandler
	private static void save(GameLeftEvent event) {
		if (mc.player == null) return;
		if (getSaveFile() == null) return;
		if (ITEMS.stream().allMatch(ItemStack::isEmpty)) {
			getSaveFile().delete();
			return;
		}
		
		NbtCompound tag = new NbtCompound();
		NbtList list = new NbtList();
		for (ItemStack stack : ITEMS) {
			list.add(stack.encode(mc.player.getRegistryManager()));
		}
		
		tag.put("items", list);
		try {
			
			loc.mkdir();
			getSaveFile().getParentFile().mkdir();
			getSaveFile().createNewFile();
			
			NbtIo.write(tag, getSaveFile().toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	private static void load(GameJoinedEvent event) {
		if (getSaveFile() == null || mc.player == null) return;
		
		
		NbtCompound tag = null;
		try {
			tag = NbtIo.read(getSaveFile().toPath());
		} catch (IOException e) {
			if (!(e instanceof FileNotFoundException)) e.printStackTrace();
		}
		ITEMS.clear();
		if (tag == null) return;
		
		NbtList list = tag.getList("items", 10);
		
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof NbtCompound) {
				ITEMS.set(i, ItemStack.fromNbtOrEmpty(mc.player.getRegistryManager(), (NbtCompound) list.get(i)));
			}
		}
		
	}
}
