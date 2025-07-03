package com.codebyriley.Core.Rendering.Textures;

import static org.lwjgl.opengl.GL33.*;

public class Texture {
    public int mId;
    public int mWidth;
    public int mHeight;
    public String mPath;

    public Texture() {
        mId = 0;
        mWidth = 0;
        mHeight = 0;
        mPath = "";
    }
    
    public Texture(int id, int width, int height) {
        mId = id;
        mWidth = width;
        mHeight = height;
        mPath = "";
    }

    public Texture(int id, int width, int height, String path) {
        mId = id;
        mWidth = width;
        mHeight = height;
        mPath = path;
    }
    
    // Bind this texture for rendering
    public void bind() {
        if (mId != 0) {
            glBindTexture(GL_TEXTURE_2D, mId);
        }
    }
    
    // Bind to a specific texture unit
    public void bind(int unit) {
        if (mId != 0) {
            glActiveTexture(GL_TEXTURE0 + unit);
            glBindTexture(GL_TEXTURE_2D, mId);
        }
    }
    
    // Unbind this texture
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    // Check if texture is valid
    public boolean isValid() {
        return mId != 0 && mWidth > 0 && mHeight > 0;
    }
    
    // Get aspect ratio
    public float getAspectRatio() {
        return mHeight > 0 ? (float)mWidth / (float)mHeight : 1.0f;
    }
    
    // Delete the texture and free OpenGL resources
    public void delete() {
        if (mId != 0) {
            glDeleteTextures(mId);
            mId = 0;
            mWidth = 0;
            mHeight = 0;
            mPath = "";
        }
    }
}
