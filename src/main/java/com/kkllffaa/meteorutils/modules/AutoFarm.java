package com.kkllffaa.meteorutils.modules;

import com.kkllffaa.meteorutils.Addon;
import com.kkllffaa.meteorutils.utils.ImmediateBlockIterator;
import com.kkllffaa.meteorutils.utils.MyInvUtils;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public class AutoFarm extends Module {

	public AutoFarm() {
		super(Addon.CATEGORY, "auto-farm", "automaticly harvest and bonemeal crop");
	}

	private BlockPos pos = null;

	@Override
	public void onActivate() {
		if (!Utils.canUpdate()) {
			disable();
			return;
		}
		pos = null;
		// TODO: activate on just farmland and dont requre wheat
		// TODO: why block iterator???
		ImmediateBlockIterator.register(1, 1, (blockPos, blockState) -> {
			pos = blockPos;
			ImmediateBlockIterator.disableCurrent();
		}, Blocks.WHEAT);
		if (pos == null)
			disable();
	}

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		if (!Utils.canUpdate()) {
			disable();
			return;
		}

		// check if standing in position
		if (pos == null || !mc.player.blockPosition().offset(Direction.UP.getUnitVec3i()).equals(pos)) {
			disable();
			return;
		}

		BlockState state = mc.level.getBlockState(pos);

		BlockState farmland = mc.level.getBlockState(pos.offset(Direction.DOWN.getUnitVec3i()));

		// check if standing on moist farmland
		if (farmland.getBlock() != Blocks.FARMLAND || farmland.getValue(FarmlandBlock.MOISTURE) != 7) {
			disable();
			return;
		}

		if (state.getBlock() == Blocks.WHEAT) {
			if (state.getValue(BlockStateProperties.AGE_7) == BlockStateProperties.MAX_AGE_7) {
				BlockUtils.breakBlock(pos, true);
			} else if (MyInvUtils.switchtoitem(Items.BONE_MEAL, true, true, this)) {
				BlockUtils.interact(new BlockHitResult(mc.player.position(), Direction.UP, pos, false),
						InteractionHand.MAIN_HAND, true);
			} else
				disable();
		} else if (state.isAir() && MyInvUtils.switchtoitem(Items.WHEAT_SEEDS, true, true, this)) {
			BlockUtils.place(pos, InteractionHand.MAIN_HAND, mc.player.getInventory().getSelectedSlot(), false, 0,
					true,
					false,
					false);
		} else
			disable();

	}
}
