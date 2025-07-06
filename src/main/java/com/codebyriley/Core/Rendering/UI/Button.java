package com.codebyriley.Core.Rendering.UI;

import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Util.Log;
import com.codebyriley.Util.Math.Vector3f;
import java.util.function.Consumer;

/**
 * A clickable button UI element with text and hover effects.
 */
public class Button extends UIElement {
    private String text;
    private TextRenderer textRenderer;
    private Vector3f textColor;
    private float textAlpha;
    private Vector3f hoverColor;
    private Vector3f pressedColor;
    private boolean isHovered;
    private boolean isPressed;
    private Consumer<Button> onClickCallback;
    private float textScale;
    
    public Button(float x, float y, float width, float height, String text, TextRenderer textRenderer) {
        super(x, y, width, height);
        this.text = text;
        this.textRenderer = textRenderer;
        this.textColor = new Vector3f(1.0f, 1.0f, 1.0f);
        this.textAlpha = 1.0f;
        this.hoverColor = new Vector3f(0.3f, 0.3f, 0.3f);
        this.pressedColor = new Vector3f(0.1f, 0.1f, 0.1f);
        this.isHovered = false;
        this.isPressed = false;
        this.onClickCallback = null;
        this.textScale = 1.0f;
        
        // Set default button colors
        setBackgroundColor(new Vector3f(0.4f, 0.4f, 0.4f));
        setBorderColor(new Vector3f(0.6f, 0.6f, 0.6f));
    }
    
    @Override
    public void update() {
        // Button-specific update logic can go here
    }
    
    @Override
    public void render(UIRenderer renderer) {
        if (!visible) return;
        
        // Determine background color based on state
        Vector3f currentBgColor = backgroundColor;
        if (isPressed) {
            currentBgColor = pressedColor;
        } else if (isHovered) {
            currentBgColor = hoverColor;
        }
        
        // Draw background with current color
        if (backgroundAlpha > 0) {
            renderer.addQuad(
                x, y, width, height,
                0.0f, 0.0f, 1.0f, 1.0f, // Full texture UV
                currentBgColor.x, currentBgColor.y, currentBgColor.z, backgroundAlpha,
                0 // White texture
            );
        }
        
        // Draw border
        drawBackground(renderer);
        
        // Draw text centered in button using the new TextRenderer
        if (textRenderer != null && text != null && !text.isEmpty()) {
            float textX = x + width / 2.0f;
            float textY = y + height / 2.0f;
            textRenderer.drawTextCentered(text, textX, textY, textColor, 1.0f, textScale);
        }
    }
    
    @Override
    protected boolean handleMouseClick(float mouseX, float mouseY, int button) {
        if (button == 0) { // Left mouse button
            isPressed = true;
            Log.info("Button clicked");
            if (onClickCallback != null) {
                onClickCallback.accept(this);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Set the click callback for this button
     */
    public void setOnClick(Consumer<Button> callback) {
        this.onClickCallback = callback;
    }
    
    /**
     * Set the button text
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Get the button text
     */
    public String getText() {
        return text;
    }
    
    /**
     * Set the text color
     */
    public void setTextColor(Vector3f color) {
        this.textColor = color;
    }
    
    /**
     * Set the text alpha
     */
    public void setTextAlpha(float alpha) {
        this.textAlpha = alpha;
    }
    
    /**
     * Set the hover color
     */
    public void setHoverColor(Vector3f color) {
        this.hoverColor = color;
    }
    
    /**
     * Set the pressed color
     */
    public void setPressedColor(Vector3f color) {
        this.pressedColor = color;
    }
    
    /**
     * Set the text scale
     */
    public void setTextScale(float scale) {
        this.textScale = scale;
    }
    
    /**
     * Get the text scale
     */
    public float getTextScale() {
        return textScale;
    }
    
    /**
     * Check if button is currently hovered
     */
    public boolean isHovered() {
        return isHovered;
    }
    
    /**
     * Check if button is currently pressed
     */
    public boolean isPressed() {
        return isPressed;
    }
    
    /**
     * Set hover state (called by UI manager)
     */
    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
    }
    
    /**
     * Set pressed state (called by UI manager)
     */
    public void setPressed(boolean pressed) {
        this.isPressed = pressed;
    }
    
    /**
     * Release the button (called when mouse is released)
     */
    public void release() {
        isPressed = false;
    }
} 