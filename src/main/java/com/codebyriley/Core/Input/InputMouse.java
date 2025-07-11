package com.codebyriley.Core.Input;

import org.lwjgl.glfw.GLFW;
import com.codebyriley.Core.Rendering.WindowBase;
import com.codebyriley.Util.Log;
import com.codebyriley.Util.Math.Vector2f;
import java.util.HashMap;
import java.util.Map;

public class InputMouse {
    private static Map<Integer, Boolean> prevButtonStates = new HashMap<>();
    private static Map<Integer, Boolean> currButtonStates = new HashMap<>();
    private static double mouseX = 0.0;
    private static double mouseY = 0.0;
    private static double prevMouseX = 0.0;
    private static double prevMouseY = 0.0;
    private static double scrollX = 0.0;
    private static double scrollY = 0.0;
    private static boolean mouseInWindow = true;
    
    // Mouse button constants
    public static final int MOUSE_BUTTON_LEFT = GLFW.GLFW_MOUSE_BUTTON_LEFT;
    public static final int MOUSE_BUTTON_RIGHT = GLFW.GLFW_MOUSE_BUTTON_RIGHT;
    public static final int MOUSE_BUTTON_MIDDLE = GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
    public static final int MOUSE_BUTTON_4 = GLFW.GLFW_MOUSE_BUTTON_4;
    public static final int MOUSE_BUTTON_5 = GLFW.GLFW_MOUSE_BUTTON_5;
    public static final int MOUSE_BUTTON_6 = GLFW.GLFW_MOUSE_BUTTON_6;
    public static final int MOUSE_BUTTON_7 = GLFW.GLFW_MOUSE_BUTTON_7;
    public static final int MOUSE_BUTTON_8 = GLFW.GLFW_MOUSE_BUTTON_8;
    
    /**
     * Check if a mouse button is currently pressed
     */
    public static boolean IsButtonPressed(int button) {
        boolean pressed = GLFW.glfwGetMouseButton(WindowBase.windowHandle, button) == GLFW.GLFW_PRESS;
        currButtonStates.put(button, pressed);
        return pressed;
    }
    
    /**
     * Check if a mouse button is currently released
     */
    public static boolean IsButtonUp(int button) {
        boolean up = GLFW.glfwGetMouseButton(WindowBase.windowHandle, button) == GLFW.GLFW_RELEASE;
        currButtonStates.put(button, !up);
        return up;
    }
    
    /**
     * Check if a mouse button was just pressed this frame
     */
    public static boolean IsButtonJustPressed(int button) {
        boolean prev = prevButtonStates.getOrDefault(button, false);
        boolean curr = currButtonStates.getOrDefault(button, false);
        return !prev && curr;
    }
    
    /**
     * Check if a mouse button was just released this frame
     */
    public static boolean IsButtonJustReleased(int button) {
        boolean prev = prevButtonStates.getOrDefault(button, false);
        boolean curr = currButtonStates.getOrDefault(button, false);
        return prev && !curr;
    }
    
    /**
     * Get the current mouse X position
     */
    public static double GetMouseX() {
        return mouseX;
    }
    
    /**
     * Get the current mouse Y position
     */
    public static double GetMouseY() {
        return mouseY;
    }
    
    /**
     * Get the current mouse position as a Vector2f
     */
    public static Vector2f GetMousePosition() {
        return new Vector2f((float)mouseX, (float)mouseY);
    }
    
    /**
     * Get the previous mouse X position
     */
    public static double GetPrevMouseX() {
        return prevMouseX;
    }
    
    /**
     * Get the previous mouse Y position
     */
    public static double GetPrevMouseY() {
        return prevMouseY;
    }
    
    /**
     * Get the previous mouse position as a Vector2f
     */
    public static Vector2f GetPrevMousePosition() {
        return new Vector2f((float)prevMouseX, (float)prevMouseY);
    }
    
    /**
     * Get the mouse delta movement (current - previous)
     */
    public static Vector2f GetMouseDelta() {
        return new Vector2f((float)(mouseX - prevMouseX), (float)(mouseY - prevMouseY));
    }
    
