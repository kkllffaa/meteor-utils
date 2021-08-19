package com.franek.meteor_tweaks.modules;

import com.franek.meteor_tweaks.Addon;
import com.franek.meteor_tweaks.utils.MyBlockUtils;
import com.franek.meteor_tweaks.utils.MyItemUtils;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.Optional;

public class AutoFarm extends Module {
	public AutoFarm() {
		super(Addon.CATEGORY, "auto-farm", "automaticly harvest and bonemeal crop");
	}
	
	private BlockPos pos = null;
	
	@Override
	public void onActivate() {
		pos = null;
		MyBlockUtils.immediateBlockIterator(1, 1, (blockPos, blockState) -> {pos = blockPos; MyBlockUtils.disableCurrent();}, Blocks.WHEAT);
		if (pos == null) toggle();
	}
	
	@EventHandler
	private void onTick(TickEvent.Pre event) {
		if (mc.player == null || mc.world == null || mc.interactionManager == null || TickRate.INSTANCE.getTimeSinceLastTick() > 1) return;
		if (pos == null || !mc.player.getBlockPos().offset(Direction.UP).equals(pos)) { toggle(); return; }
		
		
		BlockState state = mc.world.getBlockState(pos);
		
		if (mc.world.getBlockState(pos.offset(Direction.DOWN)).getBlock() == Blocks.FARMLAND && mc.world.getBlockState(pos.offset(Direction.DOWN)).get(Properties.MOISTURE) == 7) {
			if (state.getBlock() == Blocks.WHEAT) {
				if (state.get(Properties.AGE_7) == Properties.AGE_7_MAX) {
					BlockUtils.breakBlock(pos, true);
				}else if (MyItemUtils.switchtoitem(Items.BONE_MEAL, Optional.of(this))) {
					mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), Direction.UP, pos, false));
				}else toggle();
			}else if (state.isAir() && MyItemUtils.switchtoitem(Items.WHEAT_SEEDS, Optional.of(this))) {
				BlockUtils.place(pos, Hand.MAIN_HAND, mc.player.getInventory().selectedSlot, false, 0, true, false, false);
			}else toggle();

		}else toggle();
		
	}
}
