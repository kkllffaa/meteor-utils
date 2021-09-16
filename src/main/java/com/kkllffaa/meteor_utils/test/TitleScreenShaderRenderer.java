package com.kkllffaa.meteor_utils.test;

import com.kkllffaa.meteor_utils.mixins.RotatingCubeMapRendererAccessor;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import static org.lwjgl.opengl.GL32C.*;

public class TitleScreenShaderRenderer extends RotatingCubeMapRenderer {
	public TitleScreenShaderRenderer(CubeMapRenderer cubeMap) {
		super(cubeMap);
	}
	
	
	private RotatingCubeMapRendererAccessor accessor() {
		return (RotatingCubeMapRendererAccessor) this;
	}
	
	
	public static Identifier TEXTURE_TRANSPARENT = new Identifier("meteor-utils", "a.png");
	
	
	@Override
	public void render(float delta, float alpha) {
		accessor().setTime(accessor().getTime()+delta);
		
		
		glClearColor(0, 0.4f, 0.8f, 1);
		glClear(GL_COLOR_BUFFER_BIT);
		
		Utils.mc.getTextureManager().bindTexture(TEXTURE_TRANSPARENT);
		
		Color color = new Color(1f, 1f, 1f, 1);
		
		Pair<Float, Float> h = calc(accessor().getTime()/10, Utils.getWindowHeight());
		
		Pair<Float, Float> w = calc(accessor().getTime()/-10, Utils.getWindowWidth());
		
		
		Renderer2D.TEXTURE.begin();
		Renderer2D.TEXTURE.texQuad(w.getRight(), h.getRight(), w.getLeft(), h.getLeft(), color);
		Renderer2D.TEXTURE.render(null);
		
	}
	
	
	
	private static Pair<Float, Float> calc(float t, int w) {
		
		float h = (float) MathHelper.clamp(((w)*(Math.sin(t)/2+0.5)), w/2f, w);
		float h2 = ((w/2f)-h/2)-(w/4f);
		
		
		
		return new Pair<>(h, h2);
	}
	
}
