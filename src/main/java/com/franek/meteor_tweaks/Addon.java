package com.franek.meteor_tweaks;

import com.franek.meteor_tweaks.commands.*;
import com.franek.meteor_tweaks.modules.*;
import minegame159.meteorclient.MeteorAddon;
import minegame159.meteorclient.systems.commands.Commands;

import minegame159.meteorclient.systems.modules.Modules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Addon extends MeteorAddon {
	public static final Logger LOG = LogManager.getLogger();

	@Override
	public void onInitialize() {
		LOG.info("initializing meteor addon");

		// Commands
		Commands.get().add(new EchestPreview());
		Commands.get().add(new AddWaypoint());
		Commands.get().add(new Disconnect());
		Commands.get().add(new BookBot());
		Commands.get().add(new Test());

		// Modules
        Modules.get().add(new ChatFilter());
        Modules.get().add(new Strefe());

	}
}
