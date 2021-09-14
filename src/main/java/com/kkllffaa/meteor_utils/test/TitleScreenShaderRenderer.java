package com.kkllffaa.meteor_utils.test;

import com.kkllffaa.meteor_utils.mixins.RotatingCubeMapRendererAccessor;
import com.kkllffaa.meteor_utils.utils.MyShader;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;

import java.io.File;

import static org.lwjgl.opengl.GL32C.*;

public class TitleScreenShaderRenderer extends RotatingCubeMapRenderer {
	public TitleScreenShaderRenderer(CubeMapRenderer cubeMap) {
		super(cubeMap);
		
		shader = new MyShader(new File("C:/Users/Franek/Desktop/l/shader.vert"), new File("C:/Users/Franek/Desktop/l/shader.frag"));
		
		
		vao = glGenVertexArrays();
		int verts = glGenBuffers();
		
		glBindVertexArray(vao);
		glBindBuffer( GL_ARRAY_BUFFER, verts);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 5*4, 0);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 5*4, 2*4);
		
		
	}
	
	private final MyShader shader;
	
	private RotatingCubeMapRendererAccessor accessor() {
		return (RotatingCubeMapRendererAccessor) this;
	}
	
	
	float[] vertices =
			{
					//pos	//color
					-0.5f,  0.5f, 1f, 0f, 0f, // top left
					 0.5f,  0.5f, 0f, 1f, 0f, // top right
					-0.5f, -0.5f, 0f, 0f, 1f, // bottom left
					
					 0.5f,  0.5f, 0f, 1f, 0f, // top right
					 0.5f, -0.5f, 0f, 1f, 1f, // bottom right
					-0.5f, -0.5f, 0f, 0f, 1f, // bottom left
			};
	
	int vao;
	
	@Override
	public void render(float delta, float alpha) {
		accessor().setTime(accessor().getTime()+delta);
		
		
		glClearColor(0, 0.4f, 0.8f, 1);
		glClear(GL_COLOR_BUFFER_BIT);
		
		shader.use(() -> {
			glBindVertexArray(vao);
			glDrawArrays(GL_TRIANGLES, 0, 6);
		});
	}
	
}
