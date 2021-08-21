package com.franek.meteor_tweaks.modules;

import com.franek.meteor_tweaks.Addon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class AutoBreak extends Module {
	public AutoBreak() {
		super(Addon.CATEGORY, "auto-break", "description");
	}
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
			.name("break range")
			.description("a")
			.min(0)
			.max(5)
			.sliderMin(0)
			.sliderMax(5)
			.defaultValue(3)
			.build()
	);
	
	private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
			.name("block")
			.description("What blocks to break")
			.defaultValue(new ArrayList<>())
			.filter(s -> s.getHardness() == 0)
			.build()
	);
	
	
	@EventHandler
	public void onTick(TickEvent.Pre event) {
		BlockIterator.register(range.get(), range.get(), (blockPos, blockState) -> {
			if (blocks.get().stream().anyMatch(s -> s == blockState.getBlock())) {
				BlockUtils.breakBlock(blockPos, true);
			}
		});
	}
	
}
