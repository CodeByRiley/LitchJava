package com.codebyriley.Core.Rendering.Text;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public class TextRenderer {
    private FontLoader font;
    private int vao, vbo;
    private int shaderProgram;
    private int uProjectionLoc, uTextColorLoc, uFontAtlasLoc;
    private int windowWidth, windowHeight;
    private FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(6 * 4); // Reusable buffer
    
    // Cached projection matrix to avoid recalculation
    private float[] projectionMatrix = new float[16];
    private boolean projectionDirty = true;

    public TextRenderer(FontLoader font, int windowWidth, int windowHeight) {
        this.font = font;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        // Setup VAO/VBO for a quad
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, 6 * 4 * Float.BYTES, GL_DYNAMIC_DRAW); // 6 vertices, 4 floats each
        glEnableVertexAttribArray(0); // aPos
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(1); // aTexCoord
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        // Load shaders
        int vertShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertShader, com.codebyriley.Core.Rendering.Shaders.ShaderLoader.readShaderFromResource("/shaders/TextVertexShader.vert.glsl"));
        glCompileShader(vertShader);
        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShader, com.codebyriley.Core.Rendering.Shaders.ShaderLoader.readShaderFromResource("/shaders/TextFragmentShader.frag.glsl"));
        glCompileShader(fragShader);
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertShader);
        glAttachShader(shaderProgram, fragShader);
        glLinkProgram(shaderProgram);
        glDeleteShader(vertShader);
        glDeleteShader(fragShader);
        uProjectionLoc = glGetUniformLocation(shaderProgram, "uProjection");
        uTextColorLoc = glGetUniformLocation(shaderProgram, "uTextColor");
        uFontAtlasLoc = glGetUniformLocation(shaderProgram, "uFontAtlas");
    }

    public void setWindowSize(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
        projectionDirty = true; // Mark projection matrix as needing update
    }
    
    private void updateProjectionMatrix() {
        if (!projectionDirty) return;
        
        projectionMatrix[0] = 2.0f/windowWidth;
        projectionMatrix[1] = 0;
        projectionMatrix[2] = 0;
        projectionMatrix[3] = 0;
        projectionMatrix[4] = 0;
        projectionMatrix[5] = -2.0f/windowHeight;
        projectionMatrix[6] = 0;
        projectionMatrix[7] = 0;
        projectionMatrix[8] = 0;
        projectionMatrix[9] = 0;
        projectionMatrix[10] = -1;
        projectionMatrix[11] = 0;
        projectionMatrix[12] = -1;
        projectionMatrix[13] = 1;
        projectionMatrix[14] = 0;
        projectionMatrix[15] = 1;
        
        projectionDirty = false;
    }

    // Draws text at (x, y) in window coordinates (top-left origin), with given scale and color (r,g,b)
    public void drawText(String text, float x, float y, float scale, float r, float g, float b) {
        glUseProgram(shaderProgram);
        
        // Cache projection matrix calculation
        if (projectionDirty) {
            updateProjectionMatrix();
            projectionDirty = false;
        }
        
        glUniformMatrix4fv(uProjectionLoc, false, projectionMatrix);
        glUniform3f(uTextColorLoc, r, g, b);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, font.getTextureId());
        glUniform1i(uFontAtlasLoc, 0);
        glBindVertexArray(vao);
        
        // Pre-allocate reusable arrays to avoid allocations in hot path
        float[] xpos = new float[1];
        float[] ypos = new float[1];
        xpos[0] = x / scale;
        ypos[0] = y / scale;
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            STBTTAlignedQuad quad = STBTTAlignedQuad.malloc(stack);
            
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c < 32 || c >= 128) continue;
                
                STBTruetype.stbtt_GetBakedQuad(
                    font.getCharData(),
                    font.bitmapWidth, font.bitmapHeight,
                    c - 32, xpos, ypos, quad, true
                );
                
                float x0 = quad.x0() * scale;
                float y0 = quad.y0() * scale;
                float x1 = quad.x1() * scale;
                float y1 = quad.y1() * scale;
                float s0 = quad.s0();
                float t0 = quad.t0();
                float s1 = quad.s1();
                float t1 = quad.t1();
                
                // Use vertexBuffer.put() instead of new float[]
                vertexBuffer.clear();
                vertexBuffer.put(x0).put(y0).put(s0).put(t0);
                vertexBuffer.put(x1).put(y0).put(s1).put(t0);
                vertexBuffer.put(x1).put(y1).put(s1).put(t1);
                vertexBuffer.put(x0).put(y0).put(s0).put(t0);
                vertexBuffer.put(x1).put(y1).put(s1).put(t1);
                vertexBuffer.put(x0).put(y1).put(s0).put(t1);
                vertexBuffer.flip();
                
                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer);
                glDrawArrays(GL_TRIANGLES, 0, 6);
            }
        }
        
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
        glUseProgram(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteProgram(shaderProgram);
    }
}
