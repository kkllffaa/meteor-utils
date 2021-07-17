package com.franek.meteor_tweaks.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import minegame159.meteorclient.systems.commands.Command;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static minegame159.meteorclient.utils.Utils.mc;

public class Test extends Command {
	public Test() {
		super("Test", "description");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		
		
		builder.executes(context -> {
			
			
			//info(mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).get(Properties.FACING).asString());
			if (mc.crosshairTarget instanceof BlockHitResult && mc.world != null && mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).getBlock() instanceof ChestBlock) {
				info(ChestBlock.getFacing(mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos())).asString());
			}
			
			return SINGLE_SUCCESS;
		});
		
		
	}
}
