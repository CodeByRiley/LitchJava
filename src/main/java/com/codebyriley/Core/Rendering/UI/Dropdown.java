package com.codebyriley.Core.Rendering.UI;

import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Util.Math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A dropdown UI element for selection from a list of options.
 */
public class Dropdown extends UIElement {
    private List<String> options;
    private int selectedIndex;
    private boolean expanded;
    private TextRenderer textRenderer;
    private Vector3f textColor;
    private Vector3f arrowColor;
    private float textScale;
    private float maxDropdownHeight;
    private Consumer<Integer> onSelectionChanged;
    private Consumer<String> onSelectionChangedText;
    private Vector3f dropdownBgColor;
    private Vector3f dropdownHoverColor;
    private Vector3f dropdownSelectedColor;
    
    public Dropdown(float x, float y, float width, float height, List<String> options, TextRenderer textRenderer) {
        super(x, y, width, height);
        this.options = new ArrayList<>(options);
        this.selectedIndex = -1;
        this.expanded = false;
        this.textRenderer = textRenderer;
        this.textColor = new Vector3f(1.0f, 1.0f, 1.0f);
        this.arrowColor = new Vector3f(0.8f, 0.8f, 0.8f);
        this.textScale = 0.8f;
        this.maxDropdownHeight = 200.0f;
        this.dropdownBgColor = new Vector3f(0.2f, 0.2f, 0.2f);
        this.dropdownHoverColor = new Vector3f(0.3f, 0.3f, 0.3f);
        this.dropdownSelectedColor = new Vector3f(0.4f, 0.4f, 0.6f);
        
        // Set default colors
        setBackgroundColor(new Vector3f(0.1f, 0.1f, 0.1f));
        setBorderColor(new Vector3f(0.5f, 0.5f, 0.5f));
        setBorderThickness(2.0f);
    }
    
    @Override
    public void update() {
        // Dropdown-specific update logic can go here
    }
    
    @Override
    public void render(UIRenderer renderer) {
        if (!visible) return;
        
        // Draw main button background
        float centerX = x + width / 2.0f;
        float centerY = y + height / 2.0f;
        Vector3f bgColor = expanded ? new Vector3f(0.15f, 0.15f, 0.15f) : backgroundColor;
        renderer.addQuad(centerX, centerY, width, height,
            0.0f, 0.0f, 1.0f, 1.0f,
            bgColor.x, bgColor.y, bgColor.z, backgroundAlpha, 0);
        
        // Draw border
        drawBackground(renderer);
        
        // Draw selected text
        if (textRenderer != null && selectedIndex >= 0 && selectedIndex < options.size()) {
            String selectedText = options.get(selectedIndex);
            float textX = x + 10.0f;
            float textY = y + height / 2.0f;
            textRenderer.drawText(selectedText, textX, textY, textColor, 1.0f, textScale);
        }
        
        // Draw arrow
        drawArrow(renderer);
        
        // Draw dropdown menu if expanded
        if (expanded) {
            drawDropdownMenu(renderer);
        }
    }
    
    /**
     * Draw the dropdown arrow
     */
    private void drawArrow(UIRenderer renderer) {
        float arrowSize = 8.0f;
        float arrowX = x + width - 20.0f;
        float arrowY = y + height / 2.0f - arrowSize / 2.0f;
        
        if (expanded) {
            // Draw up arrow
            renderer.addQuad(arrowX, arrowY + arrowSize/2, arrowSize, 2.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                arrowColor.x, arrowColor.y, arrowColor.z, 1.0f, 0);
            renderer.addQuad(arrowX + arrowSize/2, arrowY, 2.0f, arrowSize/2,
                0.0f, 0.0f, 1.0f, 1.0f,
                arrowColor.x, arrowColor.y, arrowColor.z, 1.0f, 0);
        } else {
            // Draw down arrow
            renderer.addQuad(arrowX, arrowY, arrowSize, 2.0f,
                0.0f, 0.0f, 1.0f, 1.0f,
                arrowColor.x, arrowColor.y, arrowColor.z, 1.0f, 0);
            renderer.addQuad(arrowX + arrowSize/2, arrowY, 2.0f, arrowSize/2,
                0.0f, 0.0f, 1.0f, 1.0f,
                arrowColor.x, arrowColor.y, arrowColor.z, 1.0f, 0);
        }
    }
    
