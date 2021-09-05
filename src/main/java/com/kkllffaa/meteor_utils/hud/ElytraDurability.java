package com.kkllffaa.meteor_utils.hud;

import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.render.hud.HUD;
import meteordevelopment.meteorclient.systems.modules.render.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.render.hud.modules.HudElement;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ElytraDurability extends HudElement {
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
			.name("scale")
			.description("The scale.")
			.defaultValue(2)
			.min(1)
			.sliderMin(1).sliderMax(5)
			.build()
	);
	
	private final Setting<SettingColor> textcolor = sgGeneral.add(new ColorSetting.Builder()
			.name("text-color")
			.description("Color of durability text.")
			.defaultValue(new SettingColor(0, 0, 0, 255))
			.build()
	);
	
	
	public ElytraDurability(HUD hud) {
		super(hud, "elytra-durability", "description");
	}
	
	@Override
	public void update(HudRenderer renderer) {
		box.setSize(16 * scale.get() * 3, 16 * scale.get());
	}
	
	private static final Color red = new Color(255, 15, 15);
	
	@Override
	public void render(HudRenderer renderer) {
		renderer.addPostTask(() -> {
			ItemStack elytra;
			if (mc.player == null) {
				if (!isInEditor()) return;
				elytra = Items.ELYTRA.getDefaultStack();
				elytra.setDamage(200);
			}else {
				elytra = mc.player.getInventory().getArmorStack(2);
				if (elytra.isEmpty() || !elytra.isDamageable() || elytra.getItem() != Items.ELYTRA) return;
			}
			
			double x = box.getX();
			double y = box.getY();
			
			
			float percentage = ((elytra.getMaxDamage()-1) - elytra.getDamage()) / ((float)elytra.getMaxDamage()-1);
			
			if (percentage != 0) {
				
				
				final Color green = new Color((int) (15+((255-15)*(1 - percentage))), (int) (15+((255-15)*percentage)), 15);
				
				Renderer2D.COLOR.begin();
				Renderer2D.COLOR.quad(x, y, (box.width * percentage), box.height, red, green, green, red);
				Renderer2D.COLOR.render(null);
			}
			
			RenderUtils.drawItem(elytra, (int) x, (int) y, scale.get(), false);
			
			TextRenderer textRenderer = TextRenderer.get();
			
			textRenderer.begin(scale.get()/2);
			
			String dur = String.valueOf(elytra.getMaxDamage() - elytra.getDamage());
			
			textRenderer.render(dur, x+box.width-textRenderer.getWidth(dur)-2, y + (box.height-(textRenderer.getHeight()/2)) - (box.height/2) - (textRenderer.getHeight()/2), textcolor.get(), false);
			dur = String.valueOf(elytra.getMaxDamage());
			textRenderer.render(dur, x+box.width-textRenderer.getWidth(dur)-2, y + (box.height-(textRenderer.getHeight()/2)) - (box.height/2) + (textRenderer.getHeight()/2), textcolor.get(), false);
			
			textRenderer.end();
			
		});
		
		
		
		
	}
}
