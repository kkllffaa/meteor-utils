package com.franek.meteor_tweaks.modules;

import com.franek.meteor_tweaks.mixins.AirPlaceAccessor;
import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.meteor.MouseButtonEvent;
import minegame159.meteorclient.events.render.RenderEvent;
import minegame159.meteorclient.rendering.Renderer;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.systems.modules.Modules;
import minegame159.meteorclient.systems.modules.player.LiquidInteract;
import minegame159.meteorclient.systems.modules.world.AirPlace;
import minegame159.meteorclient.utils.misc.input.KeyAction;
import minegame159.meteorclient.utils.player.FindItemResult;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class ThirdHand extends Module {
	
	private enum Type{
		Interact,
		Place,
		Onblock
	}
	
	@SuppressWarnings("unused")
	public enum Itemstouse{
		EChest(Items.ENDER_CHEST, Type.Place),
		Obsidian(Items.OBSIDIAN, Type.Place),
		Crystal(Items.END_CRYSTAL, Type.Onblock),
		Netherrack(Items.NETHERRACK, Type.Place),
		Pearl(Items.ENDER_PEARL, Type.Interact),
		Flint_and_steal(Items.FLINT_AND_STEEL, Type.Onblock);
		
		
		private final Item item;
		private final Type type;
		
		Itemstouse(Item item, Type type) {
			this.item = item;
			this.type = type;
		}
	}
	
	//region settings
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
	
	private final Setting<Boolean> airplace = sgGeneral.add(new BoolSetting.Builder()
		.name("air place")
		.description("Place blocks in air.")
		.defaultValue(false)
		.build()
	);
	
	private final Setting<Boolean> render = sgGeneral.add(new BoolSetting.Builder()
			.name("render")
			.description("Renders a block overlay where the obsidian will be placed if air place is enabled or liquid interact.")
			.defaultValue(true)
			.build()
	);
	//endregion
	
	public ThirdHand() {
		super(Categories.Player, "Third Hand", "Uses specified item instead of other item.");
	}
	
	
	@EventHandler
	private void onMouseButton(MouseButtonEvent event) {
		if (event.action != KeyAction.Press || event.button != GLFW_MOUSE_BUTTON_RIGHT || mc.currentScreen != null) return;
		if (mc.player == null || !useditem.get().contains(mc.player.getMainHandStack().getItem())) return;
		FindItemResult result = InvUtils.findInHotbar(itemstouse.get().item);
		
		if (!result.found()) {
			if (notify.get()) warning("Unable to find specified item.");
			return;
		}
		
		
		BlockHitResult hitResult;
		
		//assert mc.interactionManager != null;
		if (mc.crosshairTarget instanceof BlockHitResult){
			hitResult = (BlockHitResult) mc.crosshairTarget;
		}else {
			return;
		}
		
		
		//assert mc.world != null;
		
		if (mc.world == null ) throw new IllegalStateException("world is null");
		if (mc.interactionManager == null) throw new IllegalStateException("interactionmenager is null");
		BlockState blockState = mc.world.getBlockState(hitResult.getBlockPos());
		if (BlockUtils.isClickable(blockState.getBlock())) return;
		switch (itemstouse.get().type){
			
			case Interact -> {
				
				event.cancel();
				
				int preSlot = mc.player.inventory.selectedSlot;
				InvUtils.swap(result.getSlot());
				mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
				InvUtils.swap(preSlot);
			}
			case Place -> {
				
				if (blockState.isAir() || ((!Modules.get().isActive(LiquidInteract.class) && (blockState.getMaterial().isLiquid())))){
					if (airplace.get()){
						event.cancel();
						BlockUtils.place(hitResult.getBlockPos(),result,false,0,false,true);
					}
				}else if (blockState.getMaterial().isLiquid()){
					if (Modules.get().isActive(LiquidInteract.class)){
						event.cancel();
						BlockUtils.place(hitResult.getBlockPos(),result,false,0,false,true);
					}
				}else {
					
					event.cancel();
					
					int preSlot = mc.player.inventory.selectedSlot;
					InvUtils.swap(result.getSlot());
					mc.interactionManager.interactBlock(mc.player,mc.world,Hand.MAIN_HAND,hitResult);
					InvUtils.swap(preSlot);
				}
			}
			case Onblock -> {
				if (!(blockState.isAir() || blockState.getMaterial().isLiquid())){
					event.cancel();
					
					int preSlot = mc.player.inventory.selectedSlot;
					InvUtils.swap(result.getSlot());
					mc.interactionManager.interactBlock(mc.player,mc.world,Hand.MAIN_HAND,hitResult);
					InvUtils.swap(preSlot);
				}
			}
		}
	}
	
	
	@EventHandler
	private void onRender(RenderEvent event) {
		if (!render.get()) return;
		if (mc.world == null || mc.crosshairTarget == null) return;
		if (!(mc.crosshairTarget instanceof BlockHitResult)) return;
		BlockState blockState = mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos());
		
		if (!(airplace.get() || (Modules.get().get(LiquidInteract.class).isActive() && (blockState.getMaterial().isLiquid())))
				|| !blockState.getMaterial().isReplaceable()
				|| !useditem.get().contains(Objects.requireNonNull(mc.player).getMainHandStack().getItem())
				|| itemstouse.get().type != Type.Place
		) return;
		if (Modules.get().get(AirPlace.class).isActive()
				&& !(!(mc.player.getMainHandStack().getItem() instanceof BlockItem) || !((AirPlaceAccessor) Modules.get().get(AirPlace.class)).render().get())
		) return;
		
		Renderer.boxWithLines(Renderer.NORMAL, Renderer.LINES, ((BlockHitResult) mc.crosshairTarget).getBlockPos(), ((AirPlaceAccessor) Modules.get().get(AirPlace.class)).sideColor().get(), ((AirPlaceAccessor) Modules.get().get(AirPlace.class)).lineColor().get(), ((AirPlaceAccessor) Modules.get().get(AirPlace.class)).shapeMode().get(), 0);
	}
}
