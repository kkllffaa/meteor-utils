package com.kkllffaa.meteorutils.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.commands.Command;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class AddWaypoint extends Command {
	public AddWaypoint(){super("addwaypoint","adds waypoint via command in actual coords");}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("name", StringArgumentType.string()).executes(context -> {
			
			if (mc.player == null) return -1;
			
			Waypoint waypoint = new Waypoint.Builder()
					.name(context.getArgument("name", String.class))
					.pos(new BlockPos((int) mc.player.getX(), (int) mc.player.getY() + 2, (int) mc.player.getZ()))
					.dimension(PlayerUtils.getDimension())
					.build();
			
			Waypoints.get().add(waypoint);
			
			return SINGLE_SUCCESS;
		}));
	}
}
