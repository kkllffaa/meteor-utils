package com.franek.meteor_tweaks.modules;

import com.franek.meteor_tweaks.utils.MyBlockUtils;
import com.franek.meteor_tweaks.utils.MyRenderUtils;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
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
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

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
	
	private final Setting<Boolean> renderframes = sgGeneral.add(new BoolSetting.Builder()
			.name("render frames")
			.description("render where shulker is placed")
			.defaultValue(true)
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
	private int redstonetimer = 0;
	
	private ItemFrameEntity currentframe = null;
	
	private BlockPos redstone = null;
	
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
		MyBlockUtils.immediateBlockIterator(3, 2, (blockPos, blockState) -> {
			if (blockState.get(Properties.POWER) > 0) {
				ref._redstone = blockPos;
				MyBlockUtils.disableCurrent();
			}
		}, Blocks.REDSTONE_WIRE);
		return ref._redstone;
	}
	
	@EventHandler
	public void onTick(TickEvent.Pre event) {
		//region before
		if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
		
		if (lag()) return;
		
		if (redstone == null || !mc.player.getBlockPos().isWithinDistance(redstone, 4)) {
			if (redstonetimer > 100) {
				info("no redstone nerby");
				toggle();
				return;
			}
			redstonetimer++;
			redstone = findredstone();
			return;
		}
		redstonetimer = 0;
		
		final List<BlockPos> pistons = new ArrayList<>();
		
		MyBlockUtils.immediateBlockIterator(3, 2, (blockPos, blockState) -> pistons.add(blockPos), Blocks.PISTON);
		
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
					}
				}else if (timer > maxdelay.get()) {
					stage = Stage.Activate;
					timer = 0;
				}else {
					stage = Stage.Placeitemframes;
					timer = 0;
				}
			}
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
					if (!(entity instanceof ItemFrameEntity) || mc.player.distanceTo(entity) > 4) continue;
					
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
					if (!(entity instanceof ItemFrameEntity) || mc.player.distanceTo(entity) > 4) continue;
					
					
					if (pistons.stream().anyMatch(s -> entity.getBlockPos().equals(s.offset(mc.world.getBlockState(s).get(Properties.FACING))))) {
						if (((ItemFrameEntity) entity).getHeldItemStack().isEmpty()) {
							
							//region place
							FindItemResult shulker = InvUtils.find(s -> Utils.isShulker(s.getItem()));
							
							
							if (!shulker.found()) {
								if (/*stage != Stage.Activate || */timer > shulkerpickup.get()) {
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
									mc.interactionManager.interactEntity(mc.player, entity,Hand.MAIN_HAND);
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
				}else if ((!currentframe.getHeldItemStack().isEmpty() && timer > shulkerdelay.get()) || timer > maxdelay.get()) {
					stage = Stage.Placeshulker;
					timer = 0;
				}
			}
			case Activate -> {
				
				if (timer < redstonedelay.get()) return;
				
				FindItemResult result = InvUtils.findInHotbar(Items.REDSTONE);
				
				if (!result.found()) {
					toggle();
					info("no redstone");
					return;
				}
				
				if (mc.world.isAir(redstone))
					BlockUtils.place(redstone, result, false, 0, true, false);
				
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
