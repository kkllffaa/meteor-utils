package com.kkllffaa.meteorutils.test;

import com.kkllffaa.meteorutils.mixins.RotatingCubeMapRendererAccessor;
import com.kkllffaa.meteorutils.modules.CustomTitleScreen;
import meteordevelopment.meteorclient.renderer.DrawMode;
import meteordevelopment.meteorclient.renderer.Mesh;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static org.lwjgl.opengl.GL32C.*;

public class TitleScreenShaderRenderer extends RotatingCubeMapRenderer {
	public TitleScreenShaderRenderer(CubeMapRenderer cubeMap) {
		super(cubeMap);
		
	}
	
	private static MyRenderer renderer;
	
	public static void init() {
		renderer = new MyRenderer(
				new MyShader("shader.vert", "shader.frag", false),
				DrawMode.Triangles,
				Mesh.Attrib.Vec2, Mesh.Attrib.Color);
	}
	
	
	
	private RotatingCubeMapRendererAccessor accessor() {
		return (RotatingCubeMapRendererAccessor) this;
	}
	
	
	@Override
	public void render(float delta, float alpha) {
		accessor().setTime(accessor().getTime()+delta);
		float time = accessor().getTime();
		
		if (!Modules.get().isActive(CustomTitleScreen.class)) {
			accessor().getCubeMap().draw(accessor().getClient(), MathHelper.sin(time * 0.001F) * 5.0F + 25.0F, -time * 0.1F, alpha);
			return;
		}
		
		
		
		
		glClearColor(0.3f, 0.9f, 0.4f, 1);
		glClear(GL_COLOR_BUFFER_BIT);
		
		renderer.begin();
		renderer.quad(0, 0, Utils.getWindowWidth()/2f, Utils.getWindowHeight()/2f,
				new Color(1f, 0f, 0f, 1f),
				new Color(0f, 1f, 0f, 1f),
				new Color(0f, 0f, 1f, 1f),
				new Color(1f, 1f, 1f, 1f));
		renderer.render();
	}
	
	
	
	
	
	
	private void renderRetardedTexture(Identifier texture, float time) {
		mc.getTextureManager().bindTexture(texture);
		
		Pair<Float, Float> h = calc(time/10, Utils.getWindowHeight());
		
		Pair<Float, Float> w = calc(time/-10, Utils.getWindowWidth());
		
		
		Renderer2D.TEXTURE.begin();
		Renderer2D.TEXTURE.texQuad(w.getRight(), h.getRight(), w.getLeft(), h.getLeft(), new Color(1f, 1f, 1f, 1));
		Renderer2D.TEXTURE.render(null);
	}
	private static Pair<Float, Float> calc(float t, int w) {
		
		float h = (float) MathHelper.clamp(((w)*(Math.sin(t)/2+0.5)), w/2f, w);
		float h2 = ((w/2f)-h/2)-(w/4f);
		
		
		
		return new Pair<>(h, h2);
	}
	
}
