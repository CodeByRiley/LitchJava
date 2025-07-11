package com.codebyriley.Core.Scene.Entities;

import com.codebyriley.Core.Rendering.BatchedRenderer;
import com.codebyriley.Core.Scene.Entities.Components.TexturedComponent;

public class TexturedEntity extends Entity {
    
    public TexturedEntity(String texturePath, float width, float height) {
        super(true, true);
        
        // Add a textured component
        TexturedComponent texturedComp = new TexturedComponent(texturePath, width, height);
        AddComponent(texturedComp);
    } 
    
    public TexturedEntity(String texturePath, float width, float height, float r, float g, float b, float a) {
        super(true, true);
        
        // Add a textured component with custom color
        TexturedComponent texturedComp = new TexturedComponent(texturePath, width, height, r, g, b, a);
        AddComponent(texturedComp);
    }
    
    @Override
    public void Update(float dT) {
        // Update logic here
    }
    
    @Override
    public void FixedUpdate(float fixedDeltaTime) {
        // Fixed update logic here
    }
    
    // @Override
    // public void Draw(BatchedRenderer renderer) {
    //     // Get the textured component using the generic method
    //     TexturedComponent texturedComp = GetComponent(TexturedComponent.class);
        
    //     if (texturedComp != null && texturedComp.mTexture != null) {
    //         // Draw the textured quad
    //         renderer.drawTexturedQuadBatch(
    //             mTransform.mPosition.x, mTransform.mPosition.y,
    //             texturedComp.mWidth, texturedComp.mHeight, 
    //             texturedComp.mTexture.mId, 
    //             texturedComp.mR, texturedComp.mG, texturedComp.mB, texturedComp.mA
    //         );
    //     }
    // }
    
    // Convenience method to get the textured component
    public TexturedComponent getTexturedComponent() {
        return GetComponent(TexturedComponent.class);
    }
    
    // Convenience method to change the texture color
    public void setTextureColor(float r, float g, float b, float a) {
        TexturedComponent texturedComp = GetComponent(TexturedComponent.class);
        if (texturedComp != null) {
            texturedComp.setColor(r, g, b, a);
        }
    }
    
    // Convenience method to change the texture size
    public void setTextureSize(float width, float height) {
        TexturedComponent texturedComp = GetComponent(TexturedComponent.class);
        if (texturedComp != null) {
            texturedComp.setSize(width, height);
        }
    }
} 