package com.codebyriley.Core.Rendering.UI;

import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Util.Math.Vector3f;
import java.util.function.Consumer;

/**
 * A text field UI element for text input.
 */
public class TextField extends UIElement {
    private String text;
    private String placeholder;
    private TextRenderer textRenderer;
    private Vector3f textColor;
    private Vector3f placeholderColor;
    private float textScale;
    private int cursorPosition;
    private boolean focused;
    private boolean showCursor;
    private float cursorBlinkTime;
    private float cursorBlinkRate;
    private int selectionStart;
    private int selectionEnd;
    private Consumer<String> onTextChanged;
    private Consumer<String> onEnterPressed;
    private int maxLength;
    private boolean passwordMode;
    
    public TextField(float x, float y, float width, float height, String initialText, TextRenderer textRenderer) {
        super(x, y, width, height);
        this.text = initialText != null ? initialText : "";
        this.placeholder = "";
        this.textRenderer = textRenderer;
        this.textColor = new Vector3f(1.0f, 1.0f, 1.0f);
        this.placeholderColor = new Vector3f(0.6f, 0.6f, 0.6f);
        this.textScale = 0.8f;
        this.cursorPosition = this.text.length();
        this.focused = false;
        this.showCursor = false;
        this.cursorBlinkTime = 0.0f;
        this.cursorBlinkRate = 0.5f;
        this.selectionStart = -1;
        this.selectionEnd = -1;
        this.maxLength = 100;
        this.passwordMode = false;
        
        // Set default colors
        setBackgroundColor(new Vector3f(0.1f, 0.1f, 0.1f));
        setBorderColor(new Vector3f(0.5f, 0.5f, 0.5f));
        setBorderThickness(2.0f);
    }
    
    @Override
    public void update() {
        if (focused) {
            cursorBlinkTime += 0.016f; // Assuming 60 FPS
            if (cursorBlinkTime >= cursorBlinkRate) {
                cursorBlinkTime = 0.0f;
                showCursor = !showCursor;
            }
        } else {
            showCursor = false;
        }
    }
    
    @Override
    public void render(UIRenderer renderer) {
        if (!visible) return;
        
        // Draw background with focus state
        float centerX = x + width / 2.0f;
        float centerY = y + height / 2.0f;
        Vector3f bgColor = focused ? new Vector3f(0.15f, 0.15f, 0.15f) : backgroundColor;
        renderer.addQuad(centerX, centerY, width, height,
            0.0f, 0.0f, 1.0f, 1.0f,
            bgColor.x, bgColor.y, bgColor.z, backgroundAlpha, 0);
        // Draw border with focus state
        Vector3f borderCol = focused ? new Vector3f(0.8f, 0.8f, 0.8f) : borderColor;
        if (borderAlpha > 0 && borderThickness > 0) {
            renderer.addQuad(centerX, y + height - borderThickness / 2.0f, width, borderThickness,
                0.0f, 0.0f, 1.0f, 1.0f,
                borderCol.x, borderCol.y, borderCol.z, borderAlpha, 0);
            renderer.addQuad(centerX, y + borderThickness / 2.0f, width, borderThickness,
                0.0f, 0.0f, 1.0f, 1.0f,
                borderCol.x, borderCol.y, borderCol.z, borderAlpha, 0);
            renderer.addQuad(x + borderThickness / 2.0f, centerY, borderThickness, height - 2 * borderThickness,
                0.0f, 0.0f, 1.0f, 1.0f,
                borderCol.x, borderCol.y, borderCol.z, borderAlpha, 0);
            renderer.addQuad(x + width - borderThickness / 2.0f, centerY, borderThickness, height - 2 * borderThickness,
                0.0f, 0.0f, 1.0f, 1.0f,
                borderCol.x, borderCol.y, borderCol.z, borderAlpha, 0);
        }
        
        // Draw text
        if (textRenderer != null) {
            String displayText = text.isEmpty() ? placeholder : (passwordMode ? "*".repeat(text.length()) : text);
            Vector3f color = text.isEmpty() ? placeholderColor : textColor;
            
            float textX = x + 5.0f;
            float textY = y + height / 2.0f;
            
            if (!text.isEmpty()) {
                textRenderer.drawText(displayText, textX, textY, color, 1.0f, textScale);
            } else if (!placeholder.isEmpty()) {
                textRenderer.drawText(displayText, textX, textY, color, 1.0f, textScale);
            }
            
            // Draw cursor
            if (focused && showCursor) {
                float cursorX = textX;
                if (!text.isEmpty()) {
                    String beforeCursor = passwordMode ? "*".repeat(cursorPosition) : text.substring(0, cursorPosition);
                    cursorX += textRenderer.getTextSize(beforeCursor, textScale).x;
                }
                
                renderer.addQuad(cursorX, textY - 5.0f, 2.0f, 10.0f,
                    0.0f, 0.0f, 1.0f, 1.0f,
                    textColor.x, textColor.y, textColor.z, 1.0f, 0);
            }
        }
    }
    
