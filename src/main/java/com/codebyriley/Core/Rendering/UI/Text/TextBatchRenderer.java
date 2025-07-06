package com.codebyriley.Core.Rendering.UI.Text;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import com.codebyriley.Core.Rendering.WindowBase;
import com.codebyriley.Core.Rendering.Shaders.ShaderLoader;
import static org.lwjgl.opengl.GL33.*;


public class TextBatchRenderer {
    private int vao, vbo, shaderProgram;
    private FloatBuffer vertexBuffer;
    private static final int VERTEX_SIZE = 4; // x, y, u, v
    private static final int MAX_CHARS = 1024;

    public TextBatchRenderer() {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, MAX_CHARS * 6 * VERTEX_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);

        // Position (location 0): x, y
        glVertexAttribPointer(0, 2, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        // TexCoord (location 1): u, v
        glVertexAttribPointer(1, 2, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);

        vertexBuffer = BufferUtils.createFloatBuffer(MAX_CHARS * 6 * VERTEX_SIZE);

        // Load shaders
        String vertSource = ShaderLoader.readShaderFromResource("/shaders/TextVertexShader.vert.glsl");
        String fragSource = ShaderLoader.readShaderFromResource("/shaders/TextFragmentShader.frag.glsl");
        shaderProgram = ShaderLoader.createShaderProgram(vertSource, fragSource);
    }

    public void begin() {
        vertexBuffer.clear();
    }

    // Add a quad for each character (using your font's baked data)
    public void addCharQuad(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1) {
        // 2 triangles per quad
        vertexBuffer.put(new float[]{
            x0, y0, u0, v0,
            x1, y0, u1, v0,
            x1, y1, u1, v1,
            x0, y0, u0, v0,
            x1, y1, u1, v1,
            x0, y1, u0, v1
        });
    }

    public void end(int fontTextureId, float r, float g, float b, float alpha) {
        vertexBuffer.flip();
        glUseProgram(shaderProgram);

        int err = glGetError();
        if (err != GL_NO_ERROR) {
            System.err.println("OpenGL Error after text draw: " + err);
        }

        int windowSizeLoc = glGetUniformLocation(shaderProgram, "uWindowSize");
        glUniform2f(windowSizeLoc, WindowBase.windowWidth, WindowBase.windowHeight);

        int colorLoc = glGetUniformLocation(shaderProgram, "uTextColor");
        glUniform3f(colorLoc, r, g, b);

        int alphaLoc = glGetUniformLocation(shaderProgram, "uTextAlpha");
        glUniform1f(alphaLoc, alpha); // Pass the label's alpha

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, fontTextureId);
        int fontAtlasLoc = glGetUniformLocation(shaderProgram, "uFontAtlas");
        glUniform1i(fontAtlasLoc, 0);

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer);

        glDrawArrays(GL_TRIANGLES, 0, vertexBuffer.limit() / VERTEX_SIZE);

        glBindVertexArray(0);
        glUseProgram(0);
    }

    public void destroy() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteProgram(shaderProgram);
    }
}