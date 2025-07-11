package com.codebyriley.Core.Rendering;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

public class WindowBase {
    
    public static long windowHandle;
    public static int windowWidth;
    public static int windowHeight;

    private void CreateHints() {
        glfwDefaultWindowHints();
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        glfwWindowHint(GLFW.GLFW_CENTER_CURSOR, GLFW.GLFW_TRUE);
    }

    public void Destroy() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
    }

    public WindowBase(String title, int width, int height) {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        windowWidth = width;
        windowHeight = height;
        CreateHints();
        windowHandle = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);
        if(windowHandle == NULL) {
            throw new RuntimeException("Failed to create window");
        }


        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowHandle, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                windowHandle, 
                (vidmode.width() - pWidth.get(0)) / 2
                , (vidmode.height() - pHeight.get(0)) / 2
            );

            glfwMakeContextCurrent(windowHandle);

            glfwSwapInterval(0);
            
            // Set up mouse callbacks
            setupMouseCallbacks();
            
            glfwShowWindow(windowHandle);
        }
    }
    
    /**
     * Set up GLFW mouse callbacks for InputMouse integration
     */
    private void setupMouseCallbacks() {
        // Cursor position callback
        glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> {
            // This callback is optional since InputMouse.update() polls the position
            // But it can be useful for immediate position updates
        });
        
        // Scroll callback
        glfwSetScrollCallback(windowHandle, (window, xoffset, yoffset) -> {
            com.codebyriley.Core.Input.InputMouse.setScroll(xoffset, yoffset);
        });
        
        // Cursor enter/leave callback
        glfwSetCursorEnterCallback(windowHandle, (window, entered) -> {
            com.codebyriley.Core.Input.InputMouse.setMouseInWindow(entered);
        });
    }
}
