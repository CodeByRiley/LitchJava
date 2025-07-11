package com.codebyriley.Core.Rendering.UI;

import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Util.Math.Vector3f;
import java.util.function.Consumer;

/**
 * A slider UI element for numeric input with drag functionality.
 */
public class Slider extends UIElement {
    private float minValue;
    private float maxValue;
    private float currentValue;
    private float handleSize;
    private boolean isDragging;
    private Vector3f handleColor;
    private Vector3f trackColor;
    private Vector3f fillColor;
    private Consumer<Float> onValueChanged;
    private boolean vertical;
    
    public Slider(float x, float y, float width, float height, float minValue, float maxValue, float initialValue) {
        super(x, y, width, height);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = Math.max(minValue, Math.min(maxValue, initialValue));
        this.handleSize = Math.min(width, height) * 0.2f;
        this.isDragging = false;
        this.vertical = height > width;
        
        // Set default colors
        this.handleColor = new Vector3f(0.8f, 0.8f, 0.8f);
        this.trackColor = new Vector3f(0.3f, 0.3f, 0.3f);
        this.fillColor = new Vector3f(0.6f, 0.6f, 0.6f);
        
        // Set background to transparent
        setBackgroundAlpha(0.0f);
        setBorderAlpha(0.0f);
    }
    
    @Override
    public void update() {
        // Slider-specific update logic can go here
    }
    
    @Override
    public void render(UIRenderer renderer) {
        if (!visible) return;
        
        float trackX, trackY, trackWidth, trackHeight;
        float handleX, handleY;
        
        if (vertical) {
            // Vertical slider
            trackX = x + (width - handleSize) / 2.0f;
            trackY = y + handleSize / 2.0f;
            trackWidth = handleSize;
            trackHeight = height - handleSize;
            
            float progress = (currentValue - minValue) / (maxValue - minValue);
            handleX = x + (width - handleSize) / 2.0f;
            handleY = y + (height - handleSize) - (trackHeight * progress);
        } else {
            // Horizontal slider
            trackX = x + handleSize / 2.0f;
            trackY = y + (height - handleSize) / 2.0f;
            trackWidth = width - handleSize;
            trackHeight = handleSize;
            
            float progress = (currentValue - minValue) / (maxValue - minValue);
            handleX = x + (trackWidth * progress);
            handleY = y + (height - handleSize) / 2.0f;
        }
        
        // Draw track background
        float trackCenterX = trackX + trackWidth / 2.0f;
        float trackCenterY = trackY + trackHeight / 2.0f;
        renderer.addQuad(trackCenterX, trackCenterY, trackWidth, trackHeight,
            0.0f, 0.0f, 1.0f, 1.0f,
            trackColor.x, trackColor.y, trackColor.z, 1.0f, 0);
        
        // Draw filled portion
        if (vertical) {
            float progress = (currentValue - minValue) / (maxValue - minValue);
            float fillHeight = trackHeight * progress;
            float fillY = trackY + trackHeight - fillHeight;
            renderer.addQuad(trackCenterX, fillY + fillHeight / 2.0f, trackWidth, fillHeight,
                0.0f, 0.0f, 1.0f, 1.0f,
                fillColor.x, fillColor.y, fillColor.z, 1.0f, 0);
        } else {
            float progress = (currentValue - minValue) / (maxValue - minValue);
            float fillWidth = trackWidth * progress;
            renderer.addQuad(trackCenterX - (trackWidth - fillWidth) / 2.0f, trackCenterY, fillWidth, trackHeight,
                0.0f, 0.0f, 1.0f, 1.0f,
                fillColor.x, fillColor.y, fillColor.z, 1.0f, 0);
        }
        
        // Draw handle
        renderer.addQuad(handleX + handleSize / 2.0f, handleY + handleSize / 2.0f, handleSize, handleSize,
            0.0f, 0.0f, 1.0f, 1.0f,
            handleColor.x, handleColor.y, handleColor.z, 1.0f, 0);
    }
    
    @Override
    protected boolean handleMouseClick(float mouseX, float mouseY, int button) {
        if (button == 0) { // Left mouse button
            isDragging = true;
            updateValueFromMouse(mouseX, mouseY);
            return true;
        }
        return false;
    }
    
    /**
     * Handle mouse drag
     */
    public void onMouseDrag(float mouseX, float mouseY) {
        if (isDragging) {
            updateValueFromMouse(mouseX, mouseY);
        }
    }
    
    /**
     * Handle mouse release
     */
    public void onMouseRelease() {
        isDragging = false;
    }
    
    /**
     * Update value based on mouse position
     */
    private void updateValueFromMouse(float mouseX, float mouseY) {
        float progress;
        
        if (vertical) {
            float trackY = y + handleSize / 2.0f;
            float trackHeight = height - handleSize;
            progress = 1.0f - Math.max(0.0f, Math.min(1.0f, (mouseY - trackY) / trackHeight));
        } else {
            float trackX = x + handleSize / 2.0f;
            float trackWidth = width - handleSize;
            progress = Math.max(0.0f, Math.min(1.0f, (mouseX - trackX) / trackWidth));
        }
        
        float newValue = minValue + (maxValue - minValue) * progress;
        setValue(newValue);
    }
    
    /**
     * Set the current value
     */
    public void setValue(float value) {
        float oldValue = currentValue;
        currentValue = Math.max(minValue, Math.min(maxValue, value));
        
        if (currentValue != oldValue && onValueChanged != null) {
            onValueChanged.accept(currentValue);
        }
    }
    
    /**
     * Get the current value
     */
    public float getValue() {
        return currentValue;
    }
    
    /**
     * Set the value range
     */
    public void setRange(float min, float max) {
        this.minValue = min;
        this.maxValue = max;
        setValue(currentValue); // Clamp current value to new range
    }
    
    /**
     * Set the value change callback
     */
    public void setOnValueChanged(Consumer<Float> callback) {
        this.onValueChanged = callback;
    }
    
    /**
     * Set the handle color
     */
    public void setHandleColor(Vector3f color) {
        this.handleColor = color;
    }
    
    /**
     * Set the track color
     */
    public void setTrackColor(Vector3f color) {
        this.trackColor = color;
    }
    
    /**
     * Set the fill color
     */
    public void setFillColor(Vector3f color) {
        this.fillColor = color;
    }
    
    /**
     * Check if slider is being dragged
     */
    public boolean isDragging() {
        return isDragging;
    }
    
    /**
     * Check if slider is vertical
     */
    public boolean isVertical() {
        return vertical;
    }
} 