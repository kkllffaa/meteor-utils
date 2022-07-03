package com.kkllffaa.meteorutils.modules;

import com.kkllffaa.meteorutils.Addon;
import com.kkllffaa.meteorutils.utils.MyInvUtils;
import com.kkllffaa.meteorutils.utils.screens.EditIntScreen;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.mixin.TextHandlerAccessor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;

public class BetterBookBot extends Module {
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	//region settings
	
	private final Setting<Boolean> sign = sgGeneral.add(new BoolSetting.Builder()
			.name("sign")
			.description("Whether to sign written books.")
			.defaultValue(true)
			.build()
	);
	
	private final Setting<String> name = sgGeneral.add(new StringSetting.Builder()
			.name("name")
			.description("The name you want to give your books.")
			.defaultValue("Meteor on Crack!")
			.visible(sign::get)
			.build()
	);
	
	private final Setting<Boolean> count = sgGeneral.add(new BoolSetting.Builder()
			.name("append-count")
			.description("Whether to append the number of the book to the title.")
			.defaultValue(true)
			.visible(sign::get)
			.build()
	);
	
	private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
			.name("delay")
			.description("The amount of delay between writing books.")
			.defaultValue(20)
			.min(1)
			.sliderMin(1).sliderMax(200)
			.build()
	);
	
	private final Setting<Boolean> filltomax = sgGeneral.add(new BoolSetting.Builder()
			.name("fill")
			.description("Whether to fill book or limit characters in one book.")
			.defaultValue(true)
			.build()
	);
	
	private final Setting<Integer> numberofcharacters = sgGeneral.add(new IntSetting.Builder()
			.name("characters")
			.description("The amount of characters to write in each book.")
			.defaultValue(100000)
			.min(1).max(100000)
			.sliderMin(1).sliderMax(100000)
			.visible(() -> !filltomax.get())
			.build()
	);
	
	private final Setting<Boolean> showiterator = sgGeneral.add(new BoolSetting.Builder()
			.name("showiterator")
			.description("Whether to show iterator after writing book.")
			.defaultValue(true)
			.build()
	);
	
	//endregion
	
	char[] filechars = null;
	
	private File file = new File(MeteorClient.FOLDER, "bookbot.txt");
	private final PointerBuffer filters;
	
	private int delayTimer;
	
	public int iterator, bookCount;
	
	public BetterBookBot() {
		super(Addon.CATEGORY, "file-book-bot", "book bot that can write file to multiple books");
		
		if (!file.exists()) {
			file = null;
		}
		
		filters = BufferUtils.createPointerBuffer(1);
		
		ByteBuffer txtFilter = MemoryUtil.memASCII("*.txt");
		
		filters.put(txtFilter);
		filters.rewind();
	}
	
	
	@Override
	public WWidget getWidget(GuiTheme theme) {
		WVerticalList list = theme.verticalList();
		calculatewidget(list, theme);
		return list;
	}
	
	private void calculatewidget(WVerticalList list, GuiTheme theme) {
		list.clear();
		WHorizontalList filebuttons = list.add(theme.horizontalList()).expandX().widget();
		
		
		filebuttons.add(theme.button((file != null) ? file.getName() : "Select File")).padLeft(6).expandX().widget().action = () -> {
			String path = TinyFileDialogs.tinyfd_openFileDialog(
					"Select File",
					new File(MeteorClient.FOLDER, "bookbot.txt").getAbsolutePath(),
					filters,
					null,
					false
			);
			if (path != null) {
				file = new File(path);
			}else file = null;
			buildstring();
			calculatewidget(list, theme);
		};
		filebuttons.add(theme.button(GuiRenderer.RESET)).padRight(6).widget().action = () -> {
			buildstring();
			calculatewidget(list, theme);
		};
		
		
		list.add(theme.button("iterator: "+iterator)).padHorizontal(6).expandX().widget().action = () -> mc.setScreen(new EditIntScreen(theme, "iterator", iterator, false, (a) -> {
			iterator = a;
			calculatewidget(list, theme);
		}));
		list.add(theme.button("count: "+bookCount)).padHorizontal(6).expandX().widget().action = () -> mc.setScreen(new EditIntScreen(theme, "count", bookCount, false, (a) -> {
			bookCount = a;
			calculatewidget(list, theme);
		}));
	}
	
	
	@EventHandler private void onDisconnected(GameLeftEvent event) { if (isActive()) toggle(); }
	@EventHandler private void onConnected(GameJoinedEvent event) { if (isActive()) toggle(); }
	
	@Override
	public void onActivate() {
		delayTimer = delay.get();
	}
	
	@EventHandler
	private void onTick(TickEvent.Post event) {
		
		if (mc.player == null) return;
		if (filechars == null || filechars.length <= 0 || !MyInvUtils.switchtoitem(Items.WRITABLE_BOOK, true, true, this)) {
			toggle();
			return;
		}
		// Check delay
		if (delayTimer > 0) {
			delayTimer--;
			return;
		}
		// Reset delay
		if (TickRate.INSTANCE.getTimeSinceLastTick() > 0.5f) return;
		delayTimer = delay.get();
		
		
		// Write book
		
		
		writeBook(filechars);
		
		
	}
	
	private boolean check(char[] chars) {
		return iterator >= chars.length;
	}
	
	private void writeBook(char[] chars) {
		if (mc.player == null) return;
		ArrayList<String> pages = new ArrayList<>();
		
		int booklenght = 0;
		
		if (check(chars)) {
			toggle();
			info("complete");
			return;
		}
		
		for (int pageI = 0; pageI < 100; pageI++) {
			if (check(chars)) break;
			if (!filltomax.get() && booklenght >= numberofcharacters.get()) break;
			
			StringBuilder page = new StringBuilder();
			
			for (int lineI = 0; lineI < 13; lineI++) {
				if (check(chars)) break;
				if (!filltomax.get() && booklenght >= numberofcharacters.get()) break;
				
				double lineWidth = 0;
				StringBuilder line = new StringBuilder();
				
				while (true) {
					if (check(chars)) break;
					if (!filltomax.get() && booklenght >= numberofcharacters.get()) break;
					
					char next = chars[iterator];
					iterator++;
					booklenght++;
					if (next == '\r') {
						if (chars.length > iterator && chars[iterator] == '\n') continue;
						break;
					}
					
					if (next == '\n') break;
					
					double charWidth = ((TextHandlerAccessor) mc.textRenderer.getTextHandler()).getWidthRetriever().getWidth(next, Style.EMPTY);
					if (lineWidth + charWidth > 114) break;
					line.appendCodePoint(next);
					lineWidth += charWidth;
				}
				page.append(line).append('\n');
			}
			pages.add(page.toString());
		}
		
		
		if (check(chars)) {
			toggle();
			info("complete");
		}
		
		
		// Write pages NBT
		NbtList pageNbt = new NbtList();
		pages.stream().map(NbtString::of).forEach(pageNbt::add);
		if (!pages.isEmpty()) mc.player.getMainHandStack().setSubNbt("pages", pageNbt);
		
		
		mc.player.networkHandler.sendPacket(new BookUpdateC2SPacket(mc.player.getInventory().selectedSlot, pages, sign.get() ?
				Optional.of(name.get() + (count.get() ? " #" + bookCount : "")) : Optional.empty()));
		
		
		bookCount++;
		
		
		if (showiterator.get()) {
			MutableText message = Text.literal("");
			message.append(Text.literal("iterator: ")).append(Text.literal(String.valueOf(iterator))
					.setStyle(Style.EMPTY
							.withFormatting(Formatting.UNDERLINE, Formatting.RED)
							.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(iterator)))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("COPY")))
					)
			);
			message.append(Text.literal("     "));
			message.append(Text.literal("bookcount: ")).append(Text.literal(String.valueOf(bookCount))
					.setStyle(Style.EMPTY
							.withFormatting(Formatting.UNDERLINE, Formatting.RED)
							.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(bookCount)))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("COPY")))
					)
			);
			info(message);
		}
	}
	
	private void buildstring() {
		if (file == null || !file.exists() || file.length() == 0) {
			info("The bookbot file is empty! ");
			filechars = null;
			iterator = 0;
			bookCount = 0;
			file = null;
			if (isActive()) toggle();
			return;
		}
		try (DataInputStream reader = new DataInputStream(new FileInputStream(file))) {
			
			filechars = new String(reader.readAllBytes(), StandardCharsets.UTF_8).toCharArray();
			
			iterator = 0;
			bookCount = 0;
			
		} catch (IOException ignored) {
			error("Failed to read the file.");
			filechars = null;
			iterator = 0;
			bookCount = 0;
			file = null;
		}
	}
	
	//region nbt
	@Override
	public NbtCompound toTag() {
		NbtCompound tag = super.toTag();
		
		if (file != null && file.exists()) {
			tag.putString("file", file.getAbsolutePath());
		}
		
		return tag;
	}
	
	@Override
	public Module fromTag(NbtCompound tag) {
		if (tag.contains("file")) {
			file = new File(tag.getString("file"));
			buildstring();
		}
		
		return super.fromTag(tag);
	}
	//endregion
}