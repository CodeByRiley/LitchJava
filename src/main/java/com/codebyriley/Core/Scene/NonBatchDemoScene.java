package com.codebyriley.Core.Scene;

// TEMPLATE USAGE:
// renderer.begin();
// ... draw all batched quads ...
// renderer.end();
// Always call end() before scene swap or at the end of Draw().

import com.codebyriley.Core.Rendering.BatchedRenderer;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Util.Math.Vector3f;

import static org.lwjgl.opengl.GL11.*;

import com.codebyriley.Core.Rendering.WindowBase;

public class NonBatchDemoScene extends SceneBase {
    private float elapsedTime = 0.0f;
    @Override
    public void Update(float dT) {
        elapsedTime += dT / 500.0f;
    }
    
    @Override
    public void FixedUpdate(float fixedDeltaTime) {
        // Physics updates would go here
    }
    
    @Override
    public void Draw(BatchedRenderer renderer, TextRenderer textRenderer) {
        // Draw background colored quads (individual draw calls - inefficient)
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
        
            // Draw individual quads (inefficient - no batching)
            renderer.begin();
            renderer.addQuad(centerX - half, centerY - half, size, size, 0.0f, 0.0f, 1.0f, 1.0f, r, g, b, 0.7f, 0);
            renderer.end();
        }
        
        // Draw some larger colored quads
        renderer.begin();
        renderer.addQuad(100, 100, 200, 200, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0);
        renderer.addQuad(350, 100, 200, 200, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0);
        renderer.addQuad(600, 100, 200, 200, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0);
        renderer.end();
        

        // Draw some more colored quads (deterministic positions)
        renderer.begin();
        for (int i = 0; i < 20; i++) {
            float x = (float)(Math.sin(elapsedTime + i * 0.5f) * WindowBase.windowWidth * 0.4f + WindowBase.windowWidth * 0.5f);
            float y = (float)(Math.cos(elapsedTime + i * 0.3f) * WindowBase.windowHeight * 0.4f + WindowBase.windowHeight * 0.5f);
            float size = 15.0f;
            float r = 0.5f + 0.5f * (float)Math.sin(elapsedTime + i * 0.2f);
            float g = 0.5f + 0.5f * (float)Math.cos(elapsedTime + i * 0.2f);
            float b = 0.5f + 0.5f * (float)Math.sin(elapsedTime * 0.5f + i * 0.2f);
            renderer.addQuad(x, y, size, size, 0.0f, 0.0f, 1.0f, 1.0f, r, g, b, 1.0f, 0);
        }
        renderer.end();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        textRenderer.drawText("Non-Batched Demo", 175, 75, new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
    }
} 