package com.franek.meteor_tweaks.modules;

import com.franek.meteor_tweaks.utils.MyBlockUtils;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

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
	
	//region settings
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	private final Setting<Integer> maxdelay = sgGeneral.add(new IntSetting.Builder()
			.name("max delay")
			.description("max delay before redoing action in ticks")
			.defaultValue(15)
			.min(0)
			.sliderMax(100)
			.build()
	);
	
	private final Setting<Integer> leverdelay = sgGeneral.add(new IntSetting.Builder()
			.name("leverdelay")
			.description("delay between lever activation")
			.defaultValue(50)
			.min(0)
			.sliderMax(300)
			.build()
	);
	
	private final Setting<Integer> shulkerdelay = sgGeneral.add(new IntSetting.Builder()
			.name("shulker delay")
			.description("delay between placing shulkers")
			.defaultValue(5)
			.min(0)
			.sliderMax(15)
			.build()
	);
	
	private final Setting<Integer> shulkerpickup = sgGeneral.add(new IntSetting.Builder()
			.name("shulker pickup delay")
			.description("turn off when no shulkers in inventory")
			.defaultValue(40)
			.min(0)
			.sliderMax(80)
			.build()
	);
	
	//endregion
	
	public OpenAnarchyAutoDupe(Category category) {
		super(category, "OpenAnarchyAutoDupe", "description");
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
	
	private ItemFrameEntity currentframe = null;
	
	private BlockPos lever = null;
	
	@Override
	public void onActivate() {
		stage = Stage.Waitforpiston;
		timer = 0;
		timerpiston = 0;
		currentframe = null;
		lever = null;
	}
	
	@EventHandler
	public void onTick(TickEvent.Pre event) {
		if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
		
		if (lag()) return;
		
		final List<BlockPos> pistons = new ArrayList<>();
		
		MyBlockUtils.immediateBlockIterator(3, 2, (blockPos, blockState) -> pistons.add(blockPos), Blocks.PISTON);
		
		if (pistons.isEmpty() && timerpiston <= 5) {
			timerpiston++;
			return;
		}
		timerpiston = 0;
		
		if (pistons.isEmpty()) {
			info("no pistons nerby");
			toggle();
			return;
		}
		
		timer++;
		switch (stage) {
			case Waitforpiston -> MyBlockUtils.immediateBlockIterator(3, 2, (blockPos, blockState) -> {
				if (blockState.get(Properties.POWERED)) {
					for (BlockPos pos : pistons) {
						if (mc.world.getBlockState(pos).get(Properties.EXTENDED) && timer > leverdelay.get()) {
							mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), blockState.get(Properties.HORIZONTAL_FACING), blockPos, false));
							timer = 0;
						}
					}
				}else if (timer > maxdelay.get()) {
					stage = Stage.Activate;
					timer = 0;
				}else {
					stage = Stage.Placeitemframes;
					timer = 0;
				}
				MyBlockUtils.disableCurrent();
			}, Blocks.LEVER);
			case Placeitemframes -> {
				FindItemResult frames = InvUtils.findInHotbar(Items.ITEM_FRAME);
				if (!frames.found() || frames.getCount() < pistons.size()) {
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
					
					
					BlockUtils.place(pos.offset(b.get(Properties.FACING), 1),
							Hand.MAIN_HAND, frames.getSlot(),
							false, 0, true, false, false);
					
					
				}
				timer = 0;
				stage = Stage.Waitforframes;
			}
			case Waitforframes -> {
				
				for (Entity entity : mc.world.getEntities()) {
					if (!(entity instanceof ItemFrameEntity) || mc.player.distanceTo(entity) > 3) continue;
					
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
				//stage = Stage.Placeshulker;
			}
			case Placeshulker -> {
				for (Entity entity : mc.world.getEntities()) {
					if (!(entity instanceof ItemFrameEntity) || mc.player.distanceTo(entity) > 3) continue;
					
					
					if (pistons.stream().anyMatch(s -> entity.getBlockPos().equals(s.offset(mc.world.getBlockState(s).get(Properties.FACING))))) {
						if (((ItemFrameEntity) entity).getHeldItemStack().isEmpty()) {
							
							//region place
							FindItemResult shulker = InvUtils.find(s -> Utils.isShulker(s.getItem()));
							
							
							if (!shulker.found()) {
								if (stage != Stage.Activate || timer > shulkerpickup.get()) {
									toggle();
									info("no shulkers");
								}
								return;
							}
							
							if (shulker.isHotbar()) {
								InvUtils.swap(shulker.getSlot());
								mc.interactionManager.interactEntity(mc.player, entity,Hand.MAIN_HAND);
							}else {
								FindItemResult empty = InvUtils.findEmpty();
								if (empty.found() && empty.isHotbar()) {
									InvUtils.move().from(shulker.getSlot()).toHotbar(empty.getSlot());
									InvUtils.swap(empty.getSlot());
								}else {
									toggle();
									info("no empty slots in hotbar");
									return;
								}
							}
							//endregion
							
							
							
							stage = Stage.Waitforshulker;
							currentframe = (ItemFrameEntity) entity;
							timer = 0;
							return;
						}else stage = Stage.Activate;
					}
				}
			}
			case Waitforshulker -> {
				
				if (currentframe == null || !currentframe.isAlive()) {
					stage = Stage.Placeitemframes;
					return;
				}
				if (timer > shulkerdelay.get() && currentframe.getHeldItemStack().isEmpty()) {
					stage = Stage.Placeshulker;
					timer = 0;
				}
				if (timer > maxdelay.get()) {
					stage = Stage.Placeshulker;
					timer = 0;
				}
			}
			case Activate -> MyBlockUtils.immediateBlockIterator(2,2,(blockPos, blockState) -> {
				
				if (!blockState.get(Properties.POWERED)) mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), blockState.get(Properties.HORIZONTAL_FACING), blockPos, false));
				
				timer = 0;
				stage = Stage.Waitpistonsextend;
				MyBlockUtils.disableCurrent();
			}, Blocks.LEVER);
			case Waitpistonsextend -> MyBlockUtils.immediateBlockIterator(2, 2, (blockPos, blockState) -> {
				
				if (blockState.get(Properties.POWERED)) {
					
					if (pistons.stream().allMatch(s -> mc.world.getBlockState(s).get(Properties.EXTENDED))) {
						stage = Stage.Waitforpiston;
						timer = 0;
					}
				}else if (timer > maxdelay.get()) {
					stage = Stage.Activate;
					timer = 0;
				}
				MyBlockUtils.disableCurrent();
			}, Blocks.LEVER);
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
