package com.kkllffaa.meteorutils.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.EChestMemory;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class EchestPreview extends Command {
 
 
	public EchestPreview() {
		super("echestpeek", "open echest inventory");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
		 
			Utils.openContainer(new ItemStack(Items.ENDER_CHEST), EChestMemory.ITEMS.toArray(new ItemStack[0]), true);
			
			return SINGLE_SUCCESS;
			
		});
	}
}
