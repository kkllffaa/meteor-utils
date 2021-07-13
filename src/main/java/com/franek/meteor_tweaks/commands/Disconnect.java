package com.franek.meteor_tweaks.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import minegame159.meteorclient.systems.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.LiteralText;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Disconnect extends Command {
 
	public Disconnect() {
		super("disconnect", "diconnect and reconnect to actual server");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
			
			
			//assert mc.player != null;
			mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("Disconnected via command")));
			
			return SINGLE_SUCCESS;
		});
	}
}
