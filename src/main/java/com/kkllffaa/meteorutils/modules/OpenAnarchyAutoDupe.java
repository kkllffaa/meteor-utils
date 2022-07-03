package com.kkllffaa.meteorutils.modules;

import com.kkllffaa.meteorutils.Addon;
import com.kkllffaa.meteorutils.utils.ImmediateBlockIterator;
import com.kkllffaa.meteorutils.utils.MyInvUtils;
import com.kkllffaa.meteorutils.utils.MyRenderUtils;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class OpenAnarchyAutoDupe extends Module {
	
	private enum Stage {
		Waitforpiston,
		Placeitemframes,
		Waitforframes,
		Placeshulker,
		Waitforshulker,
		Activate,
		Waitpistonsextend
	}
	
	
	@SuppressWarnings("unused")
	public enum Itemplace {
		Shulker(Utils::isShulker, Items.SHULKER_BOX),
		Chest(s -> s == Items.CHEST, Items.CHEST),
		Redstone(s -> s == Items.REDSTONE, Items.REDSTONE),
		Itemframe(s -> s == Items.ITEM_FRAME, Items.ITEM_FRAME);
		
		private final Predicate<Item> itemPredicate;
		
		private final Item item;
		
		Itemplace(Predicate<Item> predicate, Item _item) {
			itemPredicate = predicate;
			item = _item;
		}
	}
	
	
	
	//region settings
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	//region delays
	private final Setting<Integer> maxdelay = sgGeneral.add(new IntSetting.Builder()
			.name("max delay")
			.description("max delay before redoing action in ticks")
			.defaultValue(15)
			.min(0)
			.sliderMin(0)
			.sliderMax(100)
			.build()
	);
	
	private final Setting<Integer> redstonedelay = sgGeneral.add(new IntSetting.Builder()
			.name("redstone delay")
			.description("delay between placing and braking redstone in ticks")
			.defaultValue(20)
			.min(0)
			.sliderMin(0)
			.sliderMax(100)
			.build()
	);
	
	private final Setting<Integer> shulkerdelay = sgGeneral.add(new IntSetting.Builder()
			.name("shulker delay")
			.description("delay between placing shulkers in ticks")
			.defaultValue(5)
			.min(0)
			.sliderMin(0)
			.sliderMax(15)
			.build()
	);
	
	private final Setting<Integer> shulkerpickup = sgGeneral.add(new IntSetting.Builder()
			.name("shulker pickup delay")
			.description("delay before turn off when no shulkers in inventory in ticks")
			.defaultValue(40)
			.min(0)
			.sliderMin(0)
			.sliderMax(80)
			.build()
	);
	//endregion
	//region render
	private final Setting<Boolean> renderredstone = sgGeneral.add(new BoolSetting.Builder()
			.name("render redstone")
			.description("render place for redstone")
			.defaultValue(true)
			.build()
	);
	
	private final Setting<Boolean> duperedstonewhenno = sgGeneral.add(new BoolSetting.Builder()
			.name("dupe redstone")
			.description("when few redstone in inventory dupe redstone instead")
			.defaultValue(false)
			.build()
	);
	
	private final Setting<Integer> redstonedupeamount = sgGeneral.add(new IntSetting.Builder()
			.name("redstone dupe amount")
			.description("dupe redstone when amount or less")
			.defaultValue(5)
			.min(0)
			.sliderMin(0)
			.max(20)
			.sliderMax(20)
			.visible(duperedstonewhenno::get)
			.build()
	);
	
	private final Setting<Boolean> renderframes = sgGeneral.add(new BoolSetting.Builder()
			.name("render frames")
			.description("render where shulker is placed")
			.defaultValue(true)
			.build()
	);
	
	private final Setting<Itemplace> itemtoplace = sgGeneral.add(new EnumSetting.Builder<Itemplace>()
			.name("item to place")
			.description("item to place in itemframes and dupe")
			.defaultValue(Itemplace.Shulker)
			.build()
	);
	//endregion
	
	public OpenAnarchyAutoDupe() {
		super(Addon.CATEGORY, "OpenAnarchyAutoDupe", "description");
	}
	
	public Stage getStage() {
		if (isActive()) return stage;
		return null;
	}
	
	public boolean lag() {
		return TickRate.INSTANCE.getTimeSinceLastTick() > 1.5f;
	}
	
	private Stage stage = Stage.Waitforpiston;
	
	private int timer = 0;
	private int timerpiston = 0;
	private int redstonetimer = 0;
	
	private boolean shulkertry = false;
	
	private boolean redstonedupe = false;
	
	private ItemFrameEntity currentframe = null;
	
	private BlockPos redstone = null;
	
	@Override
	public void onDeactivate() {
		if (redstone != null && mc.world != null && mc.player != null && mc.world.isAir(redstone) && MyInvUtils.switchtoitem(Items.REDSTONE, true, true, null)) {
			BlockUtils.place(redstone, Hand.MAIN_HAND, mc.player.getInventory().selectedSlot, false, 0, true, false, false);
		}
		redstone = null;
	}
	
	@Override
	public void onActivate() {
		stage = Stage.Waitforpiston;
		timer = 0;
		timerpiston = 0;
		redstonetimer = 0;
		currentframe = null;
		redstone = findredstone();
	}
	
	public static BlockPos findredstone() {
		var ref = new Object() {
			BlockPos _redstone = null;
		};
		ImmediateBlockIterator.register(4, 2, (blockPos, blockState) -> {
			if (blockState.get(Properties.POWER) > 0) {
				ref._redstone = blockPos;
				ImmediateBlockIterator.disableCurrent();
			}
		}, Blocks.REDSTONE_WIRE);
		return ref._redstone;
	}
	
	@EventHandler
	public void onTick(TickEvent.Pre event) {
		//region before
		if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
		
		if (lag()) return;
		
		if (redstone == null || !mc.player.getBlockPos().isWithinDistance(redstone, 4.1)) {
			if (redstonetimer > 100) {
				info("no redstone nerby");
				toggle();
				return;
			}
			redstonetimer++;
			BlockPos _redstone = findredstone();
			if (_redstone != null) redstone = _redstone;
			return;
		}
		redstonetimer = 0;
		
		final List<BlockPos> pistons = new ArrayList<>();
		
		ImmediateBlockIterator.register(3, 2, (blockPos, blockState) -> pistons.add(blockPos), Blocks.PISTON);
		
		if (pistons.isEmpty()) {
			if (timerpiston > 100) {
				info("no pistons nerby");
				toggle();
				return;
			}
			timerpiston++;
			return;
		}
		timerpiston = 0;
		//endregion
		
		timer++;
		switch (stage) {
			case Waitforpiston -> {
				if (!mc.world.isAir(redstone) && mc.world.getBlockState(redstone).getBlock() == Blocks.REDSTONE_WIRE && mc.world.getBlockState(redstone).get(Properties.POWER) > 0) {
					if (pistons.stream().allMatch(s -> mc.world.getBlockState(s).get(Properties.EXTENDED)) && timer > redstonedelay.get()) {
						BlockUtils.breakBlock(redstone, true);
					}else if (timer > maxdelay.get()) {
						BlockUtils.breakBlock(redstone, true);
						stage = Stage.Activate;
						timer = 0;
					}
				}else if (pistons.stream().allMatch(s -> mc.world.getBlockState(s).get(Properties.EXTENDED))) {
					if (timer > maxdelay.get()) {
						stage = Stage.Activate;
						timer = 0;
					}
				}else {
					stage = Stage.Placeitemframes;
					timer = 0;
				}
			}
			case Placeitemframes -> {
				FindItemResult frames = InvUtils.findInHotbar(Items.ITEM_FRAME);
				if (!frames.found() || frames.count() < pistons.size()) {
					info("not enough item frames in hotbar");
					toggle();
					return;
				}
				for (BlockPos pos : pistons) {
					BlockState b = mc.world.getBlockState(pos);
					if (b.get(Properties.EXTENDED)) {
						stage = Stage.Waitforpiston;
						return;
					}
					
					info(String.valueOf(pos));
					BlockUtils.place(pos.offset(b.get(Properties.FACING), 1),
							Hand.MAIN_HAND, frames.slot(),
							false, 0, true, false, false);
					
					
				}
				info("");
				
				timer = 0;
				stage = Stage.Waitforframes;
			}
			case Waitforframes -> {
				
				for (Entity entity : mc.world.getEntities()) {
					if (!(entity instanceof ItemFrameEntity) || mc.player.distanceTo(entity) > 3) continue;
					
					if (pistons.stream().anyMatch(s -> entity.getBlockPos().equals(s.offset(mc.world.getBlockState(s).get(Properties.FACING)))))
					
					if (pistons.stream().noneMatch(s -> entity.getBlockPos().equals(s.offset(mc.world.getBlockState(s).get(Properties.FACING))))) {
						if (timer > maxdelay.get()) {
							stage = Stage.Placeitemframes;
							timer = 0;
						}
						return;
					}else {
						stage = Stage.Placeshulker;
						timer = 0;
					}
				}
			}
			case Placeshulker -> {
				for (Entity entity : mc.world.getEntities()) {
					if (!(entity instanceof ItemFrameEntity) || mc.player.distanceTo(entity) > 4) continue;
					
					
					if (pistons.stream().anyMatch(s -> entity.getBlockPos().equals(s.offset(mc.world.getBlockState(s).get(Properties.FACING))))) {
						if (((ItemFrameEntity) entity).getHeldItemStack().isEmpty()) {
							
							//region place
							
							
							if (duperedstonewhenno.get() && (InvUtils.find(Items.REDSTONE).count() <= redstonedupeamount.get() || (redstonedupe && InvUtils.find(Items.REDSTONE).count() <= 60))) {
								redstonedupe = true;
								if (MyInvUtils.switchtoitem(Items.REDSTONE, true, true, this)) {
									mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
								}else {
									toggle();
									return;
								}
							}else {
								redstonedupe = false;
								if (MyInvUtils.switchtoitem(InvUtils.find(stack -> itemtoplace.get().itemPredicate.test(stack.getItem())), true, true, (timer > shulkerpickup.get() ? this : null), itemtoplace.get().item)) {
									mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
								}else if (timer > shulkerpickup.get()) {
									toggle();
									return;
								}else {
									for (Entity item : mc.world.getEntities()) {
										if ((item instanceof ItemEntity) && mc.player.distanceTo(item) < 1.5) return;
									}
									stage = Stage.Activate;
									timer = 0;
									return;
								}
							}
							//endregion
							
							
							
							stage = Stage.Waitforshulker;
							currentframe = (ItemFrameEntity) entity;
							timer = 0;
							return;
						}else {
							stage = Stage.Activate;
							timer = 0;
						}
					}
				}
			}
			case Waitforshulker -> {
				
				if (currentframe == null || !currentframe.isAlive()) {
					stage = Stage.Placeitemframes;
					timer = 0;
				}else if (currentframe.getHeldItemStack().isEmpty()) {
					if (timer > maxdelay.get()) {
						if (shulkertry) {
							stage = Stage.Activate;
							shulkertry = false;
						}else {
							stage = Stage.Placeshulker;
							shulkertry = true;
						}
						timer = 0;
					}
				}else if (timer > shulkerdelay.get()){
					if (redstonedupe && InvUtils.find(Items.REDSTONE).count() <= 3) {
						stage = Stage.Activate;
					}else {
						stage = Stage.Placeshulker;
					}
					shulkertry = false;
					timer = 0;
				}
			}
			case Activate -> {
				
				if (timer < redstonedelay.get()) return;
				
				//FindItemResult result = InvUtils.findInHotbar(Items.REDSTONE);
				
				if (!MyInvUtils.switchtoitem(Items.REDSTONE, true, true, this)) {
					toggle();
					return;
				}
				
				if (mc.world.isAir(redstone))
					BlockUtils.place(redstone, Hand.MAIN_HAND, mc.player.getInventory().selectedSlot, false, 0, true, false, false);
				
				//BlockUtils.place(redstone, result, false, 0, true, false);
				
				timer = 0;
				stage = Stage.Waitpistonsextend;
			}
			case Waitpistonsextend -> {
				if (!mc.world.isAir(redstone) && mc.world.getBlockState(redstone).getBlock() == Blocks.REDSTONE_WIRE && mc.world.getBlockState(redstone).get(Properties.POWER) > 0) {
					
					if (pistons.stream().allMatch(s -> mc.world.getBlockState(s).get(Properties.EXTENDED))) {
						stage = Stage.Waitforpiston;
						timer = 0;
					}else if (timer > maxdelay.get()) {
						stage = Stage.Waitforpiston;
						timer = 0;
					}
				}else if (timer > maxdelay.get()) {
					stage = Stage.Activate;
					timer = 0;
				}
			}
		}
	}
	
	@EventHandler
	public void onRender(Render3DEvent event) {
		if (mc.player == null) return;
		if (redstone != null && renderredstone.get()) {
			event.renderer.box(redstone.getX(), redstone.getY(), redstone.getZ(), redstone.getX() + 1, redstone.getY() + 0.25, redstone.getZ() + 1, new Color(1f, 0.6f, 0.2f, 0.2f), new Color(0f, 1f, 0.5f, 1f), ShapeMode.Both, 0);
		}
		
		if (currentframe != null && (stage == Stage.Placeshulker || stage == Stage.Waitforshulker) && renderframes.get()) {
			int x = (int) currentframe.getRotationVector().x;
			int y = (int) currentframe.getRotationVector().y;
			int z = (int) currentframe.getRotationVector().z;
			
			Direction direction = Direction.fromVector(x,y,z);
			
			if (direction != null) {
				MyRenderUtils.renderQuad(currentframe.getBlockPos(), direction.getOpposite(),event, new Color(1f,0f,0f,0.5f));
			}
		}
		
	}
	
	@EventHandler
	public void onGameJoin(GameJoinedEvent event) {
		if (isActive()) toggle();
	}
	
	@EventHandler
	public void onGameLeft(GameLeftEvent event) {
		if (isActive()) toggle();
	}
}
