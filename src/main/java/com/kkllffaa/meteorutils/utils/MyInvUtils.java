package com.kkllffaa.meteorutils.utils;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MyInvUtils {
	
	public static boolean switchtoitem(FindItemResult item, boolean quickmove, boolean force, Module thismodule, Item iteminmessage) {
		if (mc.player == null) return false;
		
		if (item.found()) {
			if (item.isHotbar()) {
				InvUtils.swap(item.slot(), false);
				return true;
			}else {
				FindItemResult empty = InvUtils.findEmpty();
				if (empty.found() && empty.isHotbar()) {
					if (quickmove && !(mc.currentScreen instanceof GenericContainerScreen)) InvUtils.quickMove().from(item.slot()).toHotbar(empty.slot());
					else InvUtils.move().from(item.slot()).toHotbar(empty.slot());
					InvUtils.swap(empty.slot(), false);
					return true;
				}else if (force){
					InvUtils.move().from(item.slot()).toHotbar(mc.player.getInventory().selectedSlot);
					return true;
				}else {
					if (thismodule != null)	thismodule.info("no space in hotbar");
					return false;
				}
			}
		}else {
			if (thismodule != null) thismodule.info("no " + (iteminmessage != null ? iteminmessage : "required item") + " found");
			return false;
		}
	}
	
	
	public static boolean switchtoitem(Item item, boolean quickmove, boolean force, Module thismodule) {
		if (mc.player != null && mc.player.getMainHandStack().getItem() == item) return true;
		return switchtoitem(InvUtils.find(item), quickmove, force, thismodule, item);
	}
	
	
	public static void sendrecipebookpacket(Item item) {
		MinecraftClient mc = MinecraftClient.getInstance();
		
		if (mc != null && mc.player != null && mc.getNetworkHandler() != null) {
			mc.getNetworkHandler().sendPacket(new CraftRequestC2SPacket(mc.player.currentScreenHandler.syncId, new Recipe<>() {
				@Override public boolean matches(Inventory inventory, World world) { return false; }
				@Override public ItemStack craft(Inventory inventory) { return null; }
				@Override public boolean fits(int width, int height) { return false; }
				@Override public ItemStack getOutput() { return null; }
				@Override public RecipeSerializer<?> getSerializer() { return null; }
				@Override public RecipeType<?> getType() { return null; }
				
				@Override
				public Identifier getId() {
					return Registry.ITEM.getId(item);
				}
			}, false));
		}
	}
}
