package com.franek.meteor_tweaks.systems.chestmemory;

import com.franek.meteor_tweaks.Addon;
import com.franek.meteor_tweaks.utils.MathUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import minegame159.meteorclient.MeteorClient;
import minegame159.meteorclient.events.game.GameJoinedEvent;
import minegame159.meteorclient.events.game.GameLeftEvent;
import minegame159.meteorclient.events.game.OpenScreenEvent;
import minegame159.meteorclient.events.world.BlockActivateEvent;
import minegame159.meteorclient.systems.System;
import minegame159.meteorclient.systems.Systems;
import minegame159.meteorclient.systems.modules.Modules;
import minegame159.meteorclient.systems.modules.render.hud.HUD;
import minegame159.meteorclient.utils.Utils;
import minegame159.meteorclient.utils.player.PlayerUtils;
import minegame159.meteorclient.utils.world.Dimension;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.util.math.Vec3i;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static minegame159.meteorclient.utils.Utils.mc;

public class ChestMemory extends System<ChestMemory> {
	
	
	@SuppressWarnings("unchecked")
	public final static HashMap<Vec3i, ContainerC>[] containermaps = new HashMap[Dimension.values().length];
	
	public static HashMap<Vec3i, ContainerC> getactualdimContainerHashMap(){
		return containermaps[PlayerUtils.getDimension().ordinal()];
	}
	
	
	
	public ChestMemory() {
		super(null);
		for (int i = 0; i < containermaps.length; i++) {
			containermaps[i] = new HashMap<>();
		}
	}
	
	public static ChestMemory getInstance() {
		return Systems.get(ChestMemory.class);
	}
	
	public static void set(Vec3i pos, ContainerC container) {
		getactualdimContainerHashMap().put(pos, container);
	}
	
	public static ContainerC get(Vec3i pos) {
		return getactualdimContainerHashMap().get(pos);
	}
	
	public static void remove(Vec3i pos) {
		getactualdimContainerHashMap().remove(pos);
	}
	
	
	@EventHandler
	private void onGameJoined(GameJoinedEvent event) {
		load();
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	private void onGameDosconnected(GameLeftEvent event) {
		//todo sprawdzic czy zapisuje sie przy wyjsciu
		for (@SuppressWarnings("rawtypes") Map map : containermaps){
			map.clear();
		}
	}
	
	@Override
	public File getFile() {
		if (!Utils.canUpdate()) return null;
		return new File(new File(MeteorClient.FOLDER, "savedchests"), Utils.getWorldName() + ".nbt");
	}
	
	
	
	
	//region logic
	//todo
	private Vec3i pos;
	private int openedstate;
	
	
	private boolean modulnoteactive() {
		return !Modules.get().isActive(HUD.class) || !Modules.get().get(HUD.class).isActive();
	}
	
	@EventHandler
	private void onBlockActivate(BlockActivateEvent event) {
		if (modulnoteactive()) {
			openedstate = 0;
			pos = null;
			return;
		}
		if (openedstate == 0 && mc.crosshairTarget != null && ContainerC.goodblock(event.blockState.getBlock())) {
			openedstate = 1;
			pos = new Vec3i(mc.crosshairTarget.getPos().x,mc.crosshairTarget.getPos().y,mc.crosshairTarget.getPos().z);
		}else if (openedstate == 0){
			pos = null;
		}
	}
	
	
	@EventHandler
	private void onOpenScreenEvent(OpenScreenEvent event) {
		if (openedstate == 1 && event.screen instanceof GenericContainerScreen) {
			openedstate = 2;
			return;
		}
		if (openedstate == 0) return;
		if (modulnoteactive()) {
			openedstate = 0;
			return;
		}
		if (!(mc.currentScreen instanceof GenericContainerScreen)) return;
		GenericContainerScreenHandler container = ((GenericContainerScreen) mc.currentScreen).getScreenHandler();
		
		if (container == null) {
			openedstate = 0;
			pos = null;
			return;
		}
		Inventory inv = container.getInventory();
		
		if (pos != null) {
			if (inv.isEmpty()) remove(pos);
			
			ContainerC container_temp = new ContainerC(ContainerC.Type.fromslots(inv.size()));
			if (container_temp.getType() == ContainerC.Type.Null) {
				remove(pos);
				openedstate = 0;
				pos = null;
				return;
			}
			
			for (int i = 0; i < inv.size(); i++) {
				
				if (inv.getStack(i).isEmpty()) continue;
				container_temp.getITEMS()[i] = new ItemC(inv.getStack(i));
				
			}
			if (!Arrays.stream(container_temp.getITEMS()).allMatch(Objects::isNull)) {
				set(pos,container_temp);
			}else {
				remove(pos);
			}
		}
		openedstate = 0;
		pos = null;
	}
	
	//endregion
	
	//region totag fromtag
	@Override
	public NbtCompound toTag() {
		boolean save = false;
		NbtCompound tag = new NbtCompound();
		for (Dimension dimension : Dimension.values()){
			NbtList list = new NbtList();
			Map<Vec3i, ContainerC> map = containermaps[dimension.ordinal()];
			for (Vec3i pos : map.keySet()) {
				if (map.get(pos).getType() == ContainerC.Type.Null) continue;
				NbtCompound containertag = new NbtCompound();
				containertag.putInt("x",pos.getX());
				containertag.putInt("y",pos.getY());
				containertag.putInt("z",pos.getZ());
				NbtCompound cc = map.get(pos).toTag();
				if (!cc.contains("items")) continue;
				containertag.put("container",cc);
				list.add(containertag);
			}
			if (!list.isEmpty()) save = true;
			tag.put(dimension.name(), list);
		}
		if (!save) return null;
		return tag;
	}
	
	
	@Override
	public ChestMemory fromTag(NbtCompound tag) {
		for (@SuppressWarnings("rawtypes") Map map : containermaps){
			map.clear();
		}
		for (Dimension dimension : Dimension.values()) {
			for (NbtElement element : tag.getList(dimension.name(),10)) {
				containermaps[dimension.ordinal()].put(MathUtils.vecfromtag(((NbtCompound) element)),new ContainerC(((NbtCompound) element).getCompound("container")));
			}
		}
		return this;
	}
	//endregion
}
