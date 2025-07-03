package com.codebyriley.Core.Rendering.Primatives;

import static org.lwjgl.opengl.GL33.*;

import com.codebyriley.Core.Rendering.WindowBase;
import com.codebyriley.Core.Rendering.Shaders.ShaderLoader;

public class Renderer {
    private int vao, vbo, shaderProgram;
    private int framebuffer = 0;
    private int framebufferTexture = 0;
    private int framebufferRBO = 0;
    private int fbWidth = 0, fbHeight = 0;

    private int texturedShaderProgram = 0;
    private int batchShaderProgram = 0;
    
    // Batching system
    private QuadBatch quadBatch;

    private static final float[] SQUARE_VERTICES = {
        // x, y (origin at top-left)
        0.0f, 0.0f,
        1.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 1.0f
    };
    private static final int[] INDICES = { 0, 1, 2, 2, 3, 0 };

    public Renderer() {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        int ebo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, SQUARE_VERTICES, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, INDICES, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glBindVertexArray(0);

        // Base shader
        int vertShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertShader, ShaderLoader.readShaderFromResource("/shaders/BaseVertexShader.vert.glsl"));
        glCompileShader(vertShader);
        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShader, ShaderLoader.readShaderFromResource("/shaders/BaseFragmentShader.frag.glsl"));
        glCompileShader(fragShader);
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertShader);
        glAttachShader(shaderProgram, fragShader);
        glLinkProgram(shaderProgram);
        glDeleteShader(vertShader);
        glDeleteShader(fragShader);

        // Textured shader
        int texturedVertShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(texturedVertShader, ShaderLoader.readShaderFromResource("/shaders/TexturedVertexShader.vert.glsl"));
        glCompileShader(texturedVertShader);
        int texturedFragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(texturedFragShader, ShaderLoader.readShaderFromResource("/shaders/TexturedFragmentShader.frag.glsl"));
        glCompileShader(texturedFragShader);
        texturedShaderProgram = glCreateProgram();
        glAttachShader(texturedShaderProgram, texturedVertShader);
        glAttachShader(texturedShaderProgram, texturedFragShader);
        glLinkProgram(texturedShaderProgram);
        glDeleteShader(texturedVertShader);
        glDeleteShader(texturedFragShader);

        // Batch shader
        int batchVertShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(batchVertShader, ShaderLoader.readShaderFromResource("/shaders/BatchVertexShader.vert.glsl"));
        glCompileShader(batchVertShader);
        int batchFragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(batchFragShader, ShaderLoader.readShaderFromResource("/shaders/BatchFragmentShader.frag.glsl"));
        glCompileShader(batchFragShader);
        batchShaderProgram = glCreateProgram();
        glAttachShader(batchShaderProgram, batchVertShader);
        glAttachShader(batchShaderProgram, batchFragShader);
        glLinkProgram(batchShaderProgram);
        glDeleteShader(batchVertShader);
        glDeleteShader(batchFragShader);

        // Initialize batching system (1000 quads per batch)
        quadBatch = new QuadBatch(1000);
        quadBatch.setShaderProgram(batchShaderProgram);
    }

    // Framebuffer setup
    public void createFramebuffer(int width, int height) {
        if (framebuffer != 0) {
            glDeleteFramebuffers(framebuffer);
            glDeleteTextures(framebufferTexture);
            glDeleteRenderbuffers(framebufferRBO);
        }
        fbWidth = width;
        fbHeight = height;
        framebuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

        framebufferTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, framebufferTexture);
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, framebufferTexture, 0);

        framebufferRBO = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, framebufferRBO);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, framebufferRBO);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer is not complete!");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bindFramebuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        glViewport(0, 0, fbWidth, fbHeight);
    }

    public void unbindFramebuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, WindowBase.windowWidth, WindowBase.windowHeight);
    }

    public int getFramebufferTexture() {
        return framebufferTexture;
    }

    // x, y: top-left in window pixels; width, height: in pixels; r,g,b: color (0-1)
    public void DrawSquare(float x, float y, float width, float height, float r, float g, float b) {
        glUseProgram(shaderProgram);
        int uPosLoc = glGetUniformLocation(shaderProgram, "uPos");
        int uSizeLoc = glGetUniformLocation(shaderProgram, "uSize");
        int uColorLoc = glGetUniformLocation(shaderProgram, "uColor");
        int uWindowSizeLoc = glGetUniformLocation(shaderProgram, "uWindowSize");
        int uAlphaLoc = glGetUniformLocation(shaderProgram, "uAlpha");
        glUniform2f(uPosLoc, x, y);
        glUniform2f(uSizeLoc, width, height);
        glUniform3f(uColorLoc, r, g, b);
        glUniform2f(uWindowSizeLoc, WindowBase.windowWidth, WindowBase.windowHeight);
        glUniform1f(uAlphaLoc, 1.0f); // Opaque for normal squares
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, INDICES.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    // Draws a fullscreen quad with the given color and alpha
    public void DrawFullscreenQuad(float r, float g, float b, float alpha) {
        glEnable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glUseProgram(shaderProgram);
        int uPosLoc = glGetUniformLocation(shaderProgram, "uPos");
        int uSizeLoc = glGetUniformLocation(shaderProgram, "uSize");
        int uColorLoc = glGetUniformLocation(shaderProgram, "uColor");
        int uWindowSizeLoc = glGetUniformLocation(shaderProgram, "uWindowSize");
        int uAlphaLoc = glGetUniformLocation(shaderProgram, "uAlpha");
        glUniform2f(uPosLoc, 0.0f, 0.0f);
        glUniform2f(uSizeLoc, WindowBase.windowWidth, WindowBase.windowHeight);
        glUniform3f(uColorLoc, r, g, b);
        glUniform2f(uWindowSizeLoc, WindowBase.windowWidth, WindowBase.windowHeight);
        glUniform1f(uAlphaLoc, alpha);
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, INDICES.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    // Draw the framebuffer texture to the screen as a fullscreen quad
    public void DrawFramebufferToScreen() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, WindowBase.windowWidth, WindowBase.windowHeight);
        glDisable(GL_DEPTH_TEST);
    
        glUseProgram(texturedShaderProgram);
    
        int uPosLoc = glGetUniformLocation(texturedShaderProgram, "uPos");
        int uSizeLoc = glGetUniformLocation(texturedShaderProgram, "uSize");
        int uWindowSizeLoc = glGetUniformLocation(texturedShaderProgram, "uWindowSize");
        int uTextureLoc = glGetUniformLocation(texturedShaderProgram, "uTexture");
    
        glUniform2f(uPosLoc, 0.0f, 0.0f);
        glUniform2f(uSizeLoc, WindowBase.windowWidth, WindowBase.windowHeight);
        glUniform2f(uWindowSizeLoc, WindowBase.windowWidth, WindowBase.windowHeight);
    
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, framebufferTexture);
        glUniform1i(uTextureLoc, 0);
    
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, INDICES.length, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    
        glBindTexture(GL_TEXTURE_2D, 0);
        glEnable(GL_DEPTH_TEST);
    }

    // ===== BATCHED RENDERING METHODS =====
    
    public void beginBatch() {
        glUseProgram(batchShaderProgram);
        int uWindowSizeLoc = glGetUniformLocation(batchShaderProgram, "uWindowSize");
        glUniform2f(uWindowSizeLoc, WindowBase.windowWidth, WindowBase.windowHeight);
        quadBatch.begin();
    }
    
    public void endBatch() {
        quadBatch.end();
    }
    
    // Draw colored quad using batching
    public void drawColoredQuadBatch(float x, float y, float width, float height, float r, float g, float b, float a) {
        quadBatch.drawColoredQuad(x, y, width, height, r, g, b, a);
    }
    
    // Draw colored quad with rotation using batching
    public void drawColoredQuadBatch(float x, float y, float width, float height, float r, float g, float b, float a, float rotation) {
        quadBatch.drawColoredQuad(x, y, width, height, r, g, b, a, rotation);
    }
    
    // Draw textured quad using batching
    public void drawTexturedQuadBatch(float x, float y, float width, float height, int textureId, float r, float g, float b, float a) {
        quadBatch.drawTexturedQuad(x, y, width, height, textureId, r, g, b, a);
    }
    
    // Draw textured quad with rotation using batching
    public void drawTexturedQuadBatch(float x, float y, float width, float height, int textureId, float r, float g, float b, float a, float rotation) {
        quadBatch.drawTexturedQuad(x, y, width, height, textureId, r, g, b, a, rotation);
    }
    
    // Draw textured quad with custom UV coordinates using batching
    public void drawTexturedQuadBatch(float x, float y, float width, float height, int textureId, 
                                    float u1, float v1, float u2, float v2, float r, float g, float b, float a) {
        quadBatch.drawTexturedQuad(x, y, width, height, textureId, u1, v1, u2, v2, r, g, b, a);
    }
    
    // Draw textured quad with custom UV coordinates and rotation using batching
    public void drawTexturedQuadBatch(float x, float y, float width, float height, int textureId, 
                                    float u1, float v1, float u2, float v2, float r, float g, float b, float a, float rotation) {
        quadBatch.drawTexturedQuad(x, y, width, height, textureId, u1, v1, u2, v2, r, g, b, a, rotation);
    }

    public void Shutdown() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        if (shaderProgram != 0) {
            glDeleteProgram(shaderProgram);
        }
        if (texturedShaderProgram != 0) {
            glDeleteProgram(texturedShaderProgram);
        }
        if (batchShaderProgram != 0) {
            glDeleteProgram(batchShaderProgram);
        }
        if (framebuffer != 0) {
            glDeleteFramebuffers(framebuffer);
            glDeleteTextures(framebufferTexture);
            glDeleteRenderbuffers(framebufferRBO);
        }
        if (quadBatch != null) {
            quadBatch.destroy();
        }
    }
}
