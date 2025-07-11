package com.codebyriley.Core.Input;

import org.lwjgl.glfw.GLFW;

import com.codebyriley.Core.Rendering.WindowBase;
import java.util.HashMap;
import java.util.Map;

public class InputKeyboard {
    private static Map<Integer, Boolean> prevKeyStates = new HashMap<>();
    private static Map<Integer, Boolean> currKeyStates = new HashMap<>();

    public static boolean IsKeyPressed(int key) {
        boolean pressed = GLFW.glfwGetKey(WindowBase.windowHandle, key) == GLFW.GLFW_PRESS;
        currKeyStates.put(key, pressed);
        return pressed;
    }
    public static boolean IsKeyUp(int key) {
        boolean up = GLFW.glfwGetKey(WindowBase.windowHandle, key) == GLFW.GLFW_RELEASE;
        currKeyStates.put(key, !up);
        return up;
    }
    public static boolean IsKeyJustReleased(int key) {
        boolean prev = prevKeyStates.getOrDefault(key, false);
        boolean curr = currKeyStates.getOrDefault(key, false);
        return prev && !curr;
    }

    public static boolean IsKeyJustPressed(int key) {
        boolean prev = prevKeyStates.getOrDefault(key, false);
        boolean curr = currKeyStates.getOrDefault(key, false);
        return !prev && curr;
    }

    public static void update() {
        // Update previous key states to current key states for all tracked keys
        prevKeyStates.clear();
        prevKeyStates.putAll(currKeyStates);
        // Poll all possible keys and update current state
        for (int key = GLFW.GLFW_KEY_SPACE; key <= GLFW.GLFW_KEY_LAST; key++) {
            boolean pressed = GLFW.glfwGetKey(WindowBase.windowHandle, key) == GLFW.GLFW_PRESS;
            currKeyStates.put(key, pressed);
        }
    }
}
