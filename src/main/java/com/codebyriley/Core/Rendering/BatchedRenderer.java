package com.codebyriley.Core.Rendering;

import static org.lwjgl.opengl.GL33.*;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import com.codebyriley.Core.Rendering.Shaders.ShaderLoader;
import com.codebyriley.Util.Log;

public class BatchedRenderer {
    private static final int MAX_QUADS = 1000;
    private static final int VERTEX_SIZE = 8; // x, y, u, v, r, g, b, a (removed rotation and texIndex)
    private static final int QUAD_VERTICES = 4;
    private static final int QUAD_INDICES = 6;
    private static final int MAX_TEXTURES = 16;

    private int vao, vbo, ebo, shaderProgram;
    private FloatBuffer vertexBuffer;
    private int quadCount = 0;
    private int[] textureSlots = new int[MAX_TEXTURES];
    private int textureSlotIndex = 1; // 0 is reserved for white texture

    public BatchedRenderer() {
        vao = glGenVertexArrays();
        Log.checkGLErrorDetailed("BatchedRenderer.constructor", "glGenVertexArrays");
        vbo = glGenBuffers();
        Log.checkGLErrorDetailed("BatchedRenderer.constructor", "glGenBuffers VBO");
        ebo = glGenBuffers();
        Log.checkGLErrorDetailed("BatchedRenderer.constructor", "glGenBuffers EBO");
        
        // Initialize texture slots array
        for (int i = 0; i < MAX_TEXTURES; i++) {
            textureSlots[i] = 0;
        }
        
        glBindVertexArray(vao);
        Log.checkGLErrorDetailed("BatchedRenderer.constructor", "glBindVertexArray");
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        Log.checkGLErrorDetailed("BatchedRenderer.constructor", "glBindBuffer ARRAY_BUFFER");
        glBufferData(GL_ARRAY_BUFFER, MAX_QUADS * QUAD_VERTICES * VERTEX_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);
        Log.checkGLErrorDetailed("BatchedRenderer.constructor", "glBufferData ARRAY_BUFFER");
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        Log.checkGLErrorDetailed("BatchedRenderer.constructor", "glBindBuffer ELEMENT_ARRAY_BUFFER");
        int[] indices = new int[MAX_QUADS * QUAD_INDICES];
        int offset = 0;
        for (int i = 0; i < MAX_QUADS; i++) {
            indices[i * 6 + 0] = offset + 0;
            indices[i * 6 + 1] = offset + 1;
            indices[i * 6 + 2] = offset + 2;
            indices[i * 6 + 3] = offset + 2;
            indices[i * 6 + 4] = offset + 3;
            indices[i * 6 + 5] = offset + 0;
            offset += 4;
        }
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        Log.checkGLErrorDetailed("BatchedRenderer.constructor", "glBufferData ELEMENT_ARRAY_BUFFER");
        int stride = VERTEX_SIZE * Float.BYTES;
        // Position (location 0): x, y
        glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);
        // Color (location 1): r, g, b, a
        glVertexAttribPointer(1, 4, GL_FLOAT, false, stride, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);
        // TexCoord (location 2): u, v
        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);
        Log.checkGLErrorDetailed("BatchedRenderer.constructor", "vertex attribute setup");
        glBindVertexArray(0);
        vertexBuffer = BufferUtils.createFloatBuffer(MAX_QUADS * QUAD_VERTICES * VERTEX_SIZE);
        // Shader
        String vertSource = ShaderLoader.readShaderFromResource("/shaders/BatchVertexShader.vert.glsl");
        String fragSource = ShaderLoader.readShaderFromResource("/shaders/BatchFragmentShader.frag.glsl");
        
        if (vertSource == null || fragSource == null) {
            Log.error("BatchedRenderer.constructor: Failed to load shader sources");
            return;
        }
        
        int vertShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertShader, vertSource);
        glCompileShader(vertShader);
        
        // Check vertex shader compilation
        int[] vertCompiled = new int[1];
        glGetShaderiv(vertShader, GL_COMPILE_STATUS, vertCompiled);
        if (vertCompiled[0] == 0) {
            String error = glGetShaderInfoLog(vertShader);
            Log.error("BatchedRenderer.constructor: Vertex shader compilation failed: " + error);
            glDeleteShader(vertShader);
            return;
        }
        Log.checkGLErrorDetailed("BatchedRenderer.constructor", "vertex shader compilation");
        
        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShader, fragSource);
        glCompileShader(fragShader);
        
        // Check fragment shader compilation
        int[] fragCompiled = new int[1];
        glGetShaderiv(fragShader, GL_COMPILE_STATUS, fragCompiled);
        if (fragCompiled[0] == 0) {
            String error = glGetShaderInfoLog(fragShader);
            Log.error("BatchedRenderer.constructor: Fragment shader compilation failed: " + error);
            glDeleteShader(vertShader);
            glDeleteShader(fragShader);
            return;
        }
        Log.checkGLErrorDetailed("BatchedRenderer.constructor", "fragment shader compilation");
        
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertShader);
        glAttachShader(shaderProgram, fragShader);
        glLinkProgram(shaderProgram);
        
        // Check program linking
        int[] linked = new int[1];
        glGetProgramiv(shaderProgram, GL_LINK_STATUS, linked);
        if (linked[0] == 0) {
            String error = glGetProgramInfoLog(shaderProgram);
            Log.error("BatchedRenderer.constructor: Shader program linking failed: " + error);
            glDeleteShader(vertShader);
            glDeleteShader(fragShader);
            glDeleteProgram(shaderProgram);
            shaderProgram = 0;
            return;
        }
        Log.checkGLErrorDetailed("BatchedRenderer.constructor", "shader program linking");
        
        glDeleteShader(vertShader);
        glDeleteShader(fragShader);
    }

    /**
     * Call at the start of each frame.
     */
    public void begin() {
        quadCount = 0;
        textureSlotIndex = 1;
        vertexBuffer.clear();
    }

    /**
     * Add a quad to the batch.
     * @param x, y: position (center)
     * @param w, h: size
     * @param u1, v1, u2, v2: texture UVs
     * @param r, g, b, a: color
     * @param textureId: OpenGL texture id
     */
    public void addQuad(float x, float y, float w, float h, float u1, float v1, float u2, float v2, float r, float g, float b, float a, int textureId) {
        if (quadCount >= MAX_QUADS) flush();
        int texIndex = getTextureSlot(textureId);
        float hw = w * 0.5f, hh = h * 0.5f;
        
        // Calculate the four corners of the quad
        float[][] corners = {
            {x - hw, y - hh, u1, v1}, // bottom-left
            {x + hw, y - hh, u2, v1}, // bottom-right
            {x + hw, y + hh, u2, v2}, // top-right
            {x - hw, y + hh, u1, v2}  // top-left
        };
        
        for (float[] c : corners) {
            vertexBuffer.put(c[0]).put(c[1]).put(r).put(g).put(b).put(a).put(c[2]).put(c[3]);
        }
        quadCount++;
    }

    private int getTextureSlot(int textureId) {
        if (textureId == 0) return 0;
        for (int i = 1; i < textureSlotIndex; i++) if (textureSlots[i] == textureId) return i;
        if (textureSlotIndex >= textureSlots.length) flush();
        textureSlots[textureSlotIndex] = textureId;
        return textureSlotIndex++;
    }

    /**
     * Call at the end of each frame.
     */
    public void end() {
        flush();
    }

    /**
     * Flush the batch (draw all quads).
     */
    public void flush() {
        if (quadCount == 0) return;
        vertexBuffer.flip();
        
        // Check if shader program is valid
        if (shaderProgram == 0) {
            Log.error("BatchedRenderer.flush: Shader program is not valid");
            return;
        }
        
        glUseProgram(shaderProgram);
        Log.checkGLErrorDetailed("BatchedRenderer.flush", "glUseProgram");
        
        // Set window size uniform
        int windowSizeLocation = glGetUniformLocation(shaderProgram, "uWindowSize");
        if (windowSizeLocation != -1) {
            glUniform2f(windowSizeLocation, WindowBase.windowWidth, WindowBase.windowHeight);
            Log.checkGLErrorDetailed("BatchedRenderer.flush", "set window size uniform");
        } else {
            Log.warn("BatchedRenderer.flush: Could not find uWindowSize uniform location");
        }
        
        // Set texture usage uniform
        int useTextureLocation = glGetUniformLocation(shaderProgram, "uUseTexture");
        if (useTextureLocation != -1) {
            glUniform1i(useTextureLocation, textureSlotIndex > 1 ? 1 : 0);
            Log.checkGLErrorDetailed("BatchedRenderer.flush", "set use texture uniform");
        }
        
        glBindVertexArray(vao);
        Log.checkGLErrorDetailed("BatchedRenderer.flush", "glBindVertexArray");
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        Log.checkGLErrorDetailed("BatchedRenderer.flush", "glBindBuffer");
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer);
        Log.checkGLErrorDetailed("BatchedRenderer.flush", "glBufferSubData");
        
        // Bind textures
        // Always bind texture 0 first (for white texture)
        glActiveTexture(GL_TEXTURE0);
        if (textureSlots[0] != 0) {
            glBindTexture(GL_TEXTURE_2D, textureSlots[0]);
        } else {
            // Create a simple white texture if none exists
            int whiteTexture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, whiteTexture);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            
            // Create a 1x1 white pixel
            int[] whitePixel = {0xFFFFFFFF};
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, java.nio.ByteBuffer.allocateDirect(4).putInt(whitePixel[0]).flip());
            textureSlots[0] = whiteTexture;
        }
        
        // Bind other textures
        for (int i = 1; i < textureSlotIndex; i++) {
            if (textureSlots[i] != 0) {
                glActiveTexture(GL_TEXTURE0 + i);
                glBindTexture(GL_TEXTURE_2D, textureSlots[i]);
            }
        }
        Log.checkGLErrorDetailed("BatchedRenderer.flush", "texture binding");
        
        glDrawElements(GL_TRIANGLES, quadCount * QUAD_INDICES, GL_UNSIGNED_INT, 0);
        Log.checkGLErrorDetailed("BatchedRenderer.flush", "glDrawElements");
        glBindVertexArray(0);
        vertexBuffer.clear();
        quadCount = 0;
        textureSlotIndex = 1;
    }

    public void destroy() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        if (shaderProgram != 0) glDeleteProgram(shaderProgram);
    }
} 