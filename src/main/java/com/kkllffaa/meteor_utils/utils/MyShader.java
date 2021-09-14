package com.kkllffaa.meteor_utils.utils;

import com.kkllffaa.meteor_utils.Addon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.lwjgl.opengl.GL32C.*;

public class MyShader {
		private int ID;
		
		public MyShader(String vertex, String fragment) {
			create(vertex, fragment);
		}
		
		public MyShader(File vertex, File fragment) {
			create(vertex, fragment);
		}
		
		
		public void create(File vertex, File fragment) {
			if (vertex.isFile() && fragment.isFile()) {
				
				String vs = "";
				String fs = "";
				
				try {
					StringBuilder builder = new StringBuilder();
					List<String> lines = Files.readAllLines(vertex.toPath());
					for (String s : lines) {
						builder.append(s).append("\n");
					}
					vs = builder.toString();
					
					builder = new StringBuilder();
					lines = Files.readAllLines(fragment.toPath());
					for (String s : lines) {
						builder.append(s).append("\n");
					}
					fs = builder.toString();
					
				}catch (IOException e) {
					e.printStackTrace();
				}
				
				create(vs, fs);
			}
		}
		
		public void create(String vertex, String fragment) {
			if (vertex.isEmpty() || fragment.isEmpty()) {
				return;
			}
			if (ID != 0) {
				glDeleteProgram(ID);
			}
			
			
			ID = glCreateProgram();
			int vs = glCreateShader(GL_VERTEX_SHADER);
			int fs = glCreateShader(GL_FRAGMENT_SHADER);
			
			
			glShaderSource(vs, vertex);
			glCompileShader(vs);
			String log = glGetShaderInfoLog(vs);
			if (!log.isEmpty()) {
				Addon.LOG.error(log);
				glDeleteShader(vs);
				glDeleteProgram(ID);
				ID = 0;
				return;
			}
			
			
			glShaderSource(fs, fragment);
			glCompileShader(fs);
			log = glGetShaderInfoLog(fs);
			if (!log.isEmpty()) {
				Addon.LOG.error(log);
				glDeleteShader(vs);
				glDeleteShader(fs);
				glDeleteProgram(ID);
				ID = 0;
				return;
			}
			
			
			
			glAttachShader(ID, vs);
			glAttachShader(ID, fs);
			
			glLinkProgram(ID);
			
			
			glDetachShader(ID, vs);
			glDetachShader(ID, fs);
			glDeleteShader(vs);
			glDeleteShader(fs);
		}
		
		
		public void use() {
			glUseProgram(ID);
		}
		public void use(Runnable runnable) {
			use();
			runnable.run();
			glUseProgram(0);
		}
	}