package com.kkllffaa.meteorutils.test;

import meteordevelopment.meteorclient.renderer.DrawMode;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Mesh;
import meteordevelopment.meteorclient.utils.render.color.Color;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL32C.*;

public class MyRenderer {
    
    private final MyShader shader;

    public boolean depthTest = false;
    public double alpha = 1;

    private final DrawMode drawMode;
    private final int primitiveVerticesSize;

    private final int vao, vbo, ibo;

    private ByteBuffer vertices;
    private ByteBuffer indices;
    private int vertexI, indicesCount;

    private boolean building;
    private boolean beganRendering;

    public MyRenderer(MyShader shader, DrawMode drawMode, Mesh.Attrib... attributes) {
        int stride = 0;
        for (Mesh.Attrib attribute : attributes) stride += attribute.size * 4;

        this.drawMode = drawMode;
        this.primitiveVerticesSize = stride * drawMode.indicesCount;

        vertices = BufferUtils.createByteBuffer(primitiveVerticesSize * 256 * 4);
        indices = BufferUtils.createByteBuffer(drawMode.indicesCount * 512 * 4);

        vao = GL.genVertexArray();
        GL.bindVertexArray(vao);

        vbo = GL.genBuffer();
        GL.bindVertexBuffer(vbo);

        ibo = GL.genBuffer();
        GL.bindIndexBuffer(ibo);

        int offset = 0;
        for (int i = 0; i < attributes.length; i++) {
            int attribute = attributes[i].size;

            GL.enableVertexAttribute(i);
            GL.vertexAttribute(i, attribute, GL_FLOAT, false, stride, offset);

            offset += attribute * 4;
        }

        GL.bindVertexArray(0);
        GL.bindVertexBuffer(0);
        GL.bindIndexBuffer(0);
    
        this.shader = shader;
    }

    public void begin() {
        if (building) throw new IllegalStateException("Mesh.end() called while already building.");

        vertices.clear();
        indices.clear();

        vertexI = 0;
        indicesCount = 0;

        building = true;
        
    }

    public MyRenderer vec3(double x, double y, double z) {
        vertices.putFloat((float) (x));
        vertices.putFloat((float) y);
        vertices.putFloat((float) (z));

        return this;
    }

    public MyRenderer vec2(double x, double y) {
        vertices.putFloat((float) x);
        vertices.putFloat((float) y);

        return this;
    }

    public MyRenderer color(Color c) {
        vertices.putFloat(c.r / 255f);
        vertices.putFloat(c.g / 255f);
        vertices.putFloat(c.b / 255f);
        vertices.putFloat(c.a / 255f * (float) alpha);

        return this;
    }

    public int next() {
        return vertexI++;
    }

    public void line(int i1, int i2) {
        indices.putInt(i1);
        indices.putInt(i2);

        indicesCount += 2;
        growIfNeeded();
    }

    public void quad(int i1, int i2, int i3, int i4) {
        indices.putInt(i1);
        indices.putInt(i2);
        indices.putInt(i3);

        indices.putInt(i3);
        indices.putInt(i4);
        indices.putInt(i1);

        indicesCount += 6;
        growIfNeeded();
    }

    public void growIfNeeded() {
        // Vertices
        if ((vertexI + 1) * primitiveVerticesSize >= vertices.capacity()) {
            int newSize = vertices.capacity() * 2;
            if (newSize % primitiveVerticesSize != 0) newSize += newSize % primitiveVerticesSize;

            ByteBuffer newVertices = BufferUtils.createByteBuffer(newSize);

            vertices.flip();
            newVertices.put(vertices);

            vertices = newVertices;
        }

        // Indices
        if (indicesCount * 4 >= indices.capacity()) {
            int newSize = indices.capacity() * 2;
            if (newSize % drawMode.indicesCount != 0) newSize += newSize % (drawMode.indicesCount * 4);

            ByteBuffer newIndices = BufferUtils.createByteBuffer(newSize);

            indices.flip();
            newIndices.put(indices);

            indices = newIndices;
        }
    }

    public void end() {
        if (!building) throw new IllegalStateException("Mesh.end() called while not building.");

        if (indicesCount > 0) {
            vertices.flip();
            GL.bindVertexBuffer(vbo);
            GL.bufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
            GL.bindVertexBuffer(0);

            indices.flip();
            GL.bindIndexBuffer(ibo);
            GL.bufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);
            GL.bindIndexBuffer(0);
        }

        building = false;
    }

    public void beginRender() {
        GL.saveState();

        if (depthTest) GL.enableDepth();
        else GL.disableDepth();
        GL.enableBlend();
        GL.disableCull();
        GL.enableLineSmooth();

        beganRendering = true;
    }

    public void render() {
        if (building) end();

        if (indicesCount > 0) {
            // Setup opengl state and matrix stack
            boolean wasBeganRendering = beganRendering;
            if (!wasBeganRendering) beginRender();

            // Render
            beforeRender();

            MyShader.BOUND.setDefaults();

            GL.bindVertexArray(vao);
            GL.drawElements(drawMode.getGL(), indicesCount, GL_UNSIGNED_INT);

            // Cleanup opengl state and matrix stack
            GL.bindVertexArray(0);

            if (!wasBeganRendering) endRender();
        }
    }

    public void endRender() {

        GL.restoreState();

        beganRendering = false;
    }

    private void beforeRender() {
        shader.bind();
    }
    
    
    
    
    
    public void quad(double x, double y, double width, double height, Color cTopLeft, Color cTopRight, Color cBottomRight, Color cBottomLeft) {
        quad(
                vec2(x, y).color(cTopLeft).next(),
                vec2(x, y + height).color(cBottomLeft).next(),
                vec2(x + width, y + height).color(cBottomRight).next(),
                vec2(x + width, y).color(cTopRight).next()
        );
    }
    
    public void quad(double x, double y, double width, double height, Color color) {
        quad(x, y, width, height, color, color, color, color);
    }
}
