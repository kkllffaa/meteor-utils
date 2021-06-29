package com.franek.meteor_tweaks.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import minegame159.meteorclient.systems.commands.Command;
import minegame159.meteorclient.utils.Utils;
import minegame159.meteorclient.utils.player.EChestMemory;
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
