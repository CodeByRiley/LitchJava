package com.codebyriley.Core.Scene.Entities.Components;

import com.codebyriley.Core.Rendering.Textures.Texture;
import com.codebyriley.Core.Rendering.Textures.TextureLoader;

public class TexturedComponent extends ComponentBase {
    public Texture mTexture;
    public String mTexturePath;
    public float mWidth;
    public float mHeight;
    public float mR, mG, mB, mA;
    
    public TexturedComponent(String texturePath, float width, float height) {
        super("TexturedComponent");
        mTexturePath = texturePath;
        mTexture = TextureLoader.LoadTexture(texturePath);
        mWidth = width;
        mHeight = height;
        mR = 1.0f;
        mG = 1.0f;
        mB = 1.0f;
        mA = 1.0f;
    }
    
    public TexturedComponent(String texturePath, float width, float height, float r, float g, float b, float a) {
        super("TexturedComponent");
        mTexturePath = texturePath;
        mWidth = width;
        mHeight = height;
        mR = r;
        mG = g;
        mB = b;
        mA = a;
    }
    
    public void setColor(float r, float g, float b, float a) {
        mR = r;
        mG = g;
        mB = b;
        mA = a;
    }
    
    public void setSize(float width, float height) {
        mWidth = width;
        mHeight = height;
    }

    public void SetTexture(Texture texture) {
        mTexture = texture;
    }

    public Texture GetTexture() {
        return mTexture;
    }
}