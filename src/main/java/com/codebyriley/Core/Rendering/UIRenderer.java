package com.codebyriley.Core.Rendering;

import static org.lwjgl.opengl.GL33.*;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import com.codebyriley.Core.Rendering.Shaders.ShaderLoader;
import com.codebyriley.Util.Log;

public class UIRenderer {
    private static final int MAX_QUADS = 1000;
    private static final int VERTEX_SIZE = 8; // x, y, r, g, b, a, u, v
    private static final int QUAD_VERTICES = 4;
    private static final int QUAD_INDICES = 6;

    private int vao, vbo, ebo, shaderProgram;
    private FloatBuffer vertexBuffer;
    private int quadCount = 0;
    private int currentTexture = 0;
    private int whiteTexture = 0;

    public UIRenderer() {
        // Initialize OpenGL objects
        vao = glGenVertexArrays();
        Log.checkGLErrorDetailed("UIRenderer.constructor", "glGenVertexArrays");
        vbo = glGenBuffers();
        Log.checkGLErrorDetailed("UIRenderer.constructor", "glGenBuffers VBO");
        ebo = glGenBuffers();
        Log.checkGLErrorDetailed("UIRenderer.constructor", "glGenBuffers EBO");
        
        // Setup VAO and VBO
        glBindVertexArray(vao);
        Log.checkGLErrorDetailed("UIRenderer.constructor", "glBindVertexArray");
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        Log.checkGLErrorDetailed("UIRenderer.constructor", "glBindBuffer ARRAY_BUFFER");
        glBufferData(GL_ARRAY_BUFFER, MAX_QUADS * QUAD_VERTICES * VERTEX_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);
        Log.checkGLErrorDetailed("UIRenderer.constructor", "glBufferData ARRAY_BUFFER");
        
        // Setup EBO
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        Log.checkGLErrorDetailed("UIRenderer.constructor", "glBindBuffer ELEMENT_ARRAY_BUFFER");
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
        Log.checkGLErrorDetailed("UIRenderer.constructor", "glBufferData ELEMENT_ARRAY_BUFFER");
        
        // Setup vertex attributes
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
        Log.checkGLErrorDetailed("UIRenderer.constructor", "vertex attribute setup");
        
        glBindVertexArray(0);
        vertexBuffer = BufferUtils.createFloatBuffer(MAX_QUADS * QUAD_VERTICES * VERTEX_SIZE);
        
        // Load shaders
        String vertSource = ShaderLoader.readShaderFromResource("/shaders/BatchVertexShader.vert.glsl");
        String fragSource = ShaderLoader.readShaderFromResource("/shaders/BatchFragmentShader.frag.glsl");
        
        if (vertSource == null || fragSource == null) {
            Log.error("UIRenderer.constructor: Failed to load shader sources");
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
            Log.error("UIRenderer.constructor: Vertex shader compilation failed: " + error);
            glDeleteShader(vertShader);
            return;
        }
        Log.checkGLErrorDetailed("UIRenderer.constructor", "vertex shader compilation");
        
        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShader, fragSource);
        glCompileShader(fragShader);
        
