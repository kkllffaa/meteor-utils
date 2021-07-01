package com.franek.meteor_tweaks.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import minegame159.meteorclient.systems.commands.Command;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Test extends Command {
	public Test() {
		super("Test", "description");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		
		
		builder.executes(context -> {
			
			
			
			BlockState blockState = mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos());
			
			info(String.valueOf(blockState.getMaterial().isLiquid()));
			
			
			
			
			return SINGLE_SUCCESS;
		});
		
		
	}
}
