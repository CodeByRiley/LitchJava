package com.codebyriley.Core.Rendering.UI;

import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Util.Math.Vector2f;
import com.codebyriley.Util.Math.Vector3f;

/**
 * Base class for all UI elements.
 * Provides common functionality like positioning, sizing, and input handling.
 */
public abstract class UIElement {
    protected float x, y, width, height;
    protected Vector3f backgroundColor;
    protected float backgroundAlpha;
    protected Vector3f borderColor;
    protected float borderAlpha;
    protected float borderThickness;
    protected boolean visible;
    protected boolean enabled;
    protected String id;
    
    public UIElement(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = new Vector3f(0.2f, 0.2f, 0.2f);
        this.backgroundAlpha = 1.0f;
        this.borderColor = new Vector3f(0.8f, 0.8f, 0.8f);
        this.borderAlpha = 1.0f;
        this.borderThickness = 2.0f;
        this.visible = true;
        this.enabled = true;
        this.id = "";
    }
    
    /**
     * Update the UI element (called every frame)
     */
    public abstract void update();
    
    /**
     * Render the UI element
     */
    public abstract void render(UIRenderer renderer);
    
    /**
     * Check if a point is inside this element
     */
    public boolean contains(float pointX, float pointY) {
        return pointX >= x && pointX <= x + width && 
                pointY >= y && pointY <= y + height;
    }
    
    /**
     * Handle mouse click events
     */
    public boolean onMouseClick(float mouseX, float mouseY, int button) {
        if (!enabled || !visible) return false;
        if (contains(mouseX, mouseY)) {
            return handleMouseClick(mouseX, mouseY, button);
        }
        return false;
    }
    
    /**
     * Handle mouse hover events
     */
    public boolean onMouseHover(float mouseX, float mouseY) {
        if (!enabled || !visible) return false;
        return contains(mouseX, mouseY);
    }
    
    /**
     * Override this method to handle specific mouse click logic
     */
    protected boolean handleMouseClick(float mouseX, float mouseY, int button) {
        return false;
    }
    
    /**
     * Draw the background and border
     */
    protected void drawBackground(UIRenderer renderer) {
        if (!visible) return;
        
        // Draw background
        if (backgroundAlpha > 0) {
            renderer.addQuad(
                x, y, width, height,
                0.0f, 0.0f, 1.0f, 1.0f, // Full texture UV
                backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundAlpha,
                0 // White texture
            );
        }
        
        // Draw border
        if (borderAlpha > 0 && borderThickness > 0) {
            // Top border
            renderer.addQuad(x, y + height - borderThickness, width, borderThickness,
                0.0f, 0.0f, 1.0f, 1.0f,
                borderColor.x, borderColor.y, borderColor.z, borderAlpha, 0);
            
            // Bottom border
            renderer.addQuad(x, y, width, borderThickness,
                0.0f, 0.0f, 1.0f, 1.0f,
                borderColor.x, borderColor.y, borderColor.z, borderAlpha, 0);
            
            // Left border
            renderer.addQuad(x, y + borderThickness, borderThickness, height - 2 * borderThickness,
                0.0f, 0.0f, 1.0f, 1.0f,
                borderColor.x, borderColor.y, borderColor.z, borderAlpha, 0);
            
            // Right border
            renderer.addQuad(x + width - borderThickness, y + borderThickness, borderThickness, height - 2 * borderThickness,
                0.0f, 0.0f, 1.0f, 1.0f,
                borderColor.x, borderColor.y, borderColor.z, borderAlpha, 0);
        }
    }
    
    // Getters and setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    
    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }
    
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }
    
    public Vector2f getPosition() { return new Vector2f(x, y); }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
    
    public Vector2f getSize() { return new Vector2f(width, height); }
    public void setSize(float width, float height) { this.width = width; this.height = height; }
    
    public Vector3f getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Vector3f color) { this.backgroundColor = color; }
    
    public float getBackgroundAlpha() { return backgroundAlpha; }
    public void setBackgroundAlpha(float alpha) { this.backgroundAlpha = alpha; }
    
    public Vector3f getBorderColor() { return borderColor; }
    public void setBorderColor(Vector3f color) { this.borderColor = color; }
    
    public float getBorderAlpha() { return borderAlpha; }
    public void setBorderAlpha(float alpha) { this.borderAlpha = alpha; }
    
    public float getBorderThickness() { return borderThickness; }
    public void setBorderThickness(float thickness) { this.borderThickness = thickness; }
    
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
} 