        // Check fragment shader compilation
        int[] fragCompiled = new int[1];
        glGetShaderiv(fragShader, GL_COMPILE_STATUS, fragCompiled);
        if (fragCompiled[0] == 0) {
            String error = glGetShaderInfoLog(fragShader);
            Log.error("UIRenderer.constructor: Fragment shader compilation failed: " + error);
            glDeleteShader(vertShader);
            glDeleteShader(fragShader);
            return;
        }
        Log.checkGLErrorDetailed("UIRenderer.constructor", "fragment shader compilation");
        
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertShader);
        glAttachShader(shaderProgram, fragShader);
        glLinkProgram(shaderProgram);
        
        // Check program linking
        int[] linked = new int[1];
        glGetProgramiv(shaderProgram, GL_LINK_STATUS, linked);
        if (linked[0] == 0) {
            String error = glGetProgramInfoLog(shaderProgram);
            Log.error("UIRenderer.constructor: Shader program linking failed: " + error);
            glDeleteShader(vertShader);
            glDeleteShader(fragShader);
            glDeleteProgram(shaderProgram);
            shaderProgram = 0;
            return;
        }
        Log.checkGLErrorDetailed("UIRenderer.constructor", "shader program linking");
        
        glDeleteShader(vertShader);
        glDeleteShader(fragShader);
        
        whiteTexture = createWhiteTexture();
    }

    private int createWhiteTexture() {
        int tex = glGenTextures();
        Log.checkGLErrorDetailed("UIRenderer.createWhiteTexture", "glGenTextures");
        glBindTexture(GL_TEXTURE_2D, tex);
        Log.checkGLErrorDetailed("UIRenderer.createWhiteTexture", "glBindTexture");
        
        byte[] white = {(byte)255, (byte)255, (byte)255, (byte)255};
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createByteBuffer(4).put(white).flip());
        Log.checkGLErrorDetailed("UIRenderer.createWhiteTexture", "glTexImage2D");
        
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        Log.checkGLErrorDetailed("UIRenderer.createWhiteTexture", "glTexParameteri");
        
        glBindTexture(GL_TEXTURE_2D, 0);
        return tex;
    }

    public void begin() {
        quadCount = 0;
        currentTexture = 0;
        vertexBuffer.clear();
    }

    public void addQuad(float x, float y, float w, float h, float u1, float v1, float u2, float v2, float r, float g, float b, float a, int textureId) {
        if (quadCount >= MAX_QUADS) flush();
        if (quadCount > 0 && textureId != currentTexture) {
            flush();
        }
        if (textureId == 0) textureId = whiteTexture;
        currentTexture = textureId;

        float hw = w * 0.5f, hh = h * 0.5f;
        float[][] corners = {
            {x - hw, y - hh, u1, v1},
            {x + hw, y - hh, u2, v1},
            {x + hw, y + hh, u2, v2},
            {x - hw, y + hh, u1, v2}
        };
        for (float[] c : corners) {
            vertexBuffer.put(c[0]).put(c[1]).put(r).put(g).put(b).put(a).put(c[2]).put(c[3]);
        }
        quadCount++;
    }

    public void end() {
        flush();
    }

    public void flush() {
        if (quadCount == 0) return;
        
        // Check if shader program is valid
        if (shaderProgram == 0) {
            Log.error("UIRenderer.flush: Shader program is not valid");
            return;
        }
        
        vertexBuffer.flip();
        glUseProgram(shaderProgram);
        Log.checkGLErrorDetailed("UIRenderer.flush", "glUseProgram");

        int windowSizeLocation = glGetUniformLocation(shaderProgram, "uWindowSize");
        if (windowSizeLocation != -1) {
            glUniform2f(windowSizeLocation, com.codebyriley.Core.Rendering.WindowBase.windowWidth, com.codebyriley.Core.Rendering.WindowBase.windowHeight);
            Log.checkGLErrorDetailed("UIRenderer.flush", "set window size uniform");
        } else {
            Log.warn("UIRenderer.flush: Could not find uWindowSize uniform location");
        }
        
        int useTextureLocation = glGetUniformLocation(shaderProgram, "uUseTexture");
        if (useTextureLocation != -1) {
            glUniform1i(useTextureLocation, currentTexture != whiteTexture ? 1 : 0);
            Log.checkGLErrorDetailed("UIRenderer.flush", "set use texture uniform");
        } else {
            Log.warn("UIRenderer.flush: Could not find uUseTexture uniform location");
        }

        glBindVertexArray(vao);
        Log.checkGLErrorDetailed("UIRenderer.flush", "glBindVertexArray");
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        Log.checkGLErrorDetailed("UIRenderer.flush", "glBindBuffer");
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer);
        Log.checkGLErrorDetailed("UIRenderer.flush", "glBufferSubData");

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, currentTexture);
        Log.checkGLErrorDetailed("UIRenderer.flush", "texture binding");

        glDrawElements(GL_TRIANGLES, quadCount * QUAD_INDICES, GL_UNSIGNED_INT, 0);
        Log.checkGLErrorDetailed("UIRenderer.flush", "glDrawElements");
        glBindVertexArray(0);

        vertexBuffer.clear();
        quadCount = 0;
    }

    public int getWhiteTexture() {
        return whiteTexture;
    }

    public void destroy() {
        if (vao != 0) glDeleteVertexArrays(vao);
        if (vbo != 0) glDeleteBuffers(vbo);
        if (ebo != 0) glDeleteBuffers(ebo);
        if (shaderProgram != 0) glDeleteProgram(shaderProgram);
        if (whiteTexture != 0) glDeleteTextures(whiteTexture);
        
        vao = vbo = ebo = shaderProgram = whiteTexture = 0;
    }
}