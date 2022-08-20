package com.kkllffaa.meteorutils.tabs;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class WaypointsTab extends Tab {
	public WaypointsTab() {
		super("Waypoints");
	}
	
	@Override
	public TabScreen createScreen(GuiTheme theme) {
		return new WaypointsSerwerListScreen(theme, this);
	}
	
	@Override
	public boolean isScreen(Screen screen) {
		return screen instanceof WaypointsSerwerListScreen;
	}
	
	private static class WaypointsSerwerListScreen extends WindowTabScreen {
		public WaypointsSerwerListScreen(GuiTheme theme, Tab tab) {
			super(theme, tab);
		}
		
		@Override
		protected void init() {
			super.init();
			initWidget();
		}
		
		@Override
		public void initWidgets() {
		
		}
		
		private void initWidget() {
			clear();
			
			WTable table = add(theme.table()).expandX().minWidth(300).widget();
			File folder = new File(MeteorClient.FOLDER, "waypoints");
			File[] files = folder.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".nbt"));
			
			if (folder.exists() && folder.isDirectory() && files != null && files.length > 0) {
				for (File file : files) {
					if (file.equals(Waypoints.get().getFile())) continue;
					WLabel name = table.add(theme.label(file.getName())).expandX().widget();
					table.add(theme.button("view")).widget().action = () -> {
						try {
							mc.setScreen(new WaypointsListScreen(theme, file, this::initWidget));
						} catch (IOException e) {
							name.set(name.get()+" ERROR");
							e.printStackTrace();
						}
					};
					table.add(theme.button("delete")).widget().action = () -> {
						file.delete();
						initWidget();
					};
					table.row();
				}
			}else {
				table.add(theme.label("empty"));
			}
		}
	}
	
	private static class WIcon extends WWidget {
		private final Waypoint waypoint;
		
		public WIcon(Waypoint waypoint) {
			this.waypoint = waypoint;
		}
		
		@Override
		protected void onCalculateSize() {
			double s = theme.scale(32);
			
			width = s;
			height = s;
		}
		
		@Override
		protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
			renderer.post(() -> waypoint.renderIcon(x, y, 1, width));
		}
	}
	
	private static class WaypointsListScreen extends WindowScreen {
		private final Runnable action;
		private final Waypoints waypoints;
		public WaypointsListScreen(GuiTheme theme, File file, Runnable action) throws IOException {
			super(theme, file.getName());
			this.action = action;
			this.waypoints = new Waypoints().fromTag(NbtIo.read(file));
			
			
			WHorizontalList horizontalList = add(theme.horizontalList()).expandX().widget();
			horizontalList.add(theme.button("save")).expandX().widget().action = () -> {
				try {
					NbtIo.write(waypoints.toTag(), file);
				} catch (IOException e) {
					e.printStackTrace();
					close();
					//onClose();
				}
			};
			horizontalList.add(theme.button("delete")).expandX().widget().action = () -> {
				file.delete();
				close();
				//onClose();
			};
			
			
			add(theme.horizontalSeparator()).expandX();
			
			WTable table = add(theme.table()).widget();
			
			initWidget(table);
		}
		
		private void initWidget(WTable table) {
			table.clear();
			
			for (Waypoint waypoint : waypoints) {
				
				table.add(theme.label(waypoint.name.get()));
				table.add(new WIcon(waypoint));
				table.add(theme.label(waypoint.dimension.get().name()));
				table.add(theme.verticalSeparator()).expandWidgetY();
				table.add(theme.label(" X: "+waypoint.pos.get().getX()+" Y: "+waypoint.pos.get().getY()+" Z: "+waypoint.pos.get().getZ()+" "));
				table.add(theme.verticalSeparator()).expandWidgetY();
				table.add(theme.button("Remove")).widget().action = () -> {
					waypoints.remove(waypoint);
					initWidget(table);
				};
				
				table.row();
			}
		}
		
		@Override
		public void initWidgets() {
		
		}
		
		@Override
		protected void onClosed() {
			action.run();
		}
	}
}
