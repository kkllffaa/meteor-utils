package com.kkllffaa.meteorutils.modules;

import com.kkllffaa.meteorutils.Addon;
import com.kkllffaa.meteorutils.utils.ImmediateBlockIterator;
import com.kkllffaa.meteorutils.utils.MyInvUtils;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
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

public class AutoFarm extends Module {
	public AutoFarm() {
		super(Addon.CATEGORY, "auto-farm", "automaticly harvest and bonemeal crop");
	}
	
	private BlockPos pos = null;
	
	@Override
	public void onActivate() {
		pos = null;
		ImmediateBlockIterator.register(1, 1, (blockPos, blockState) -> {pos = blockPos; ImmediateBlockIterator.disableCurrent();}, Blocks.WHEAT);
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
				}else if (MyInvUtils.switchtoitem(Items.BONE_MEAL, true, true, this)) {
					mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), Direction.UP, pos, false));
				}else toggle();
			}else if (state.isAir() && MyInvUtils.switchtoitem(Items.WHEAT_SEEDS, true, true, this)) {
				BlockUtils.place(pos, Hand.MAIN_HAND, mc.player.getInventory().selectedSlot, false, 0, true, false, false);
			}else toggle();

		}else toggle();
		
	}
}
