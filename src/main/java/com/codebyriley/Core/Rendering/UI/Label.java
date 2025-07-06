package com.codebyriley.Core.Rendering.UI;

import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Util.Log;
import com.codebyriley.Util.Math.Vector2f;
import com.codebyriley.Util.Math.Vector3f;

/**
 * A simple text label UI element.
 */
public class Label extends UIElement {
    private String text;
    private TextRenderer textRenderer;
    private Vector3f textColor;
    private float textAlpha;
    private float textScale;
    private boolean autoSize;
    private TextAlignment alignment;
    
    public enum TextAlignment {
        LEFT, CENTER, RIGHT
    }
    
    public Label(float x, float y, String text, TextRenderer textRenderer) {
        super(x, y, 0, 0); // Size will be calculated based on text
        this.text = text;
        this.textRenderer = textRenderer;
        this.textColor = new Vector3f(1.0f, 0.0f, 0.0f);
        this.textAlpha = 1.0f;
        this.textScale = 1.0f;
        this.autoSize = true;
        this.alignment = TextAlignment.LEFT;
        
        // Set transparent background for labels
        setBackgroundAlpha(0.0f);
        setBorderAlpha(0.0f);
        
        // Calculate initial size
        if (textRenderer != null && text != null) {
            updateSize();
        }
    }
    
    public Label(float x, float y, float width, float height, String text, TextRenderer textRenderer) {
        super(x, y, width, height);
        this.text = text;
        this.textRenderer = textRenderer;
        this.textColor = new Vector3f(1.0f, 0.0f, 0.0f);
        this.textAlpha = 1.0f;
        this.textScale = 1.0f;
        this.autoSize = false;
        this.alignment = TextAlignment.LEFT;
        
        // Set transparent background for labels
        setBackgroundAlpha(0.0f);
        setBorderAlpha(0.0f);
    }
    
    @Override
    public void update() {
        // Labels don't need update logic
    }
    
    @Override
    public void render(UIRenderer renderer) {

        if (!visible || textRenderer == null || text == null) return;

        // Draw background if needed
        drawBackground(renderer);

        // FLUSH UI BATCH before drawing text!
        renderer.end();

        // Calculate text position based on alignment
        float textX, textY;
        if (autoSize) {
            // For auto-sized labels, text starts at x, y
            textX = x;
            textY = y;
        } else {
            // For fixed-size labels, position based on alignment
            textY = y + height / 2.0f;
            
            switch (alignment) {
                case LEFT:
                    textX = x;
                    break;
                case CENTER:
                    textX = x + width / 2.0f;
                    break;
                case RIGHT:
                    textX = x + width;
                    break;
                default:
                    textX = x;
                    break;
            }
        }

        // Draw text using the new TextRenderer (which uses TextBatchRenderer internally)
        switch (alignment) {
            case LEFT:
                textRenderer.drawText(text, textX, textY, textColor, 1.0f, textScale);
                break;
            case CENTER:
                textRenderer.drawTextCentered(text, textX, textY, textColor, 1.0f, textScale);
                break;
            case RIGHT:
                textRenderer.drawTextRight(text, textX, textY, textColor, 1.0f, textScale);
                break;
        }

        // RESTART UI BATCH for any further UI drawing
        renderer.begin();
    }
    
    /**
     * Update the label size based on text content
     */
    private void updateSize() {
        if (textRenderer != null && text != null) {
            Vector2f textSize = textRenderer.getTextSize(text, textScale);
            this.width = textSize.x;
            this.height = textSize.y;
        }
    }
    
    /**
     * Handle mouse click events
     */
    @Override
    protected boolean handleMouseClick(float mouseX, float mouseY, int button) {
        Log.info("Label clicked");
        return true;
    }

    /**
     * Set the label text
     */
    public void setText(String text) {
        this.text = text;
        if (autoSize) {
            updateSize();
        }
    }
    
    /**
     * Get the label text
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
     * Set the text scale
     */
    public void setTextScale(float scale) {
        this.textScale = scale;
        if (autoSize) {
            updateSize();
        }
    }
    
    /**
     * Get the text scale
     */
    public float getTextScale() {
        return textScale;
    }
    
    /**
     * Set the text alignment
     */
    public void setAlignment(TextAlignment alignment) {
        this.alignment = alignment;
    }
    
    /**
     * Get the text alignment
     */
    public TextAlignment getAlignment() {
        return alignment;
    }
    
    /**
     * Set whether the label should auto-size based on text content
     */
    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
        if (autoSize) {
            updateSize();
        }
    }
    
    /**
     * Check if the label auto-sizes
     */
    public boolean isAutoSize() {
        return autoSize;
    }
} 