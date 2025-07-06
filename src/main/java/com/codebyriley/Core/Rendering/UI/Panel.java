package com.codebyriley.Core.Rendering.UI;

import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Util.Math.Vector2f;
import com.codebyriley.Util.Math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel UI element that can contain other UI elements.
 * Provides layout and grouping functionality.
 */
public class Panel extends UIElement {
    private List<UIElement> children;
    private boolean clipChildren;
    private LayoutType layoutType;
    private float padding;
    private float spacing;
    
    public enum LayoutType {
        NONE,           // No automatic layout
        VERTICAL,       // Stack children vertically
        HORIZONTAL,     // Stack children horizontally
        GRID           // Arrange children in a grid
    }
    
    public Panel(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.children = new ArrayList<>();
        this.clipChildren = true;
        this.layoutType = LayoutType.NONE;
        this.padding = 5.0f;
        this.spacing = 5.0f;
        
        // Set default panel colors
        setBackgroundColor(new Vector3f(0.1f, 0.1f, 0.1f));
        setBorderColor(new Vector3f(0.5f, 0.5f, 0.5f));
    }
    
    @Override
    public void update() {
        // Update all children
        for (UIElement child : children) {
            if (child.isVisible() && child.isEnabled()) {
                child.update();
            }
        }
        
        // Update layout if needed
        if (layoutType != LayoutType.NONE) {
            updateLayout();
        }
    }
    
    @Override
    public void render(UIRenderer renderer) {
        if (!visible) return;
        
        // Draw panel background and border
        drawBackground(renderer);
        
        // Render children
        for (UIElement child : children) {
            if (child.isVisible()) {
                // Apply clipping if enabled
                if (clipChildren) {
                    // TODO: Implement clipping (requires scissor test)
                    // For now, just render normally
                }
                
                child.render(renderer);
            }
        }
    }
    
    /**
     * Add a child element to this panel
     */
    public void addChild(UIElement child) {
        children.add(child);
        if (layoutType != LayoutType.NONE) {
            updateLayout();
        }
    }
    
    /**
     * Remove a child element from this panel
     */
    public void removeChild(UIElement child) {
        children.remove(child);
        if (layoutType != LayoutType.NONE) {
            updateLayout();
        }
    }
    
    /**
     * Get all child elements
     */
    public List<UIElement> getChildren() {
        return new ArrayList<>(children);
    }
    
    /**
     * Clear all child elements
     */
    public void clearChildren() {
        children.clear();
    }
    
    /**
     * Update the layout of child elements
     */
    private void updateLayout() {
        if (children.isEmpty()) return;
        
        float contentX = x + padding;
        float contentY = y + padding;
        float contentWidth = width - 2 * padding;
        float contentHeight = height - 2 * padding;
        
        switch (layoutType) {
            case VERTICAL:
                layoutVertical(contentX, contentY, contentWidth, contentHeight);
                break;
            case HORIZONTAL:
                layoutHorizontal(contentX, contentY, contentWidth, contentHeight);
                break;
            case GRID:
                layoutGrid(contentX, contentY, contentWidth, contentHeight);
                break;
            default:
                break;
        }
    }
    
    /**
     * Layout children vertically
     */
    private void layoutVertical(float startX, float startY, float maxWidth, float maxHeight) {
        float currentY = startY;
        
        for (UIElement child : children) {
            if (!child.isVisible()) continue;
            
            // Position child
            child.setX(startX);
            child.setY(currentY);
            
            // Adjust child width if it exceeds panel width
            if (child.getWidth() > maxWidth) {
                child.setWidth(maxWidth);
            }
            
            // Move to next position
            currentY += child.getHeight() + spacing;
            
            // Check if we've exceeded the panel height
            if (currentY > startY + maxHeight) {
                break;
            }
        }
    }
    
    /**
     * Layout children horizontally
     */
    private void layoutHorizontal(float startX, float startY, float maxWidth, float maxHeight) {
        float currentX = startX;
        
        for (UIElement child : children) {
            if (!child.isVisible()) continue;
            
            // Position child
            child.setX(currentX);
            child.setY(startY);
            
            // Adjust child height if it exceeds panel height
            if (child.getHeight() > maxHeight) {
                child.setHeight(maxHeight);
            }
            
            // Move to next position
            currentX += child.getWidth() + spacing;
            
            // Check if we've exceeded the panel width
            if (currentX > startX + maxWidth) {
                break;
            }
        }
    }
    
    /**
     * Layout children in a grid
     */
    private void layoutGrid(float startX, float startY, float maxWidth, float maxHeight) {
        // Calculate grid dimensions
        int numChildren = 0;
        for (UIElement child : children) {
            if (child.isVisible()) numChildren++;
        }
        
        if (numChildren == 0) return;
        
        int cols = (int) Math.ceil(Math.sqrt(numChildren));
        int rows = (int) Math.ceil((double) numChildren / cols);
        
        float cellWidth = (maxWidth - (cols - 1) * spacing) / cols;
        float cellHeight = (maxHeight - (rows - 1) * spacing) / rows;
        
        int childIndex = 0;
        for (UIElement child : children) {
            if (!child.isVisible()) continue;
            
            int row = childIndex / cols;
            int col = childIndex % cols;
            
            float childX = startX + col * (cellWidth + spacing);
            float childY = startY + row * (cellHeight + spacing);
            
            child.setX(childX);
            child.setY(childY);
            child.setWidth(cellWidth);
            child.setHeight(cellHeight);
            
            childIndex++;
        }
    }
    
    /**
     * Set the layout type
     */
    public void setLayoutType(LayoutType layoutType) {
        this.layoutType = layoutType;
        if (layoutType != LayoutType.NONE) {
            updateLayout();
        }
    }
    
    /**
     * Get the layout type
     */
    public LayoutType getLayoutType() {
        return layoutType;
    }
    
    /**
     * Set the padding around child elements
     */
    public void setPadding(float padding) {
        this.padding = padding;
        if (layoutType != LayoutType.NONE) {
            updateLayout();
        }
    }
    
    /**
     * Get the padding
     */
    public float getPadding() {
        return padding;
    }
    
    /**
     * Set the spacing between child elements
     */
    public void setSpacing(float spacing) {
        this.spacing = spacing;
        if (layoutType != LayoutType.NONE) {
            updateLayout();
        }
    }
    
    /**
     * Get the spacing
     */
    public float getSpacing() {
        return spacing;
    }
    
    /**
     * Set whether to clip children to panel bounds
     */
    public void setClipChildren(boolean clipChildren) {
        this.clipChildren = clipChildren;
    }
    
    /**
     * Check if children are clipped
     */
    public boolean isClipChildren() {
        return clipChildren;
    }
    
    /**
     * Find a child element by ID
     */
    public UIElement findChild(String id) {
        for (UIElement child : children) {
            if (id.equals(child.getId())) {
                return child;
            }
        }
        return null;
    }
    
    /**
     * Find all child elements of a specific type
     */
    public <T extends UIElement> List<T> findChildrenOfType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (UIElement child : children) {
            if (type.isInstance(child)) {
                result.add(type.cast(child));
            }
        }
        return result;
    }
} 