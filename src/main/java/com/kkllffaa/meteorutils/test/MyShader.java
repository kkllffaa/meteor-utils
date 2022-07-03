package com.kkllffaa.meteorutils.test;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.GL;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static org.lwjgl.opengl.GL32C.*;

public class MyShader {
    public static MyShader BOUND;

    private final int id;
    private final Object2IntMap<String> uniformLocations = new Object2IntOpenHashMap<>();

    public MyShader(String vertPath, String fragPath, boolean fromdisk) {
        int vert = GL.createShader(GL_VERTEX_SHADER);
        GL.shaderSource(vert, read(vertPath, fromdisk));

        String vertError = GL.compileShader(vert);
        if (vertError != null) {
            MeteorClient.LOG.error("Failed to compile vertex shader (" + vertPath + "): " + vertError);
            throw new RuntimeException("Failed to compile vertex shader (" + vertPath + "): " + vertError);
        }

        int frag = GL.createShader(GL_FRAGMENT_SHADER);
        GL.shaderSource(frag, read(fragPath, fromdisk));

        String fragError = GL.compileShader(frag);
        if (fragError != null) {
            MeteorClient.LOG.error("Failed to compile fragment shader (" + fragPath + "): " + fragError);
            throw new RuntimeException("Failed to compile fragment shader (" + fragPath + "): " + fragError);
        }

        id = GL.createProgram();

        String programError = GL.linkProgram(id, vert, frag);
        if (programError != null) {
            MeteorClient.LOG.error("Failed to link program: " + programError);
            throw new RuntimeException("Failed to link program: " + programError);
        }

        GL.deleteShader(vert);
        GL.deleteShader(frag);
    }

    private String read(String path, boolean fromdisk) {
        try {
            return IOUtils.toString(mc.getResourceManager().getResource(new Identifier("meteor-utils", path)).get().getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void bind() {
        GL.useProgram(id);
        BOUND = this;
    }

    private int getLocation(String name) {
        if (uniformLocations.containsKey(name)) return uniformLocations.getInt(name);

        int location = GL.getUniformLocation(id, name);
        uniformLocations.put(name, location);
        return location;
    }

    public void set(String name, boolean v) {
        GL.uniformInt(getLocation(name), v ? GL_TRUE : GL_FALSE);
    }

    public void set(String name, int v) {
        GL.uniformInt(getLocation(name), v);
    }

    public void set(String name, double v) {
        GL.uniformFloat(getLocation(name), (float) v);
    }

    public void set(String name, double v1, double v2) {
        GL.uniformFloat2(getLocation(name), (float) v1, (float) v2);
    }

    public void set(String name, Matrix4f mat) {
        GL.uniformMatrix(getLocation(name), mat);
    }

    public void setDefaults() {
        set("u_Proj", RenderSystem.getProjectionMatrix());
        set("u_ModelView", RenderSystem.getModelViewStack().peek().getPositionMatrix());
    }
}
