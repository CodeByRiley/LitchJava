package com.codebyriley.Core.Rendering.Textures;

import static org.lwjgl.opengl.GL33.*;
import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;

/**
 * Utility class for creating common textures used in rendering.
 */
public class TextureUtils {
    
    /**
     * Create a 1x1 white texture for solid color rendering.
     * This is commonly used for primitive rendering when no texture is needed.
     */
    public static int createWhiteTexture() {
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        
        // Create a 1x1 white pixel
        ByteBuffer whitePixel = BufferUtils.createByteBuffer(4);
        whitePixel.put((byte) 255).put((byte) 255).put((byte) 255).put((byte) 255);
        whitePixel.flip();
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, whitePixel);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        glBindTexture(GL_TEXTURE_2D, 0);
        return textureId;
    }
    
    /**
     * Create a 1x1 colored texture for solid color rendering.
     */
    public static int createColoredTexture(int r, int g, int b, int a) {
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        
        // Create a 1x1 colored pixel
        ByteBuffer coloredPixel = BufferUtils.createByteBuffer(4);
        coloredPixel.put((byte) r).put((byte) g).put((byte) b).put((byte) a);
        coloredPixel.flip();
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, coloredPixel);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        glBindTexture(GL_TEXTURE_2D, 0);
        return textureId;
    }
    
    /**
     * Create a checkerboard pattern texture for debugging.
     */
    public static int createCheckerboardTexture(int size, int tileSize) {
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        
        // Create checkerboard pattern
        ByteBuffer pixels = BufferUtils.createByteBuffer(size * size * 4);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                boolean isWhite = ((x / tileSize) + (y / tileSize)) % 2 == 0;
                byte color = isWhite ? (byte) 255 : (byte) 128;
                pixels.put(color);     // R
                pixels.put(color); // G
                pixels.put(color); // B
                pixels.put((byte) 255); // A
            }
        }
        pixels.flip();
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, size, size, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        
        glBindTexture(GL_TEXTURE_2D, 0);
        return textureId;
    }
    
    /**
     * Create a gradient texture.
     */
    public static int createGradientTexture(int width, int height, int r1, int g1, int b1, int r2, int g2, int b2, boolean horizontal) {
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        
        ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float t;
                if (horizontal) {
                    t = (float) x / (width - 1);
                } else {
                    t = (float) y / (height - 1);
                }
                
                int r = (int) (r1 + (r2 - r1) * t);
                int g = (int) (g1 + (g2 - g1) * t);
                int b = (int) (b1 + (b2 - b1) * t);
                
                pixels.put((byte) r);     // R
                pixels.put((byte) g); // G
                pixels.put((byte) b); // B
                pixels.put((byte) 255); // A
            }
        }
        pixels.flip();
        
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        
        glBindTexture(GL_TEXTURE_2D, 0);
        return textureId;
    }
    
    /**
     * Delete a texture and free OpenGL resources.
     */
    public static void deleteTexture(int textureId) {
        if (textureId != 0) {
            glDeleteTextures(textureId);
        }
    }
} 