    /**
     * Get the horizontal scroll value
     */
    public static double GetScrollX() {
        return scrollX;
    }
    
    /**
     * Get the vertical scroll value
     */
    public static double GetScrollY() {
        return scrollY;
    }
    
    /**
     * Get the scroll values as a Vector2f
     */
    public static Vector2f GetScroll() {
        return new Vector2f((float)scrollX, (float)scrollY);
    }
    
    /**
     * Check if the mouse is inside the window
     */
    public static boolean IsMouseInWindow() {
        return mouseInWindow;
    }
    
    /**
     * Set the mouse cursor position
     */
    public static void SetMousePosition(double x, double y) {
        GLFW.glfwSetCursorPos(WindowBase.windowHandle, x, y);
        mouseX = x;
        mouseY = y;
    }
    
    /**
     * Set the mouse cursor position using Vector2f
     */
    public static void SetMousePosition(Vector2f position) {
        SetMousePosition(position.x, position.y);
    }
    
    /**
     * Center the mouse cursor in the window
     */
    public static void CenterMouse() {
        SetMousePosition(WindowBase.windowWidth / 2.0, WindowBase.windowHeight / 2.0);
    }
    
    /**
     * Show the mouse cursor
     */
    public static void ShowCursor() {
        GLFW.glfwSetInputMode(WindowBase.windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
    }
    
    /**
     * Hide the mouse cursor
     */
    public static void HideCursor() {
        GLFW.glfwSetInputMode(WindowBase.windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
    }
    
    /**
     * Disable the mouse cursor (for FPS-style games)
     */
    public static void DisableCursor() {
        GLFW.glfwSetInputMode(WindowBase.windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }
    
    /**
     * Update mouse state - call this at the beginning of each frame
     */
    public static void update() {
        // Store previous position
        prevMouseX = mouseX;
        prevMouseY = mouseY;
        // Get current position
        double[] xpos = new double[1];
        double[] ypos = new double[1];
        GLFW.glfwGetCursorPos(WindowBase.windowHandle, xpos, ypos);
        mouseX = xpos[0];
        mouseY = ypos[0];
        // Update previous button states to current button states for all tracked buttons
        prevButtonStates.clear();
        prevButtonStates.putAll(currButtonStates);
        // Poll all possible mouse buttons and update current state
        for (int button = GLFW.GLFW_MOUSE_BUTTON_LEFT; button <= GLFW.GLFW_MOUSE_BUTTON_8; button++) {
            boolean pressed = GLFW.glfwGetMouseButton(WindowBase.windowHandle, button) == GLFW.GLFW_PRESS;
            currButtonStates.put(button, pressed);
        }
        // Reset scroll values after processing
        resetScroll();
    }
    
    /**
     * Set scroll values (called by GLFW callback)
     */
    public static void setScroll(double xoffset, double yoffset) {
        scrollX = xoffset;
        scrollY = yoffset;
    }
    
    /**
     * Set mouse enter/leave state (called by GLFW callback)
     */
    public static void setMouseInWindow(boolean entered) {
        mouseInWindow = entered;
    }
    
    /**
     * Reset scroll values (call this after processing scroll input)
     */
    public static void resetScroll() {
        scrollX = 0.0;
        scrollY = 0.0;
    }
    
    /**
     * Clear all button states (useful when switching scenes or resetting input)
     */
    public static void clearStates() {
        prevButtonStates.clear();
    }
    
    /**
     * Check if any mouse button is currently pressed
     */
    public static boolean IsAnyButtonPressed() {
        return IsButtonPressed(MOUSE_BUTTON_LEFT) || 
               IsButtonPressed(MOUSE_BUTTON_RIGHT) || 
               IsButtonPressed(MOUSE_BUTTON_MIDDLE);
    }
    
    /**
     * Get the distance the mouse has moved since last frame
     */
    public static double GetMouseDistance() {
        double dx = mouseX - prevMouseX;
        double dy = mouseY - prevMouseY;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Check if the mouse has moved since last frame
     */
    public static boolean HasMouseMoved() {
        return mouseX != prevMouseX || mouseY != prevMouseY;
    }
}
