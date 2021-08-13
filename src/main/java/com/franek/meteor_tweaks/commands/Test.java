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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
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
			
			
			var ref = new Object() {
				BlockPos redstone = null;
			};
			
			
			MyBlockUtils.immediateBlockIterator(3, 3, (blockPos, blockState) -> {
				ref.redstone = blockPos.offset(Direction.UP);
				MyBlockUtils.disableCurrent();
			}, Blocks.OBSIDIAN);
			
			
			FindItemResult result = InvUtils.findInHotbar(Items.REDSTONE);
			
			
			BlockUtils.place(ref.redstone, result, false, 0, true, false);
			
			
			

			
			return SINGLE_SUCCESS;
			
		});
	}
}
