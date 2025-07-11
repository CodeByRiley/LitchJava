package com.codebyriley.Core.Rendering.UI;

import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Util.Math.Vector3f;

/**
 * A progress bar UI element for showing progress/completion.
 */
public class ProgressBar extends UIElement {
    private float progress; // 0.0 to 1.0
    private Vector3f fillColor;
    private Vector3f emptyColor;
    private boolean showText;
    private String text;
    private TextRenderer textRenderer;
    private Vector3f textColor;
    private float textScale;
    private boolean animated;
    private float animationSpeed;
    private float animatedProgress;
    
    public ProgressBar(float x, float y, float width, float height, float initialProgress) {
        super(x, y, width, height);
        this.progress = Math.max(0.0f, Math.min(1.0f, initialProgress));
        this.animatedProgress = this.progress;
        this.fillColor = new Vector3f(0.2f, 0.8f, 0.2f);
        this.emptyColor = new Vector3f(0.3f, 0.3f, 0.3f);
        this.showText = false;
        this.text = "";
        this.textRenderer = null;
        this.textColor = new Vector3f(1.0f, 1.0f, 1.0f);
        this.textScale = 0.8f;
        this.animated = false;
        this.animationSpeed = 2.0f;
        
        // Set default colors
        setBackgroundColor(new Vector3f(0.1f, 0.1f, 0.1f));
        setBorderColor(new Vector3f(0.5f, 0.5f, 0.5f));
        setBorderThickness(2.0f);
    }
    
    public ProgressBar(float x, float y, float width, float height, float initialProgress, TextRenderer textRenderer) {
        this(x, y, width, height, initialProgress);
        this.textRenderer = textRenderer;
        this.showText = true;
    }
    
    @Override
    public void update() {
        if (animated && animatedProgress != progress) {
            float diff = progress - animatedProgress;
            if (Math.abs(diff) < 0.01f) {
                animatedProgress = progress;
            } else {
                animatedProgress += diff * animationSpeed * 0.016f; // Assuming 60 FPS
            }
        }
    }
    
    @Override
    public void render(UIRenderer renderer) {
        if (!visible) return;
        
        float currentProgress = animated ? animatedProgress : progress;
        
        // Draw background
        float centerX = x + width / 2.0f;
        float centerY = y + height / 2.0f;
        drawBackground(renderer); // Already uses center-based coordinates now
        
        // Draw empty portion
        renderer.addQuad(centerX, centerY, width, height,
            0.0f, 0.0f, 1.0f, 1.0f,
            emptyColor.x, emptyColor.y, emptyColor.z, 1.0f, 0);
        
        // Draw filled portion
        if (currentProgress > 0.0f) {
            float fillWidth = width * currentProgress;
            renderer.addQuad(x + fillWidth / 2.0f, centerY, fillWidth, height,
                0.0f, 0.0f, 1.0f, 1.0f,
                fillColor.x, fillColor.y, fillColor.z, 1.0f, 0);
        }
        
        // Draw text if enabled
        if (showText && textRenderer != null) {
            String displayText = text.isEmpty() ? 
                String.format("%.0f%%", currentProgress * 100) : text;
            
            float textX = x + width / 2.0f;
            float textY = y + height / 2.0f;
            textRenderer.drawTextCentered(displayText, textX, textY, textColor, 1.0f, textScale);
        }
    }
    
    @Override
    protected boolean handleMouseClick(float mouseX, float mouseY, int button) {
        // Progress bars are typically read-only, but you could implement click-to-set-progress here
        return false;
    }
    
    /**
     * Set the progress value (0.0 to 1.0)
     */
    public void setProgress(float progress) {
        this.progress = Math.max(0.0f, Math.min(1.0f, progress));
        if (!animated) {
            this.animatedProgress = this.progress;
        }
    }
    
    /**
     * Get the current progress value
     */
    public float getProgress() {
        return progress;
    }
    
    /**
     * Set the fill color
     */
    public void setFillColor(Vector3f color) {
        this.fillColor = color;
    }
    
    /**
     * Set the empty color
     */
    public void setEmptyColor(Vector3f color) {
        this.emptyColor = color;
    }
    
    /**
     * Set whether to show text
     */
    public void setShowText(boolean showText) {
        this.showText = showText;
    }
    
    /**
     * Set the custom text (if empty, shows percentage)
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Set the text renderer
     */
    public void setTextRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }
    
    /**
     * Set the text color
     */
    public void setTextColor(Vector3f color) {
        this.textColor = color;
    }
    
    /**
     * Set the text scale
     */
    public void setTextScale(float scale) {
        this.textScale = scale;
    }
    
    /**
     * Set whether the progress bar should animate
     */
    public void setAnimated(boolean animated) {
        this.animated = animated;
        if (!animated) {
            this.animatedProgress = this.progress;
        }
    }
    
    /**
     * Set the animation speed
     */
    public void setAnimationSpeed(float speed) {
        this.animationSpeed = speed;
    }
    
    /**
     * Check if progress bar is animated
     */
    public boolean isAnimated() {
        return animated;
    }
    
    /**
     * Get the current animated progress
     */
    public float getAnimatedProgress() {
        return animatedProgress;
    }
    
    /**
     * Set progress with animation
     */
    public void setProgressAnimated(float progress) {
        setAnimated(true);
        setProgress(progress);
    }
    
    /**
     * Set progress without animation
     */
    public void setProgressImmediate(float progress) {
        setAnimated(false);
        setProgress(progress);
    }
} 