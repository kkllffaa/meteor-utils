package com.franek.meteor_tweaks.modules;

import minegame159.meteorclient.gui.GuiTheme;
import minegame159.meteorclient.gui.widgets.WWidget;
import minegame159.meteorclient.gui.widgets.containers.WTable;
import minegame159.meteorclient.gui.widgets.input.WTextBox;
import minegame159.meteorclient.gui.widgets.pressable.WMinus;
import minegame159.meteorclient.gui.widgets.pressable.WPlus;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.List;

public class ChatFilter extends Module {
    public ChatFilter() {
        super(Categories.Misc, "chat filter", "filter specific phrases in chat");
    }

    public final List<String> messages = new ArrayList<>();






    @Override
    public WWidget getWidget(GuiTheme theme) {
        messages.removeIf(String::isEmpty);

        WTable table = theme.table();
        fillTable(theme, table);

        return table;
    }

    private void fillTable(GuiTheme theme, WTable table) {
        table.add(theme.horizontalSeparator("Messages")).expandX();
        table.row();

        // Messages
        for (int i = 0; i < messages.size(); i++) {
            int msgI = i;
            String message = messages.get(i);

            WTextBox textBox = table.add(theme.textBox(message)).minWidth(100).expandX().widget();
            textBox.action = () -> messages.set(msgI, textBox.get());

            WMinus delete = table.add(theme.minus()).widget();
            delete.action = () -> {
                messages.remove(msgI);

                table.clear();
                fillTable(theme, table);
            };

            table.row();
        }

        // New Message
        WPlus add = table.add(theme.plus()).expandCellX().right().widget();
        add.action = () -> {
            messages.add("");

            table.clear();
            fillTable(theme, table);
        };
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = super.toTag();

        messages.removeIf(String::isEmpty);
        NbtList messagesTag = new NbtList();

        for (String message : messages) messagesTag.add(NbtString.of(message));
        tag.put("messages", messagesTag);

        return tag;
    }

    @Override
    public Module fromTag(NbtCompound tag) {
        messages.clear();

        if (tag.contains("messages")) {
            NbtList messagesTag = tag.getList("messages", 8);
            for (NbtElement messageTag : messagesTag) messages.add(messageTag.asString());
        } else {
            messages.add("Meteor on Crack!");
        }

        return super.fromTag(tag);
    }
}
