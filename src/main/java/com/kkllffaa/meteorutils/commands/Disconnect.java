package com.kkllffaa.meteorutils.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;

public class Disconnect extends Command {

	public Disconnect() {
		super("disconnect", "diconnect and reconnect to actual server");
	}

	@Override
	public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
		builder.executes(context -> {

			if (mc.player != null) {
				mc.player.connection
						.onDisconnect(new DisconnectionDetails(Component.literal("Disconnected via command")));
			}

			return SINGLE_SUCCESS;
		});
	}
}