    @Override
    protected boolean handleMouseClick(float mouseX, float mouseY, int button) {
        if (button == 0) { // Left mouse button
            setFocused(true);
            // TODO: Set cursor position based on mouse click
            return true;
        }
        return false;
    }
    
    /**
     * Handle key press
     */
    public boolean onKeyPress(int key, int mods) {
        if (!focused) return false;
        
        // Handle special keys
        switch (key) {
            case 256: // GLFW_KEY_ESCAPE
                setFocused(false);
                return true;
            case 257: // GLFW_KEY_ENTER
                if (onEnterPressed != null) {
                    onEnterPressed.accept(text);
                }
                return true;
            case 259: // GLFW_KEY_BACKSPACE
                if (cursorPosition > 0) {
                    text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                    cursorPosition--;
                    notifyTextChanged();
                }
                return true;
            case 261: // GLFW_KEY_DELETE
                if (cursorPosition < text.length()) {
                    text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                    notifyTextChanged();
                }
                return true;
            case 262: // GLFW_KEY_RIGHT
                if (cursorPosition < text.length()) {
                    cursorPosition++;
                }
                return true;
            case 263: // GLFW_KEY_LEFT
                if (cursorPosition > 0) {
                    cursorPosition--;
                }
                return true;
            case 265: // GLFW_KEY_UP
                // TODO: Handle multi-line text
                return true;
            case 264: // GLFW_KEY_DOWN
                // TODO: Handle multi-line text
                return true;
        }
        
        return false;
    }
    
    /**
     * Handle character input
     */
    public boolean onCharInput(char character) {
        if (!focused) return false;
        
        // Only accept printable characters
        if (character >= 32 && character <= 126 && text.length() < maxLength) {
            text = text.substring(0, cursorPosition) + character + text.substring(cursorPosition);
            cursorPosition++;
            notifyTextChanged();
            return true;
        }
        
        return false;
    }
    
    /**
     * Set the text content
     */
    public void setText(String text) {
        this.text = text != null ? text : "";
        this.cursorPosition = Math.min(this.cursorPosition, this.text.length());
        notifyTextChanged();
    }
    
    /**
     * Get the text content
     */
    public String getText() {
        return text;
    }
    
    /**
     * Set the placeholder text
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder != null ? placeholder : "";
    }
    
    /**
     * Get the placeholder text
     */
    public String getPlaceholder() {
        return placeholder;
    }
    
    /**
     * Set the focused state
     */
    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            cursorBlinkTime = 0.0f;
            showCursor = true;
        }
    }
    
    /**
     * Check if the text field is focused
     */
    public boolean isFocused() {
        return focused;
    }
    
    /**
     * Set the text change callback
     */
    public void setOnTextChanged(Consumer<String> callback) {
        this.onTextChanged = callback;
    }
    
    /**
     * Set the enter key callback
     */
    public void setOnEnterPressed(Consumer<String> callback) {
        this.onEnterPressed = callback;
    }
    
    /**
     * Set the maximum text length
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (text.length() > maxLength) {
            text = text.substring(0, maxLength);
            cursorPosition = Math.min(cursorPosition, text.length());
            notifyTextChanged();
        }
    }
    
    /**
     * Set password mode (masks text with asterisks)
     */
    public void setPasswordMode(boolean passwordMode) {
        this.passwordMode = passwordMode;
    }
    
    /**
     * Check if password mode is enabled
     */
    public boolean isPasswordMode() {
        return passwordMode;
    }
    
    /**
     * Set the cursor blink rate
     */
    public void setCursorBlinkRate(float rate) {
        this.cursorBlinkRate = rate;
    }
    
    /**
     * Notify text change callback
     */
    private void notifyTextChanged() {
        if (onTextChanged != null) {
            onTextChanged.accept(text);
        }
    }
} 