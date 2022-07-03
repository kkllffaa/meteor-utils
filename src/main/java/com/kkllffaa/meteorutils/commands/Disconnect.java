package com.kkllffaa.meteorutils.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.systems.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Disconnect extends Command {
 
	public Disconnect() {
		super("disconnect", "diconnect and reconnect to actual server");
	}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.executes(context -> {
			
			
			if (mc.player != null) {
				mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.literal("Disconnected via command")));
			}
			
			return SINGLE_SUCCESS;
		});
	}
}
