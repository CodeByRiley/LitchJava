package com.codebyriley.Core.Rendering.Primatives;

import static org.lwjgl.opengl.GL33.*;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class QuadBatch {
    // Vertex structure: x, y, r, g, b, a, u, v, centerX, centerY, cosA, sinA
    private static final int VERTEX_SIZE = 12;
    private static final int QUAD_VERTICES = 4;
    private static final int QUAD_INDICES = 6;
    
    private final int maxQuads;
    private final int vao, vbo, ebo;
    private final FloatBuffer vertexBuffer;
    private final IntBuffer indexBuffer;
    
    private int quadCount = 0;
    private int currentTexture = -1;
    private boolean isTextured = false;
    
    // Pre-calculated indices for quads
    private static final int[] QUAD_INDICES_DATA = {
        0, 1, 2, 2, 3, 0
    };
    
    private int uUseTextureLoc = -1, uTextureLoc = -1;
    private int batchShaderProgram = -1;
    
    public QuadBatch(int maxQuads) {
        this.maxQuads = maxQuads;
        
        // Create buffers
        vertexBuffer = BufferUtils.createFloatBuffer(maxQuads * QUAD_VERTICES * VERTEX_SIZE);
        indexBuffer = BufferUtils.createIntBuffer(maxQuads * QUAD_INDICES);
        
        // Pre-fill index buffer (indices don't change)
        for (int i = 0; i < maxQuads; i++) {
            int baseIndex = i * QUAD_VERTICES;
            for (int j = 0; j < QUAD_INDICES; j++) {
                indexBuffer.put(QUAD_INDICES_DATA[j] + baseIndex);
            }
        }
        indexBuffer.flip();
        
        // Create VAO and VBO
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();
        
        glBindVertexArray(vao);
        
        // Vertex Buffer
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, maxQuads * QUAD_VERTICES * VERTEX_SIZE * Float.BYTES, GL_DYNAMIC_DRAW);
        
        // Position (x, y)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        
        // Color (r, g, b, a)
        glVertexAttribPointer(1, 4, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);
        
        // Texture coordinates (u, v)
        glVertexAttribPointer(2, 2, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);
        
        // Rotation data (centerX, centerY, cosA, sinA)
        glVertexAttribPointer(3, 4, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 8 * Float.BYTES);
        glEnableVertexAttribArray(3);
        
        // Index Buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
        
        glBindVertexArray(0);
    }
    
    // Cache uniform locations when shader program is set
    public void setShaderProgram(int shaderProgram) {
        if (this.batchShaderProgram != shaderProgram) {
            this.batchShaderProgram = shaderProgram;
            uUseTextureLoc = glGetUniformLocation(shaderProgram, "uUseTexture");
            uTextureLoc = glGetUniformLocation(shaderProgram, "uTexture");
        }
    }
    
    public void begin() {
        quadCount = 0;
        vertexBuffer.clear();
        currentTexture = -1;
        isTextured = false;
    }
    
    public void drawColoredQuad(float x, float y, float width, float height, float r, float g, float b, float a) {
        drawColoredQuad(x, y, width, height, r, g, b, a, 0.0f);
    }
    
    public void drawColoredQuad(float x, float y, float width, float height, float r, float g, float b, float a, float rotation) {
        // Check if we need to flush (different texture or batch full)
        if (isTextured || quadCount >= maxQuads) {
            flush();
        }
        
        // Set textured flag
        isTextured = false;
        
        // Add quad vertices with rotation
        addQuadVertices(x, y, width, height, r, g, b, a, 0.0f, 0.0f, 1.0f, 1.0f, rotation);
        quadCount++;
    }
    
    public void drawTexturedQuad(float x, float y, float width, float height, int textureId, float r, float g, float b, float a) {
        drawTexturedQuad(x, y, width, height, textureId, r, g, b, a, 0.0f);
    }
    
    public void drawTexturedQuad(float x, float y, float width, float height, int textureId, float r, float g, float b, float a, float rotation) {
        // Check if we need to flush (different texture or batch full)
        if (textureId != currentTexture || (!isTextured && quadCount > 0) || quadCount >= maxQuads) {
            flush();
        }
        
        // Set textured flag and current texture
        isTextured = true;
        currentTexture = textureId;
        
        // Add quad vertices with texture coordinates and rotation
        addQuadVertices(x, y, width, height, r, g, b, a, 0.0f, 0.0f, 1.0f, 1.0f, rotation);
        quadCount++;
    }
    
    public void drawTexturedQuad(float x, float y, float width, float height, int textureId, 
                                float u1, float v1, float u2, float v2, float r, float g, float b, float a) {
        drawTexturedQuad(x, y, width, height, textureId, u1, v1, u2, v2, r, g, b, a, 0.0f);
    }
    
    public void drawTexturedQuad(float x, float y, float width, float height, int textureId, 
                                float u1, float v1, float u2, float v2, float r, float g, float b, float a, float rotation) {
        // Check if we need to flush (different texture or batch full)
        if (textureId != currentTexture || (!isTextured && quadCount > 0) || quadCount >= maxQuads) {
            flush();
        }
        
        // Set textured flag and current texture
        isTextured = true;
        currentTexture = textureId;
        
        // Add quad vertices with custom texture coordinates and rotation
        addQuadVertices(x, y, width, height, r, g, b, a, u1, v1, u2, v2, rotation);
        quadCount++;
    }
    
    private void addQuadVertices(float x, float y, float width, float height, 
                                float r, float g, float b, float a,
                                float u1, float v1, float u2, float v2) {
        addQuadVertices(x, y, width, height, r, g, b, a, u1, v1, u2, v2, 0.0f);
    }
    
    private void addQuadVertices(float x, float y, float width, float height, 
                                float r, float g, float b, float a,
                                float u1, float v1, float u2, float v2, float rotation) {
        float centerX = x + width * 0.5f;
        float centerY = y + height * 0.5f;
        float cosA = (float)Math.cos(rotation);
        float sinA = (float)Math.sin(rotation);
        
        // Calculate rotated corners
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;
        
        // Top-left
        float tlX = centerX + (-halfWidth * cosA - (-halfHeight) * sinA);
        float tlY = centerY + (-halfWidth * sinA + (-halfHeight) * cosA);
        vertexBuffer.put(tlX).put(tlY).put(r).put(g).put(b).put(a).put(u1).put(v1).put(centerX).put(centerY).put(cosA).put(sinA);
        
        // Top-right
        float trX = centerX + (halfWidth * cosA - (-halfHeight) * sinA);
        float trY = centerY + (halfWidth * sinA + (-halfHeight) * cosA);
        vertexBuffer.put(trX).put(trY).put(r).put(g).put(b).put(a).put(u2).put(v1).put(centerX).put(centerY).put(cosA).put(sinA);
        
        // Bottom-right
        float brX = centerX + (halfWidth * cosA - halfHeight * sinA);
        float brY = centerY + (halfWidth * sinA + halfHeight * cosA);
        vertexBuffer.put(brX).put(brY).put(r).put(g).put(b).put(a).put(u2).put(v2).put(centerX).put(centerY).put(cosA).put(sinA);
        
        // Bottom-left
        float blX = centerX + (-halfWidth * cosA - halfHeight * sinA);
        float blY = centerY + (-halfWidth * sinA + halfHeight * cosA);
        vertexBuffer.put(blX).put(blY).put(r).put(g).put(b).put(a).put(u1).put(v2).put(centerX).put(centerY).put(cosA).put(sinA);
    }
    
    public void flush() {
        if (quadCount == 0) return;
        
        // Upload vertex data
        vertexBuffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer);
        
        // Set shader uniforms using cached locations
        if (isTextured && currentTexture != -1) {
            glUniform1i(uUseTextureLoc, 1);
            glUniform1i(uTextureLoc, 0);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, currentTexture);
        } else {
            glUniform1i(uUseTextureLoc, 0);
        }
        
        // Draw
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, quadCount * QUAD_INDICES, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
        
        // Reset for next batch
        quadCount = 0;
        vertexBuffer.clear();
    }
    
    public void end() {
        flush();
    }
    
    public void destroy() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
    }
} 