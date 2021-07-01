package com.franek.meteor_tweaks.modules;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.meteor.MouseButtonEvent;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.misc.input.KeyAction;
import minegame159.meteorclient.utils.player.FindItemResult;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class ThirdHand extends Module {
	
	private enum Type{
		Interact,
		Place
	}
	
	public enum Itemstouse{
		EChest(Items.ENDER_CHEST, Type.Place),
		Obsidian(Items.OBSIDIAN, Type.Place),
		Crystal(Items.END_CRYSTAL, Type.Place),
		Netherrack(Items.NETHERRACK, Type.Place),
		Pearl(Items.ENDER_PEARL, Type.Interact);
		
		private final Item item;
		private final Type type;
		
		Itemstouse(Item item, Type type) {
			this.item = item;
			this.type = type;
		}
	}
	
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	private final Setting<List<Item>> useditem = sgGeneral.add(new ItemListSetting.Builder()
		.name("used item")
		.description("when you try to use this item it will use other item instead")
		.defaultValue(new ArrayList<>())
		.build()
	);
	
	private final Setting<Itemstouse> itemstouse = sgGeneral.add(new EnumSetting.Builder<Itemstouse>()
		.name("item to use")
		.description("Which item to use instead of used item")
		.defaultValue(Itemstouse.Obsidian)
		.build()
	);
	
	
	private final Setting<Boolean> notify = sgGeneral.add(new BoolSetting.Builder()
		.name("notify")
		.description("Notifies you when you do not have the specified item in your hotbar.")
		.defaultValue(true)
		.build()
	);
	
	
	public ThirdHand() {
		super(Categories.Player, "Third Hand", "Uses specified item instead of other item.");
	}
	
	
	@EventHandler
	private void onMouseButton(MouseButtonEvent event) {
		if (event.action != KeyAction.Press || event.button != GLFW_MOUSE_BUTTON_RIGHT || mc.currentScreen != null) return;
		assert mc.player != null;
		if (!useditem.get().contains(mc.player.getMainHandStack().getItem())) return;
		FindItemResult result = InvUtils.findInHotbar(itemstouse.get().item);
		
		if (!result.found()) {
			if (notify.get()) warning("Unable to find specified item.");
			return;
		}
		
		
		
		
		
		
		BlockHitResult hitResult;
		
		assert mc.interactionManager != null;
		if (mc.crosshairTarget instanceof BlockHitResult){
			hitResult = (BlockHitResult) mc.crosshairTarget;
		}else {
			return;
		}
		int preSlot = mc.player.inventory.selectedSlot;
		
		assert mc.world != null;
		
		
		Block block = mc.world.getBlockState(hitResult.getBlockPos()).getBlock();
		switch (itemstouse.get().type){
			
			case Interact -> {
				if (BlockUtils.isClickable(block)) return;
				
				event.cancel();
				InvUtils.swap(result.getSlot());
				mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
			}
			case Place -> {
				if (block == Blocks.AIR || block == Blocks.WATER || block == Blocks.LAVA || BlockUtils.isClickable(block)) return;
				
				event.cancel();
				InvUtils.swap(result.getSlot());
				mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, hitResult);
			}
		}
		
		
		
		InvUtils.swap(preSlot);
		
	}
}
