package com.kkllffaa.meteorutils.hud;

import com.kkllffaa.meteorutils.Addon;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ElytraDurability extends HudElement {
	public static final HudElementInfo<ElytraDurability> INFO = new HudElementInfo<>(Addon.HUD_GROUP, "elytra-durability", "displays elytra durability.", ElytraDurability::new);
	
	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
	private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
			.name("scale")
			.description("The scale.")
			.defaultValue(2)
			.onChanged(aDouble -> calculateSize())
			.min(1)
			.sliderRange(1, 5)
			.build()
	);
	
	private final Setting<SettingColor> textcolor = sgGeneral.add(new ColorSetting.Builder()
			.name("text-color")
			.description("Color of durability text.")
			.defaultValue(new SettingColor(0, 0, 0, 255))
			.build()
	);
	
	
	public ElytraDurability() {
		super(INFO);
		calculateSize();
	}
	
	
	private void calculateSize() {
		setSize(16 * scale.get() * 3, 16 * scale.get());
	}
	
	private static final Color red = new Color(255, 15, 15);
	
	@Override
	public void render(HudRenderer renderer) {
		renderer.post(() -> {
			ItemStack elytra;
			if (mc.player == null) {
				if (!isInEditor()) return;
				elytra = Items.ELYTRA.getDefaultStack();
				elytra.setDamage(200);
			}else {
				elytra = mc.player.getInventory().getArmorStack(2);
				if (elytra.isEmpty() || !elytra.isDamageable() || elytra.getItem() != Items.ELYTRA) return;
			}
			
			double x = this.x;
			double y = this.y;
			
			
			float percentage = ((elytra.getMaxDamage()-1) - elytra.getDamage()) / ((float)elytra.getMaxDamage()-1);
			
			if (percentage != 0) {
				
				
				final Color green = new Color((int) (15+((255-15)*(1 - percentage))), (int) (15+((255-15)*percentage)), 15);
				
				Renderer2D.COLOR.begin();
				Renderer2D.COLOR.quad(x, y, (getWidth() * percentage), getHeight(), red, green, green, red);
				Renderer2D.COLOR.render(null);
			}

			RenderUtils.drawItem(renderer.drawContext, elytra, (int) x, (int) y, scale.get().floatValue(), false);
			
			TextRenderer textRenderer = TextRenderer.get();
			
			textRenderer.begin(scale.get()/2);
			
			String dur = String.valueOf(elytra.getMaxDamage() - elytra.getDamage());
			
			textRenderer.render(dur, x+getWidth()-textRenderer.getWidth(dur)-2, y + (getHeight()-(textRenderer.getHeight()/2)) - (getHeight()/(float)2) - (textRenderer.getHeight()/2), textcolor.get(), false);
			dur = String.valueOf(elytra.getMaxDamage());
			textRenderer.render(dur, x+getWidth()-textRenderer.getWidth(dur)-2, y + (getHeight()-(textRenderer.getHeight()/2)) - (getHeight()/(float)2) + (textRenderer.getHeight()/2), textcolor.get(), false);
			
			textRenderer.end();
			
		});
		
		
		
		
	}
}
