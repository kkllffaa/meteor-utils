package com.franek.meteor_tweaks.hud;

import com.franek.meteor_tweaks.Addon;
import com.franek.meteor_tweaks.utils.Container;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.MeteorClient;
import minegame159.meteorclient.events.game.OpenScreenEvent;
import minegame159.meteorclient.events.world.BlockActivateEvent;
import minegame159.meteorclient.rendering.DrawMode;
import minegame159.meteorclient.rendering.Matrices;
import minegame159.meteorclient.rendering.Renderer;
import minegame159.meteorclient.settings.*;
import minegame159.meteorclient.systems.modules.render.hud.HUD;
import minegame159.meteorclient.systems.modules.render.hud.HudRenderer;
import minegame159.meteorclient.systems.modules.render.hud.modules.HudElement;
import minegame159.meteorclient.systems.modules.render.hud.modules.InventoryViewerHud;
import minegame159.meteorclient.utils.render.RenderUtils;
import minegame159.meteorclient.utils.render.color.SettingColor;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ContainerPreview extends HudElement {
	
	private static final Identifier TEXTURE = new Identifier("meteor-client", "textures/container.png");
	private static final Identifier TEXTURE_TRANSPARENT = new Identifier("meteor-client", "textures/container-transparent.png");
	
	//region settings
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	
	private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
			.name("scale")
			.description("Scale of inventory viewer.")
			.defaultValue(3)
			.min(0.1)
			.sliderMin(0.1)
			.max(10)
			.build()
	);
	
	private final Setting<InventoryViewerHud.Background> background = sgGeneral.add(new EnumSetting.Builder<InventoryViewerHud.Background>()
			.name("background")
			.description("Background of inventory viewer.")
			.defaultValue(InventoryViewerHud.Background.Texture)
			.build()
	);
	
	private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
			.name("background-color")
			.description("Color of the background.")
			.defaultValue(new SettingColor(255, 255, 255))
			.visible(() -> background.get() != InventoryViewerHud.Background.None)
			.build()
	);
	//endregion
	
	public ContainerPreview(HUD hud) {
		super(hud, "ContainerPreview", "description");
		MeteorClient.EVENT_BUS.subscribe(this);
		//for (int i = 0; i < containers.length; i++){
		//	containers[i] = new ArrayList<>();
		//}
		
		//Arrays.setAll(containers,element -> new ArrayList<>());
	}
	
	/**
	 * y , direction (com.franek.meteor_tweaks.utils.Container.ContainerCoordsSystem) , x , z
	 * x and z must be rounded
	 */
	public Container[][][][] contai = new Container[255][4][100][100];
	
	//public ArrayList<ArrayList<Container>>[] containers = new ArrayList[4];
	
	
	//region render
	
	@Override
	public void update(HudRenderer renderer) {
		box.setSize(176 * scale.get(), 67 * scale.get());
	}
	
	@Override
	public void render(HudRenderer renderer) {
		if (!active) return;
		if (mc.crosshairTarget != null) {
			double x = box.getX();
			double y = box.getY();
			
			
			
			BlockHitResult hitResult;
			
			if (mc.crosshairTarget instanceof BlockHitResult){
				hitResult = (BlockHitResult) mc.crosshairTarget;
			}else {
				return;
			}
			
			if (mc.world == null || !(mc.world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof ChestBlock)) return;
			
			Container con = Container.getContainerfromVec(hitResult.getBlockPos(),contai);
			
			if (con == null) return;
			
			drawBackground((int) x, (int) y);
			
			drawItems(x, y, 3,con.ITEMS);
		}
	}
	
	private void drawItems(double x, double y, int rows, ItemStack[] inventory) {
		for (int row = 0; row < rows; row++) {
			for (int i = 0; i < 9; i++) {
				ItemStack stack = inventory[row * 9 + i];
				if (stack == null) continue;
				
				RenderUtils.drawItem(stack, (int) (x + (8 + i * 18) * scale.get()), (int) (y + (7 + row * 18) * scale.get()), scale.get(), true);
			}
		}
	}
	
	private void drawBackground(int x, int y) {
		int w = (int) box.width;
		int h = (int) box.height;
		
		switch (background.get()) {
			case Texture, Outline -> {
				RenderSystem.color4f(color.get().r / 255F, color.get().g / 255F, color.get().b / 255F, color.get().a / 255F);
				mc.getTextureManager().bindTexture(background.get() == InventoryViewerHud.Background.Texture ? TEXTURE : TEXTURE_TRANSPARENT);
				DrawableHelper.drawTexture(Matrices.getMatrixStack(), x, y, 0, 0, 0, w, h, h, w);
			}
			case Flat -> {
				Renderer.NORMAL.begin(null, DrawMode.Triangles, VertexFormats.POSITION_COLOR);
				Renderer.NORMAL.quad(x, y, w, h, color.get());
				Renderer.NORMAL.end();
			}
		}
	}
	
	
	//endregion
	
	
	//region logic
	
	private int openedstate;
	
	@EventHandler
	private void onBlockActivate(BlockActivateEvent event) {
		if (!active) {
			openedstate = 0;
			pos = null;
			return;
		}
		if (event.blockState.getBlock() instanceof ChestBlock && openedstate == 0 && mc.crosshairTarget != null) {
			openedstate = 1;
			pos = new Vec3i(mc.crosshairTarget.getPos().x,mc.crosshairTarget.getPos().y,mc.crosshairTarget.getPos().z);
		}else if (openedstate == 0){
			pos = null;
		}
	}
	
	private Vec3i pos;
	
	
	
	@EventHandler
	private void onOpenScreenEvent(OpenScreenEvent event) {
		if (openedstate == 1 && event.screen instanceof GenericContainerScreen) {
			openedstate = 2;
			return;
		}
		if (openedstate == 0) return;
		if (!active) {
			openedstate = 0;
			return;
		}
		if (!(mc.currentScreen instanceof GenericContainerScreen)) return;
		GenericContainerScreenHandler container = ((GenericContainerScreen) mc.currentScreen).getScreenHandler();
		
		if (container == null) return;
		Inventory inv = container.getInventory();
		
		if (pos != null) {
			
			Container container_temp = new Container(inv.size(), pos);
			
			for (int i = 0; i < inv.size(); i++) {
				
				if (inv.getStack(i).isEmpty()) continue;
				container_temp.ITEMS[i] = inv.getStack(i);
				
			}
			if (container_temp.ITEMS.length > 0) {
				
				Container.setContainerfromVec(pos,contai,container_temp);
				
				
				Addon.LOG.info(Container.getContainerfromVec(pos,contai));
				
			}
			
		}
		
		
		openedstate = 0;
		pos = null;
	}
	
	//endregion
}
