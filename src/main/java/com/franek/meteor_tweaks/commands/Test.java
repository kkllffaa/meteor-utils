package com.franek.meteor_tweaks.commands;

import com.franek.meteor_tweaks.utils.MyBlockUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Test extends Command {
	public Test() {
		super("Test", "description");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
			
			if (mc.world == null || mc.player == null || mc.interactionManager == null || mc.crosshairTarget == null) return SINGLE_SUCCESS;
			
			MyBlockUtils.immediateBlockIterator(3, 3, (blockPos, blockState) -> {
				
				info(BlockUtils.place(blockPos.offset(blockState.get(Properties.FACING), 1),
						Hand.OFF_HAND, PlayerInventory.OFF_HAND_SLOT,
						false, 0, false, false, false) + "");
				
				/*
				BlockUtils.place(blockPos.offset(blockState.get(Properties.FACING), 1),
						Hand.MAIN_HAND, mc.player.getInventory().selectedSlot,
						false, 0, true, false, false);
				*/
				
			}, Blocks.PISTON);
			
			
			

			
			return SINGLE_SUCCESS;
			
		});
	}
}
