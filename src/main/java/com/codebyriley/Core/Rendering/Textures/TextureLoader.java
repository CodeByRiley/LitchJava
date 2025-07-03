package com.codebyriley.Core.Rendering.Textures;

import static org.lwjgl.opengl.GL33.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureLoader {

    public static Texture LoadTexture(String path) {
        Texture texture = new Texture();
        
        // Load image data using STB
        ByteBuffer imageBuffer = loadImageResource(path);
        if (imageBuffer == null) {
            throw new RuntimeException("Failed to load texture: " + path);
        }
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            
            // Decode the image
            ByteBuffer imageData = STBImage.stbi_load_from_memory(
                imageBuffer, 
                width, 
                height, 
                channels, 
                4 // Force RGBA
            );
            
            if (imageData == null) {
                throw new RuntimeException("Failed to decode texture: " + path + " - " + STBImage.stbi_failure_reason());
            }
            
            // Store dimensions
            texture.mWidth = width.get(0);
            texture.mHeight = height.get(0);
            
            // Generate OpenGL texture
            texture.mId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texture.mId);
            
            // Set texture parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            
            // Upload texture data
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.mWidth, texture.mHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);
            
            // Generate mipmaps
            glGenerateMipmap(GL_TEXTURE_2D);
            
            // Unbind texture
            glBindTexture(GL_TEXTURE_2D, 0);
            
            // Free STB image data
            STBImage.stbi_image_free(imageData);
            
            System.out.println("Loaded texture: " + path + " (" + texture.mWidth + "x" + texture.mHeight + ")");
        }
        
        return texture;
    }
    
    // Overloaded method for loading with custom filtering
    public static Texture LoadTexture(String path, int minFilter, int magFilter) {
        Texture texture = new Texture();
        
        // Load image data using STB
        ByteBuffer imageBuffer = loadImageResource(path);
        if (imageBuffer == null) {
            throw new RuntimeException("Failed to load texture: " + path);
        }
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            
            // Decode the image
            ByteBuffer imageData = STBImage.stbi_load_from_memory(
                imageBuffer, 
                width, 
                height, 
                channels, 
                4 // Force RGBA
            );
            
            if (imageData == null) {
                throw new RuntimeException("Failed to decode texture: " + path + " - " + STBImage.stbi_failure_reason());
            }
            
            // Store dimensions
            texture.mWidth = width.get(0);
            texture.mHeight = height.get(0);
            
            
            texture.mPath = path;

            // Generate OpenGL texture
            texture.mId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texture.mId);
            
            // Set texture parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);
            
            // Upload texture data
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.mWidth, texture.mHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);
            
            // Generate mipmaps if using mipmap filtering
            if (minFilter == GL_LINEAR_MIPMAP_LINEAR || minFilter == GL_LINEAR_MIPMAP_NEAREST || 
                minFilter == GL_NEAREST_MIPMAP_LINEAR || minFilter == GL_NEAREST_MIPMAP_NEAREST) {
                glGenerateMipmap(GL_TEXTURE_2D);
            }
            
            // Unbind texture
            glBindTexture(GL_TEXTURE_2D, 0);
            
            // Free STB image data
            STBImage.stbi_image_free(imageData);
            
            System.out.println("Loaded texture: " + path + " (" + texture.mWidth + "x" + texture.mHeight + ")");
        }
        
        return texture;
    }
    
    // Utility method to load image resource into ByteBuffer
    private static ByteBuffer loadImageResource(String resourcePath) {
        try (InputStream source = TextureLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (source == null) {
                throw new IOException("Texture resource not found: " + resourcePath);
            }
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = source.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            
            ByteBuffer byteBuffer = BufferUtils.createByteBuffer(out.size());
            byteBuffer.put(out.toByteArray());
            byteBuffer.flip();
            return byteBuffer;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load texture resource: " + resourcePath, e);
        }
    }
    
    // Method to bind a texture for rendering
    public static void BindTexture(Texture texture) {
        if (texture != null && texture.mId != 0) {
            glBindTexture(GL_TEXTURE_2D, texture.mId);
        }
    }
    
    // Method to unbind texture
    public static void UnbindTexture() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    // Method to delete a texture and free OpenGL resources
    public static void DeleteTexture(Texture texture) {
        if (texture != null && texture.mId != 0) {
            glDeleteTextures(texture.mId);
            texture.mId = 0;
            texture.mWidth = 0;
            texture.mHeight = 0;
            texture.mPath = "";
        }
    }
}
