package com.franek.meteor_tweaks.hud;

import com.franek.meteor_tweaks.Addon;
import com.franek.meteor_tweaks.systems.chestmemory.ChestMemory;
import com.franek.meteor_tweaks.systems.chestmemory.ContainerC;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.data.client.model.Texture;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;

import java.util.Arrays;
import java.util.Objects;

public class ContainerPreview extends HudElement {
	
	private static final Identifier TEXTURESINGLE = new Identifier("meteor-client", "textures/container.png");
	private static final Identifier TEXTUREDOUBLE = new Identifier("meteor_tweaks", "textures/container-double.png");
	private static final Identifier TEXTURE_TRANSPARENT_SINGLE = new Identifier("meteor-client", "textures/container-transparent.png");
	private static final Identifier TEXTURE_TRANSPARENT_DOUBLE = new Identifier("meteor_tweaks", "textures/container-transparent-double.png");
	private final ItemStack[] defaultstack = new ItemStack[27];
	
	
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
		defaultstack[16] = new ItemStack(Items.OBSIDIAN, 64);
		defaultstack[23] = Items.NETHERITE_AXE.getDefaultStack();
		//endregion
	}
	
	//region render
	
	@Override
	public void update(HudRenderer renderer) {
		box.setSize(176 * scale.get(), (db ? 121 : 67) * scale.get());
	}
	
	private boolean db;
	
	@Override
	public void render(HudRenderer renderer) {
		if (!active) return;
		if (mc.crosshairTarget != null) {

			
			BlockHitResult hitResult;
			
			if (mc.crosshairTarget instanceof BlockHitResult){
				hitResult = (BlockHitResult) mc.crosshairTarget;
			}else {
				return;
			}
			
			if (mc.world == null || !ContainerC.goodblock(mc.world.getBlockState(hitResult.getBlockPos()).getBlock())) return;
			
			ContainerC con = ChestMemory.get(hitResult.getBlockPos());
			
			if (con == null) return;
			if (con.getType() == ContainerC.Type.Null || con.getITEMS() == null || Arrays.stream(con.getITEMS()).allMatch(Objects::isNull)) {
				ChestMemory.remove(hitResult.getBlockPos());
				return;
			}
			
			double x = box.getX();
			double y = box.getY();
			
			db = con.getType() == ContainerC.Type.DoubleChest;
			drawBackground((int) x, (int) y, db);
			drawItems(x, y, con.getType().rows(),con.asStack());
			
		}else if (isInEditor()){
			//todo add double chest rendering
			db = false;
			drawBackground((int) box.getX(),(int) box.getY(), db);
			drawItems(box.getX(),box.getY(), ContainerC.Type.SingleChest.rows(),defaultstack);
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
	
	private void drawBackground(int x, int y, boolean db) {
		int w = (int) box.width;
		int h = (int) box.height;
		
		switch (background.get()) {
			case Texture, Outline -> {
				//noinspection deprecation
				RenderSystem.color4f(color.get().r / 255F, color.get().g / 255F, color.get().b / 255F, color.get().a / 255F);
				mc.getTextureManager().bindTexture(background.get() == InventoryViewerHud.Background.Texture ? db ? TEXTUREDOUBLE : TEXTURESINGLE : db ? TEXTURE_TRANSPARENT_DOUBLE : TEXTURE_TRANSPARENT_SINGLE);
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
	
}
