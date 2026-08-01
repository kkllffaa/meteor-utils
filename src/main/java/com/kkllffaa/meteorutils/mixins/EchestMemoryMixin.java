package com.kkllffaa.meteorutils.mixins;

import com.kkllffaa.meteorutils.modules.EchestSave;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.player.NameProtect;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.EChestMemory;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.kkllffaa.meteorutils.modules.EchestSave.loc;
import static meteordevelopment.meteorclient.MeteorClient.LOG;
import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(EChestMemory.class) // in mixin because GameJoinedEvent dont fire on modules
public abstract class EchestMemoryMixin {

	@Shadow @Final
	public static NonNullList<ItemStack> ITEMS;

	private static File getSaveFile() {
		if (!Modules.get().isActive(EchestSave.class))
			return null;
		if (mc.player == null || Utils.getFileWorldName().isEmpty() || Modules.get().isActive(NameProtect.class))
			return null;
		if (mc.isSingleplayer()) {
			return new File(new File(loc, "singleplayer"), Utils.getFileWorldName() + ".nbt"); // TODO: saves as 'minecraft.nbt' ???
		} else if (!Modules.get().get(EchestSave.class).respectdifftentnicks.get()) {
			return new File(new File(loc, "all"), Utils.getFileWorldName() + ".nbt"); // TODO: check if working for multi and with profiles
		} else {
			return new File(new File(loc, mc.player.getGameProfile().name()), Utils.getFileWorldName() + ".nbt");
		}
	}

	@EventHandler
	private static void save(GameLeftEvent event) {
		File file = getSaveFile();
		if (file == null)
			return;

		LOG.info("saving echest data to file: " + file.toString());

		CompoundTag tag = new CompoundTag();
		ListTag list = new ListTag();
		for (ItemStack stack : ITEMS) {

			RegistryAccess acc = mc.level.registryAccess();

			var tagg = ItemStack.OPTIONAL_CODEC.encodeStart(acc.createSerializationContext(NbtOps.INSTANCE), stack);

			list.add(tagg.getOrThrow());

		}

		tag.put("items", list);
		try {

			loc.mkdir();
			file.getParentFile().mkdir();
			file.createNewFile();

			NbtIo.write(tag, file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	private static void load(GameJoinedEvent event) {
		File file = getSaveFile();
		if (file == null)
			return;
		LOG.info("attempting to load echest data from file: " + file.toString());

		CompoundTag tag = null;
		try {
			tag = NbtIo.read(getSaveFile().toPath());
		} catch (IOException e) {
			if (!(e instanceof FileNotFoundException))
				e.printStackTrace();
			LOG.info("cant find file: " + file.toString());
		}
		ITEMS.clear();
		if (tag == null || !tag.contains("items"))
			return;

		ListTag list = tag.getList("items").get();

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof CompoundTag) {
				RegistryAccess acc = mc.level.registryAccess();

				var item = ItemStack.OPTIONAL_CODEC.parse(acc.createSerializationContext(NbtOps.INSTANCE), list.get(i));

				ITEMS.set(i, item.getOrThrow());

			}
		}

	}
}