    /**
     * Draw the dropdown menu
     */
    private void drawDropdownMenu(UIRenderer renderer) {
        if (options.isEmpty()) return;
        
        float menuY = y + height;
        float itemHeight = 25.0f;
        float menuHeight = Math.min(options.size() * itemHeight, maxDropdownHeight);
        
        // Draw menu background
        renderer.addQuad(x, menuY, width, menuHeight,
            0.0f, 0.0f, 1.0f, 1.0f,
            dropdownBgColor.x, dropdownBgColor.y, dropdownBgColor.z, 1.0f, 0);
        
        // Draw menu border
        renderer.addQuad(x, menuY, width, 2.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            borderColor.x, borderColor.y, borderColor.z, 1.0f, 0);
        
        // Draw menu items
        if (textRenderer != null) {
            for (int i = 0; i < options.size() && i * itemHeight < maxDropdownHeight; i++) {
                float itemY = menuY + i * itemHeight;
                
                // Highlight selected item
                if (i == selectedIndex) {
                    renderer.addQuad(x, itemY, width, itemHeight,
                        0.0f, 0.0f, 1.0f, 1.0f,
                        dropdownSelectedColor.x, dropdownSelectedColor.y, dropdownSelectedColor.z, 1.0f, 0);
                }
                
                // Draw item text
                String option = options.get(i);
                float textX = x + 10.0f;
                float textY = itemY + itemHeight / 2.0f;
                textRenderer.drawText(option, textX, textY, textColor, 1.0f, textScale);
            }
        }
    }
    
    @Override
    protected boolean handleMouseClick(float mouseX, float mouseY, int button) {
        if (button == 0) { // Left mouse button
            if (expanded) {
                // Check if clicking on a menu item
                float menuY = y + height;
                float itemHeight = 25.0f;
                
                if (mouseY >= menuY && mouseY < menuY + Math.min(options.size() * itemHeight, maxDropdownHeight)) {
                    int itemIndex = (int)((mouseY - menuY) / itemHeight);
                    if (itemIndex >= 0 && itemIndex < options.size()) {
                        setSelectedIndex(itemIndex);
                        setExpanded(false);
                        return true;
                    }
                }
                
                // Clicked outside menu, close it
                setExpanded(false);
            } else {
                // Open dropdown
                setExpanded(true);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Handle mouse hover over dropdown items
     */
    public boolean onMouseHover(float mouseX, float mouseY) {
        if (!expanded) return false;
        
        float menuY = y + height;
        float itemHeight = 25.0f;
        
        if (mouseY >= menuY && mouseY < menuY + Math.min(options.size() * itemHeight, maxDropdownHeight)) {
            // TODO: Highlight hovered item
            return true;
        }
        return false;
    }
    
    /**
     * Set the selected index
     */
    public void setSelectedIndex(int index) {
        if (index >= -1 && index < options.size() && index != selectedIndex) {
            selectedIndex = index;
            if (onSelectionChanged != null) {
                onSelectionChanged.accept(selectedIndex);
            }
            if (onSelectionChangedText != null && selectedIndex >= 0) {
                onSelectionChangedText.accept(options.get(selectedIndex));
            }
        }
    }
    
    /**
     * Get the selected index
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }
    
    /**
     * Get the selected text
     */
    public String getSelectedText() {
        if (selectedIndex >= 0 && selectedIndex < options.size()) {
            return options.get(selectedIndex);
        }
        return "";
    }
    
    /**
     * Set the expanded state
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    /**
     * Check if dropdown is expanded
     */
    public boolean isExpanded() {
        return expanded;
    }
    
    /**
     * Add an option to the dropdown
     */
    public void addOption(String option) {
        options.add(option);
    }
    
    /**
     * Remove an option from the dropdown
     */
    public void removeOption(String option) {
        options.remove(option);
        if (selectedIndex >= options.size()) {
            selectedIndex = options.size() - 1;
        }
    }
    
    /**
     * Clear all options
     */
    public void clearOptions() {
        options.clear();
        selectedIndex = -1;
    }
    
    /**
     * Set the options list
     */
    public void setOptions(List<String> options) {
        this.options = new ArrayList<>(options);
        if (selectedIndex >= options.size()) {
            selectedIndex = options.size() - 1;
        }
    }
    
    /**
     * Get all options
     */
    public List<String> getOptions() {
        return new ArrayList<>(options);
    }
    
    /**
     * Set the selection change callback (index)
     */
    public void setOnSelectionChanged(Consumer<Integer> callback) {
        this.onSelectionChanged = callback;
    }
    
    /**
     * Set the selection change callback (text)
     */
    public void setOnSelectionChangedText(Consumer<String> callback) {
        this.onSelectionChangedText = callback;
    }
    
    /**
     * Set the maximum dropdown height
     */
    public void setMaxDropdownHeight(float height) {
        this.maxDropdownHeight = height;
    }
    
    /**
     * Set the dropdown background color
     */
    public void setDropdownBgColor(Vector3f color) {
        this.dropdownBgColor = color;
    }
    
    /**
     * Set the dropdown hover color
     */
    public void setDropdownHoverColor(Vector3f color) {
        this.dropdownHoverColor = color;
    }
    
    /**
     * Set the dropdown selected color
     */
    public void setDropdownSelectedColor(Vector3f color) {
        this.dropdownSelectedColor = color;
    }
} 