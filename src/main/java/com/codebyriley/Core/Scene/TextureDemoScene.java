package com.codebyriley.Core.Scene;

import com.codebyriley.Core.Rendering.Primatives.Renderer;
import com.codebyriley.Core.Rendering.Text.FontLoader;
import com.codebyriley.Core.Rendering.Text.TextRenderer;
import com.codebyriley.Core.Rendering.Textures.Texture;
import com.codebyriley.Core.Rendering.Textures.TextureLoader;
import com.codebyriley.Core.Scene.Entities.Entity;
import com.codebyriley.Core.Scene.Entities.TexturedEntity;
import com.codebyriley.Core.Scene.Entities.Components.TexturedComponent;

import static org.lwjgl.opengl.GL11.*;

import com.codebyriley.Core.Rendering.WindowBase;

public class TextureDemoScene extends SceneBase {
    private FontLoader font;
    private Texture demoTexture;
    private float elapsedTime = 0.0f;
    private boolean textureLoaded = false;

    public TextureDemoScene(FontLoader font) {
        this.font = font;
        loadDemoTexture();

        Entity demoEntity = new TexturedEntity("textures/ships/ship_E.png", 100, 100);
        demoEntity.mName = "DemoEntity";
        AddEntity(demoEntity);
    }
    
    private void loadDemoTexture() {
        try {
            // Try to load a texture - you would need to add an image file to your resources
            // For now, we'll create a simple colored texture programmatically
            textureLoaded = true;
            System.out.println("Created demo texture: " + demoTexture.mWidth + "x" + demoTexture.mHeight);
        } catch (Exception e) {
            System.err.println("Failed to load texture: " + e.getMessage());
            textureLoaded = false;
        }
    }

    public Texture[] LoadEntityTextures() {
        Texture[] textures = new Texture[entities.size()];
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity.mIsVisible) {
                TexturedComponent texturedComponent = entity.GetComponent(TexturedComponent.class);
                System.out.println("Loading texture: " + texturedComponent.mTexturePath);
                texturedComponent.mTexture = TextureLoader.LoadTexture(texturedComponent.mTexturePath);
                textures[i] = texturedComponent.mTexture;
            }
        }
        return textures;
    }
    
    // Create a simple colored texture for demonstration
    // private Texture createSimpleTexture() {
    //     Texture texture = new Texture();
    //     texture.mWidth = 64;
    //     texture.mHeight = 64;
        
    //     // Generate a simple checkerboard pattern
    //     byte[] imageData = new byte[texture.mWidth * texture.mHeight * 4]; // RGBA
    //     for (int y = 0; y < texture.mHeight; y++) {
    //         for (int x = 0; x < texture.mWidth; x++) {
    //             int index = (y * texture.mWidth + x) * 4;
    //             boolean isChecker = ((x / 8) + (y / 8)) % 2 == 0;
                
    //             if (isChecker) {
    //                 imageData[index] = (byte)255;     // R
    //                 imageData[index + 1] = (byte)255; // G
    //                 imageData[index + 2] = (byte)255; // B
    //                 imageData[index + 3] = (byte)255; // A
    //             } else {
    //                 imageData[index] = (byte)100;     // R
    //                 imageData[index + 1] = (byte)150; // G
    //                 imageData[index + 2] = (byte)200; // B
    //                 imageData[index + 3] = (byte)255; // A
    //             }
    //         }
    //     }
        
    //     // Create OpenGL texture
    //     texture.mId = glGenTextures();
    //     glBindTexture(GL_TEXTURE_2D, texture.mId);
    //     glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    //     glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    //     glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    //     glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
    //     // Upload texture data
    //     glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texture.mWidth, texture.mHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, org.lwjgl.BufferUtils.createByteBuffer(imageData.length).put(imageData).flip());
    //     glBindTexture(GL_TEXTURE_2D, 0);
        
    //     return texture;
    // }
    
    @Override
    public void Update(float dT) {
        elapsedTime += dT / 500.0f;

        for (Entity entity : entities) {
            if (entity.mIsVisible) {
                if (entity.getClass() == TexturedEntity.class) {
                    if (entity.mName == "DemoEntity") {
                        entity.mTransform.mPosition.x += 0.25f * dT;
                        entity.mTransform.mRotation += 0.0007f * dT;
                    }
                }
            }
        }
    }
    
    @Override
    public void FixedUpdate(float fixedDeltaTime) {
        // Physics updates would go here
    }
    
    @Override
    public void Draw(Renderer renderer, TextRenderer textRenderer) {
        // Start batching
        renderer.beginBatch();

        for (Entity entity : entities) {
            if (entity.mIsVisible) {
                TexturedComponent texturedComponent = entity.GetComponent(TexturedComponent.class);
                renderer.drawTexturedQuadBatch(entity.mTransform.mPosition.x, entity.mTransform.mPosition.y, texturedComponent.mWidth, texturedComponent.mHeight, texturedComponent.mTexture.mId, 1.0f, 1.0f, 1.0f, 1.0f, entity.mTransform.mRotation);
            }
        }
        
        // End batching
        renderer.endBatch();
        
        // Draw UI text
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        
        if (textureLoaded) {
            textRenderer.drawText("Texture Demo - Loaded Successfully", 175, 75, 0.75f, 0.0f, 1.0f, 0.0f);
            textRenderer.drawText("Texture Size: " + demoTexture.mWidth + "x" + demoTexture.mHeight, 175, 100, 0.5f, 1.0f, 1.0f, 1.0f);
        } else {
            textRenderer.drawText("Texture Demo - Failed to Load", 175, 75, 0.75f, 1.0f, 0.0f, 0.0f);
        }
        
        textRenderer.drawText("Press T to load texture from file", 175, 125, 0.5f, 1.0f, 1.0f, 1.0f);
        
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
    }
    
    // Method to load texture from file (example usage)
    public void loadTextureFromFile(String path) {
        try {
            if (demoTexture != null) {
                demoTexture.delete(); // Clean up old texture
            }
            demoTexture = TextureLoader.LoadTexture(path);
            textureLoaded = true;
            System.out.println("Loaded texture from file: " + path);
        } catch (Exception e) {
            System.err.println("Failed to load texture from file: " + e.getMessage());
            textureLoaded = false;
        }
    }
    
    // Cleanup method
    public void cleanup() {
        if (demoTexture != null) {
            demoTexture.delete();
            demoTexture = null;
        }
    }
} 