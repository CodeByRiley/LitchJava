package com.codebyriley.Core.Scene;

// TEMPLATE USAGE:
// renderer.begin();
// ... draw all batched quads ...
// renderer.end();
// Always call end() before scene swap or at the end of Draw().

import com.codebyriley.Core.Rendering.BatchedRenderer;
import com.codebyriley.Core.Rendering.UI.Text.FontLoader;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Core.Scene.Entities.Entity;
import com.codebyriley.Core.Scene.Entities.TexturedEntity;
import com.codebyriley.Core.Scene.Entities.Components.TexturedComponent;
import com.codebyriley.Util.Math.Vector3f;

import static org.lwjgl.opengl.GL11.*;


import com.codebyriley.Core.Rendering.WindowBase;

public class BatchDemoScene extends SceneBase {
    private FontLoader font;
    private float elapsedTime = 0.0f;
    private boolean isInitialized = false;
    

    public BatchDemoScene(FontLoader font) {
        this.font = font;
        // Don't initialize OpenGL resources here - defer until after rendering
    }
    
    private void init() {
        if (isInitialized) return;
        
        // Create Demo Entity
        TexturedEntity demoEntity = new TexturedEntity("textures/ships/ship_E.png", 100, 100);
        AddEntity(demoEntity);
        
        isInitialized = true;
    }
    
    @Override
    public void Update(float dT) {
        // Initialize on first update if not already done
        if (!isInitialized) {
            init();
        }
        
        elapsedTime += dT / 500.0f;
    }
    
    @Override
    public void FixedUpdate(float fixedDeltaTime) {
        // Physics updates would go here
        
    }
    
    @Override
    public void Draw(BatchedRenderer renderer, TextRenderer textRenderer) {
        // Initialize on first draw if not already done
        if (!isInitialized) {
            init();
        }
        
        // Start batching
        renderer.begin();
        

        for (int i = 0; i < 200; i++) {
            float centerX = (float)(Math.sin(elapsedTime + i * 0.1f) * 100 + WindowBase.windowWidth / 2);
            float centerY = (float)(Math.cos(elapsedTime + i * 0.1f) * 100 + WindowBase.windowHeight / 2);
            float size = 10.0f + (float)(Math.sin(elapsedTime * 2 + i) * 5.0f);
            float angle = elapsedTime * 2.0f + i; // Each quad spins at a different rate/phase
        
            // Calculate the four corners of the rotated quad
            float half = size / 2.0f;
            float cosA = (float)Math.cos(angle);
            float sinA = (float)Math.sin(angle);
        
            // Offset from center for each corner
            float[] dx = { -half,  half,  half, -half };
            float[] dy = { -half, -half,  half,  half };
        
            // For color:
            float r = 0.5f + 0.5f * (float)Math.sin(elapsedTime + i * 0.1f);
            float g = 0.5f + 0.5f * (float)Math.cos(elapsedTime + i * 0.1f);
            float b = 0.5f + 0.5f * (float)Math.sin(elapsedTime * 0.5f + i * 0.1f);
        
            // Draw colored quads with rotation
            renderer.addQuad(centerX, centerY, size, size, 0.0f, 0.0f, 1.0f, 1.0f, r, g, b, 0.7f, 0);
        }

        // Draw textured quads (this will cause a batch flush and start a new batch)
        if (font != null) {
            // Draw multiple textured quads with the same texture (they'll batch together)
            for (int i = 0; i < 5; i++) {
                float x = 100 + i * 120;
                float y = 400;
                float size = 100;
                float alpha = 0.5f + 0.5f * (float)(Math.sin(elapsedTime + i));
                renderer.addQuad(x, y, size, size, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, alpha, font.getTextureId());
            }
        }
        
        // End batching
        renderer.end();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        if (textRenderer != null) {
            textRenderer.drawText("Batched Demo", 175, 75, new Vector3f(1.0f, 1.0f, 1.0f), 0.75f);
        }
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
    }

    public void forceInitialize() {
        init();
    }
} 