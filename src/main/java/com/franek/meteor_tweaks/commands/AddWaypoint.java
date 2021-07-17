package com.franek.meteor_tweaks.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import minegame159.meteorclient.systems.commands.Command;
import minegame159.meteorclient.systems.waypoints.Waypoint;
import minegame159.meteorclient.systems.waypoints.Waypoints;
import minegame159.meteorclient.utils.player.PlayerUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class AddWaypoint extends Command {
	public AddWaypoint(){super("add_waypoint","adds waypoint via command in actual coords");}
	
	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("name", StringArgumentType.string()).executes(context -> {
			
			if (mc.player == null) return -1;
			Waypoint waypoint = new Waypoint() {{
				name =  context.getArgument("name", String.class);
				actualDimension = PlayerUtils.getDimension();
				
				
				x = (int) mc.player.getX();
				y = (int) mc.player.getY() + 2;
				z = (int) mc.player.getZ();
				
				switch (actualDimension) {
					case Overworld -> overworld = true;
					case Nether -> nether = true;
					case End -> end = true;
				}
				
			}};
			
			Waypoints.get().add(waypoint);
			
			return SINGLE_SUCCESS;
		}));
	}
}
