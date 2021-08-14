package com.franek.meteor_tweaks.commands;

import com.franek.meteor_tweaks.modules.OpenAnarchyAutoDupe;
import com.franek.meteor_tweaks.utils.MyBlockUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Test extends Command {
	public Test() {
		super("Test", "description");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
			
			if (mc.world == null || mc.player == null || mc.interactionManager == null || mc.crosshairTarget == null) return SINGLE_SUCCESS;
			
			
			
			if (mc.crosshairTarget instanceof EntityHitResult && ((EntityHitResult) mc.crosshairTarget).getEntity() instanceof ItemFrameEntity) {
				int x = (int) ((EntityHitResult) mc.crosshairTarget).getEntity().getRotationVector().x;
				int y = (int) ((EntityHitResult) mc.crosshairTarget).getEntity().getRotationVector().y;
				int z = (int) ((EntityHitResult) mc.crosshairTarget).getEntity().getRotationVector().z;
				
				info(x + " " + y + " " + z);
			}
			
			
			

			
			return SINGLE_SUCCESS;
			
		});
	}
}
