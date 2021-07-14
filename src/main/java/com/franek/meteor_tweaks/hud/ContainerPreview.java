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
import minegame159.meteorclient.utils.player.PlayerUtils;
import minegame159.meteorclient.utils.render.RenderUtils;
import minegame159.meteorclient.utils.render.color.SettingColor;
import minegame159.meteorclient.utils.world.Dimension;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3i;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


public class ContainerPreview extends HudElement {
	
	private static final Identifier TEXTURE = new Identifier("meteor-client", "textures/container.png");
	private static final Identifier TEXTURE_TRANSPARENT = new Identifier("meteor-client", "textures/container-transparent.png");
	ItemStack[] defaultstack = new ItemStack[27];
	
	
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
		//region initialize default stack
		defaultstack[0] = Items.TOTEM_OF_UNDYING.getDefaultStack();
		defaultstack[2] = Items.RED_SHULKER_BOX.getDefaultStack();
		defaultstack[5] = Items.BLACK_SHULKER_BOX.getDefaultStack();
		defaultstack[6] = Items.DIAMOND_CHESTPLATE.getDefaultStack();
		defaultstack[10] = new ItemStack(Items.TNT, 40);
		defaultstack[13] = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 6);
		defaultstack[19] = new ItemStack(Items.OBSIDIAN, 64);
		defaultstack[25] = Items.NETHERITE_AXE.getDefaultStack();
		//endregion
		//initialize container maps
		for (int i = 0; i < containerHashMap.length; i++) {
			containerHashMap[i] = new HashMap<>();
		}
		MeteorClient.EVENT_BUS.subscribe(this);
	}
	
	//region hash map
	@SuppressWarnings("unchecked")
	private final static HashMap<Vec3i,Container>[] containerHashMap = new HashMap[Dimension.values().length];
	
	public static HashMap<Vec3i, Container>[] getContainerHashMap() {
		return containerHashMap;
	}
	public static HashMap<Vec3i,Container> getactualdimContainerHashMap(){
		return containerHashMap[PlayerUtils.getDimension().ordinal()];
	}
	//endregion
	
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
			
			if (mc.world == null || !(mc.world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof AbstractChestBlock) || mc.world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof EnderChestBlock) return;
			
			Container con = getactualdimContainerHashMap().get(hitResult.getBlockPos());
			
			if (con == null) return;
			
			drawBackground((int) x, (int) y);
			
			drawItems(x, y, con.type.rows(),con.ITEMS);
		}else if (isInEditor()){
			drawBackground((int) box.getX(),(int) box.getY());
			drawItems(box.getX(),box.getY(),Container.Type.SingleChest.rows(),defaultstack);
		}
	}
	
	private void drawItems(double x, double y, int rows, ItemStack[] inventory) {
		for (int row = 0; row < rows; row++) {
			for (int i = 0; i < 9; i++) {
				ItemStack stack = inventory[row * 9 + i];
				if (stack == null || stack.isEmpty()) continue;
				
				RenderUtils.drawItem(stack, (int) (x + (8 + i * 18) * scale.get()), (int) (y + (7 + row * 18) * scale.get()), scale.get(), true);
			}
		}
	}
	
	private void drawBackground(int x, int y) {
		int w = (int) box.width;
		int h = (int) box.height;
		
		switch (background.get()) {
			case Texture, Outline -> {
				//noinspection deprecation
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
	
	private Vec3i pos;
	private int openedstate;
	
	
	@EventHandler
	private void onBlockActivate(BlockActivateEvent event) {
		if (!active) {
			openedstate = 0;
			pos = null;
			return;
		}
		if (openedstate == 0 && mc.crosshairTarget != null && event.blockState.getBlock() instanceof AbstractChestBlock && !(event.blockState.getBlock() instanceof EnderChestBlock)) {
			openedstate = 1;
			pos = new Vec3i(mc.crosshairTarget.getPos().x,mc.crosshairTarget.getPos().y,mc.crosshairTarget.getPos().z);
		}else if (openedstate == 0){
			pos = null;
		}
	}
	
	
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
		
		if (container == null || container.getInventory() == null || container.getInventory().isEmpty()) {
			openedstate = 0;
			pos = null;
			return;
		}
		Inventory inv = container.getInventory();
		
		if (pos != null) {
			
			Container container_temp = new Container(Container.Type.fromslots(inv.size()));
			if (container_temp.ITEMS == null) {
				openedstate = 0;
				pos = null;
				return;
			}
			
			for (int i = 0; i < inv.size(); i++) {
				
				if (inv.getStack(i).isEmpty()) continue;
				container_temp.ITEMS[i] = inv.getStack(i);
				
			}
			if (!Arrays.stream(container_temp.ITEMS).allMatch(Objects::isNull)) {
				
				if (getactualdimContainerHashMap().putIfAbsent(pos,container_temp) != null){
					getactualdimContainerHashMap().replace(pos,container_temp);
				}
				
				
			}else {
				getactualdimContainerHashMap().remove(pos);
			}
		}
		openedstate = 0;
		pos = null;
	}
	
	//endregion
	
}
