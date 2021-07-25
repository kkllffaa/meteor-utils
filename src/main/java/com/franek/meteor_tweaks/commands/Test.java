package com.franek.meteor_tweaks.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import net.minecraft.block.ChestBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Test extends Command {
	public Test() {
		super("Test", "description");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		
		
		builder.executes(context -> {
			
			
			//info(mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).get(Properties.FACING).asString());
			if (mc.crosshairTarget instanceof BlockHitResult && mc.world != null && mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).getBlock() instanceof ChestBlock) {
				info(mc.world.getBlockState(((BlockHitResult) mc.crosshairTarget).getBlockPos()).get(Properties.CHEST_TYPE).name());
			}
			
			return SINGLE_SUCCESS;
		});
		
		
	}
}
