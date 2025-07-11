package com.codebyriley.Core.Rendering.UI;

import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Util.Math.Vector2f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL43.*;

/**
 * Manages all UI elements, handles input events, and coordinates rendering.
 */
public class UIManager {
    private List<UIElement> elements;
    private Map<String, UIElement> elementsById;
    private UIRenderer renderer;
    private UIElement hoveredElement;
    private UIElement focusedElement;
    private boolean mousePressed;
    private float lastMouseX, lastMouseY;
    
    public UIManager(UIRenderer renderer) {
        this.elements = new ArrayList<>();
        this.elementsById = new HashMap<>();
        this.renderer = renderer;
        this.hoveredElement = null;
        this.focusedElement = null;
        this.mousePressed = false;
        this.lastMouseX = 0;
        this.lastMouseY = 0;
    }
    
    /**
     * Add a UI element to the manager
     */
    public void addElement(UIElement element) {
        elements.add(element);
        if (element.getId() != null && !element.getId().isEmpty()) {
            elementsById.put(element.getId(), element);
        }
    }
    
    /**
     * Remove a UI element from the manager
     */
    public void removeElement(UIElement element) {
        elements.remove(element);
        if (element.getId() != null && !element.getId().isEmpty()) {
            elementsById.remove(element.getId());
        }
    }
    
    /**
     * Get a UI element by its ID
     */
    public UIElement getElement(String id) {
        return elementsById.get(id);
    }
    
    /**
     * Get all UI elements
     */
    public List<UIElement> getElements() {
        return new ArrayList<>(elements);
    }
    
    /**
     * Clear all UI elements
     */
    public void clear() {
        elements.clear();
        elementsById.clear();
        hoveredElement = null;
        focusedElement = null;
    }
    
    /**
     * Update all UI elements
     */
    public void update() {
        for (UIElement element : elements) {
            if (element.isVisible() && element.isEnabled()) {
                element.update();
            }
        }
    }
    
    /**
     * Render all UI elements
     */
    public void render() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        
        renderer.begin();

        // Render elements in order (first added = rendered first = background)
        for (UIElement element : elements) {
            element.render(renderer);
            // if (element.isVisible()) {
            //     element.render(renderer);
            // }
        }
        
        renderer.end();

        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
    }
    
    /**
     * Handle mouse movement
     */
    public void onMouseMove(float mouseX, float mouseY) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        
        // Handle dragging for sliders (check both direct elements and panel children)
        if (mousePressed) {
            for (UIElement element : elements) {
                if (element instanceof Slider) {
                    Slider slider = (Slider) element;
                    if (slider.isDragging()) {
                        slider.onMouseDrag(mouseX, mouseY);
                        return;
                    }
                } else if (element instanceof Panel) {
                    Panel panel = (Panel) element;
                    panel.onMouseDrag(mouseX, mouseY);
                }
            }
        }
        
        // Find the topmost element under the mouse
        UIElement newHoveredElement = null;
        for (int i = elements.size() - 1; i >= 0; i--) {
            UIElement element = elements.get(i);
            if (element.isVisible() && element.isEnabled() && element.contains(mouseX, mouseY)) {
                newHoveredElement = element;
                break;
            }
        }
        
        // Update hover state
        if (hoveredElement != newHoveredElement) {
            if (hoveredElement != null) {
                // Exit hover for previous element
                if (hoveredElement instanceof Button) {
                    ((Button) hoveredElement).setHovered(false);
                }
            }
            
            hoveredElement = newHoveredElement;
            
            if (hoveredElement != null) {
                // Enter hover for new element
                if (hoveredElement instanceof Button) {
                    ((Button) hoveredElement).setHovered(true);
                }
                // Handle dropdown hover
                if (hoveredElement instanceof Dropdown) {
                    ((Dropdown) hoveredElement).onMouseHover(mouseX, mouseY);
                }
            }
        }
    }
    
    /**
     * Handle mouse button press
     */
    public boolean onMousePress(float mouseX, float mouseY, int button) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        mousePressed = true;
        
        // Find and click the topmost element under the mouse
        for (int i = elements.size() - 1; i >= 0; i--) {
            UIElement element = elements.get(i);
            if (element.isVisible() && element.isEnabled() && element.contains(mouseX, mouseY)) {
                boolean handled = element.onMouseClick(mouseX, mouseY, button);
                if (handled) {
                    focusedElement = element;
                    return true;
                }
            }
        }
        
        // No element was clicked, clear focus
        focusedElement = null;
        return false;
    }
    
    /**
     * Handle mouse button release
     */
    public void onMouseRelease(float mouseX, float mouseY, int button) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        mousePressed = false;
        
        // Release any pressed buttons and sliders (check both direct elements and panel children)
        for (UIElement element : elements) {
            if (element instanceof Button) {
                Button buttonElement = (Button) element;
                if (buttonElement.isPressed()) {
                    buttonElement.release();
                }
            } else if (element instanceof Slider) {
                Slider slider = (Slider) element;
                if (slider.isDragging()) {
                    slider.onMouseRelease();
                }
            } else if (element instanceof Panel) {
                Panel panel = (Panel) element;
                panel.onMouseRelease(mouseX, mouseY, button);
            }
        }
    }
    
    /**
     * Handle key press
     */
    public boolean onKeyPress(int key, int mods) {
        if (focusedElement != null && focusedElement.isEnabled()) {
            // Handle key events for focused element
            if (focusedElement instanceof TextField) {
                return ((TextField) focusedElement).onKeyPress(key, mods);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Handle key release
     */
    public void onKeyRelease(int key, int mods) {
        // Handle key release events
    }
    
    /**
     * Handle character input
     */
    public boolean onCharInput(char character) {
        if (focusedElement != null && focusedElement.isEnabled()) {
            if (focusedElement instanceof TextField) {
                return ((TextField) focusedElement).onCharInput(character);
            }
        }
        return false;
    }
    
    /**
     * Get the currently hovered element
     */
    public UIElement getHoveredElement() {
        return hoveredElement;
    }
    
    /**
     * Get the currently focused element
     */
    public UIElement getFocusedElement() {
        return focusedElement;
    }
    
    /**
     * Set the focused element
     */
    public void setFocusedElement(UIElement element) {
        focusedElement = element;
    }
    
    /**
     * Check if mouse is currently pressed
     */
    public boolean isMousePressed() {
        return mousePressed;
    }
    
    /**
     * Get the last mouse position
     */
    public Vector2f getLastMousePosition() {
        return new Vector2f(lastMouseX, lastMouseY);
    }
    
    /**
     * Find UI elements at a specific position
     */
    public List<UIElement> getElementsAt(float x, float y) {
        List<UIElement> result = new ArrayList<>();
        for (UIElement element : elements) {
            if (element.isVisible() && element.isEnabled() && element.contains(x, y)) {
                result.add(element);
            }
        }
        return result;
    }
    
    /**
     * Find UI elements of a specific type
     */
    public <T extends UIElement> List<T> getElementsOfType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (UIElement element : elements) {
            if (type.isInstance(element)) {
                result.add(type.cast(element));
            }
        }
        return result;
    }
} 