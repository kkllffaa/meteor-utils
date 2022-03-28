package com.kkllffaa.meteorutils.utils.screens;

import com.kkllffaa.meteorutils.utils.MyUtils;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;

import java.util.function.Consumer;

public class EditIntScreen extends WindowScreen {
	public EditIntScreen(GuiTheme theme, String title, int value, boolean canbynegative, Consumer<Integer> function) {
		super(theme, title);
		WTextBox textBox = add(theme.textBox(String.valueOf(value), (text, c) -> canbynegative ? MyUtils.isUint(text+c) : MyUtils.isInt(text+c))).expandX().widget();
		WHorizontalList buttons = add(theme.horizontalList()).expandX().widget();
		buttons.add(theme.button("save")).expandX().widget().action = () -> {
			if (canbynegative ? MyUtils.isUint(textBox.get()) : MyUtils.isInt(textBox.get())) {
				function.accept(MyUtils.getInt(textBox.get()));
				close();
				//onClose();
			}else if (textBox.get().isEmpty()) {
				function.accept(0);
				close();
				//onClose();
			}
		};
		//buttons.add(theme.button("cancel")).expandX().widget().action = this::onClose;
		buttons.add(theme.button("cancel")).expandX().widget().action = this::close;
	}
	
	@Override
	public void initWidgets() {
	
	}
}
