package com.codebyriley.Core.Scene;

import com.codebyriley.Core.Rendering.Primatives.Renderer;
import com.codebyriley.Core.Rendering.Text.FontLoader;
import com.codebyriley.Core.Rendering.Text.TextRenderer;
import com.codebyriley.Core.Scene.Entities.Entity;
import com.codebyriley.Core.Scene.Entities.TexturedEntity;
import com.codebyriley.Core.Scene.Entities.Components.TexturedComponent;

import static org.lwjgl.opengl.GL11.*;


import com.codebyriley.Core.Rendering.WindowBase;

public class BatchDemoScene extends SceneBase {
    private FontLoader font;
    private float elapsedTime = 0.0f;
    


    public BatchDemoScene(FontLoader font) {
        this.font = font;

        // Create Demo Entity
        TexturedEntity demoEntity = new TexturedEntity("textures/ships/ship_E.png", 100, 100);
        AddEntity(demoEntity);
    }
    
    @Override
    public void Update(float dT) {
        elapsedTime += dT / 500.0f;
    }
    
    @Override
    public void FixedUpdate(float fixedDeltaTime) {
        // Physics updates would go here
        
    }
    
    @Override
    public void Draw(Renderer renderer, TextRenderer textRenderer) {
        // Start batching
        renderer.beginBatch();
        

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
        
            // Compute rotated positions (not used for axis-aligned rendering)
            // for (int j = 0; j < 4; j++) {
            //     tempPositions[j] = centerX + dx[j] * cosA - dy[j] * sinA;
            //     tempPositions[j] = centerY + dx[j] * sinA + dy[j] * cosA;
            // }
        
            // If your renderer supports drawing arbitrary quads, use px/py for the corners.
            // If not, you can only draw axis-aligned quads at (centerX, centerY) with size.
        
            // For axis-aligned (no rotation), just use:
            // renderer.drawColoredQuadBatch(centerX - half, centerY - half, size, size, r, g, b, 0.7f);
        
            // For color:
            float r = 0.5f + 0.5f * (float)Math.sin(elapsedTime + i * 0.1f);
            float g = 0.5f + 0.5f * (float)Math.cos(elapsedTime + i * 0.1f);
            float b = 0.5f + 0.5f * (float)Math.sin(elapsedTime * 0.5f + i * 0.1f);
        
            // If you only support axis-aligned quads:
            //renderer.drawColoredQuadBatch(centerX - half, centerY - half, size, size, r, g, b, 0.7f);
            // If you want to support rotated quads, you need to extend your renderer.

            // for (int j = 0; j < entities.size(); j++) {
            //     Entity entity = entities.get(j);
            //     if (entity.mIsVisible) {
            //         if (entity.getClass() == TexturedEntity.class) {
            //             renderer.drawTexturedQuadBatch(entity.mTransform.mPosition.x, entity.mTransform.mPosition.y, 10, 10, entity.GetComponent(TexturedComponent.class).mTexture.mId, 1, 1, 1, 1);
            //         }
            //     }
            // }
        }

        // Draw textured quads (this will cause a batch flush and start a new batch)
        if (font != null) {
            // Draw multiple textured quads with the same texture (they'll batch together)
            for (int i = 0; i < 5; i++) {
                float x = 100 + i * 120;
                float y = 400;
                float size = 100;
                float alpha = 0.5f + 0.5f * (float)Math.sin(elapsedTime + i);
                renderer.drawTexturedQuadBatch(x, y, size, size, font.getTextureId(), 1.0f, 1.0f, 1.0f, alpha);
            }
        }
        

        // Draw some more colored quads (new batch since we switched from textured)
        // for (int i = 0; i < 20; i++) {
        //     float x = (float)(Math.random() * WindowBase.windowWidth);
        //     float y = (float)(Math.random() * WindowBase.windowHeight);
        //     float size = 15.0f;
        //     float r = (float)Math.random();
        //     float g = (float)Math.random();
        //     float b = (float)Math.random();
        //     renderer.drawColoredQuadBatch(x, y, size, size, r, g, b, 0.6f);
        // }
        
        // End batching
        renderer.endBatch();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        textRenderer.drawText("Batched Demo", 175, 75, 0.75f, 1.0f, 1.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
    }
} 