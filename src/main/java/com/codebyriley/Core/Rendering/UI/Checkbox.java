package com.codebyriley.Core.Rendering.UI;

import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Util.Math.Vector3f;
import java.util.function.Consumer;

/**
 * A checkbox UI element for boolean toggles.
 */
public class Checkbox extends UIElement {
    private boolean checked;
    private Vector3f checkColor;
    private float checkThickness;
    private Consumer<Boolean> onCheckedChanged;
    private String label;
    private float labelOffset;
    
    public Checkbox(float x, float y, float size, boolean initialValue) {
        super(x, y, size, size);
        this.checked = initialValue;
        this.checkColor = new Vector3f(0.2f, 0.2f, 0.2f);
        this.checkThickness = Math.max(2.0f, size * 0.1f);
        this.label = "";
        this.labelOffset = size + 10.0f;
        
        // Set default colors
        setBackgroundColor(new Vector3f(0.9f, 0.9f, 0.9f));
        setBorderColor(new Vector3f(0.3f, 0.3f, 0.3f));
        setBorderThickness(2.0f);
    }
    
    public Checkbox(float x, float y, float size, String label, boolean initialValue) {
        this(x, y, size, initialValue);
        this.label = label;
    }
    
    @Override
    public void update() {
        // Checkbox-specific update logic can go here
    }
    
    @Override
    public void render(UIRenderer renderer) {
        if (!visible) return;
        
        // Draw background
        Vector3f bgColor = checked ? new Vector3f(0.7f, 0.7f, 0.9f) : backgroundColor;
        float centerX = x + width / 2.0f;
        float centerY = y + height / 2.0f;
        renderer.addQuad(centerX, centerY, width, height,
            0.0f, 0.0f, 1.0f, 1.0f,
            bgColor.x, bgColor.y, bgColor.z, backgroundAlpha, 0);
        
        // Draw border
        if (borderAlpha > 0 && borderThickness > 0) {
            renderer.addQuad(centerX, y + height - borderThickness / 2.0f, width, borderThickness,
                0.0f, 0.0f, 1.0f, 1.0f,
                borderColor.x, borderColor.y, borderColor.z, borderAlpha, 0);
            renderer.addQuad(centerX, y + borderThickness / 2.0f, width, borderThickness,
                0.0f, 0.0f, 1.0f, 1.0f,
                borderColor.x, borderColor.y, borderColor.z, borderAlpha, 0);
            renderer.addQuad(x + borderThickness / 2.0f, centerY, borderThickness, height - 2 * borderThickness,
                0.0f, 0.0f, 1.0f, 1.0f,
                borderColor.x, borderColor.y, borderColor.z, borderAlpha, 0);
            renderer.addQuad(x + width - borderThickness / 2.0f, centerY, borderThickness, height - 2 * borderThickness,
                0.0f, 0.0f, 1.0f, 1.0f,
                borderColor.x, borderColor.y, borderColor.z, borderAlpha, 0);
        }
        
        // Draw check mark if checked
        if (checked) {
            drawCheckMark(renderer);
        }
    }
    
    /**
     * Draw the check mark
     */
    private void drawCheckMark(UIRenderer renderer) {
        float margin = width * 0.2f;
        float checkX = x + margin;
        float checkY = y + margin;
        float checkWidth = width - 2 * margin;
        float checkHeight = height - 2 * margin;
        
        // Draw a simple "X" check mark
        float thickness = checkThickness;
        
        // Top-left to bottom-right line
        renderer.addQuad(checkX, checkY + checkHeight/2 - thickness/2, checkWidth * 0.7f, thickness,
            0.0f, 0.0f, 1.0f, 1.0f,
            checkColor.x, checkColor.y, checkColor.z, 1.0f, 0);
        
        // Top-right to bottom-left line
        renderer.addQuad(checkX + checkWidth * 0.3f, checkY + checkHeight/2 - thickness/2, checkWidth * 0.7f, thickness,
            0.0f, 0.0f, 1.0f, 1.0f,
            checkColor.x, checkColor.y, checkColor.z, 1.0f, 0);
    }
    
    @Override
    protected boolean handleMouseClick(float mouseX, float mouseY, int button) {
        if (button == 0) { // Left mouse button
            toggle();
            return true;
        }
        return false;
    }
    
    /**
     * Toggle the checkbox state
     */
    public void toggle() {
        setChecked(!checked);
    }
    
    /**
     * Set the checked state
     */
    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            if (onCheckedChanged != null) {
                onCheckedChanged.accept(checked);
            }
        }
    }
    
    /**
     * Get the checked state
     */
    public boolean isChecked() {
        return checked;
    }
    
    /**
     * Set the check mark color
     */
    public void setCheckColor(Vector3f color) {
        this.checkColor = color;
    }
    
    /**
     * Set the check mark thickness
     */
    public void setCheckThickness(float thickness) {
        this.checkThickness = thickness;
    }
    
    /**
     * Set the checked state change callback
     */
    public void setOnCheckedChanged(Consumer<Boolean> callback) {
        this.onCheckedChanged = callback;
    }
    
    /**
     * Set the label text
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Get the label text
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Set the label offset
     */
    public void setLabelOffset(float offset) {
        this.labelOffset = offset;
    }
    
    /**
     * Get the label offset
     */
    public float getLabelOffset() {
        return labelOffset;
    }
} 