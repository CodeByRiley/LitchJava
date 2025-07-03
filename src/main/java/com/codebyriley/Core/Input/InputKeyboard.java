package com.codebyriley.Core.Input;

import org.lwjgl.glfw.GLFW;

import com.codebyriley.Core.Rendering.WindowBase;
import java.util.HashMap;
import java.util.Map;

public class InputKeyboard {
    private static Map<Integer, Boolean> prevKeyStates = new HashMap<>();

    public static boolean IsKeyPressed(int key) {
        return GLFW.glfwGetKey(WindowBase.windowHandle, key) == GLFW.GLFW_PRESS;
    }
    public static boolean IsKeyUp(int key) {
        return GLFW.glfwGetKey(WindowBase.windowHandle, key) == GLFW.GLFW_RELEASE;
    }
    public static boolean IsKeyJustReleased(int key) {
        boolean prev = prevKeyStates.getOrDefault(key, false);
        boolean curr = GLFW.glfwGetKey(WindowBase.windowHandle, key) == GLFW.GLFW_RELEASE;
        prevKeyStates.put(key, curr);
        return prev && !curr;
    }

    public static boolean IsKeyJustPressed(int key) {
        boolean prev = prevKeyStates.getOrDefault(key, false);
        boolean curr = GLFW.glfwGetKey(WindowBase.windowHandle, key) == GLFW.GLFW_PRESS;
        prevKeyStates.put(key, curr);
        return !prev && curr;
    }

    public static void update() {
        // Optionally, update all keys you care about here
    }
}
