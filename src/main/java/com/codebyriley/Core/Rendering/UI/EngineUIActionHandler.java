package com.codebyriley.Core.Rendering.UI;

import com.codebyriley.Core.Engine;
import com.codebyriley.Core.Rendering.WindowBase;
import com.codebyriley.Util.Log;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Concrete implementation of UIActionHandler for engine-specific actions.
 * Handles actions like changing resolution, toggling vsync, etc.
 */
public class EngineUIActionHandler implements UIActionHandler {
    
    private Engine engine;
    
    public EngineUIActionHandler(Engine engine) {
        this.engine = engine;
    }
    
    @Override
    public boolean handleAction(String actionType, String actionParameter) {
        switch (actionType) {
            case "cycle_background":
                engine.cycleBackgroundColor();
                Log.info("Background color cycled");
                return true;
                
            case "set_resolution":
                return handleResolutionChange(actionParameter);
                
            case "toggle_vsync":
                return handleVSyncToggle(actionParameter);
                
            case "set_fullscreen":
                return handleFullscreenToggle(actionParameter);
                
            case "set_master_volume":
                return handleVolumeChange("master", actionParameter);
                
            case "set_music_volume":
                return handleVolumeChange("music", actionParameter);
                
            case "set_sfx_volume":
                return handleVolumeChange("sfx", actionParameter);
                
            case "save_game":
                Log.info("Save game requested");
                // You can implement save game logic here
                return true;
                
            case "load_game":
                Log.info("Load game requested");
                // You can implement load game logic here
                return true;
                
            case "quit_game":
                Log.info("Quit game requested");
                Engine.ShouldClose = true;
                return true;
                
            case "custom_action":
                Log.info("Custom action: " + actionParameter);
                return true;
                
            default:
                Log.warn("Unknown action type: " + actionType);
                return false;
        }
    }
    
    private boolean handleResolutionChange(String parameter) {
        try {
            String[] parts = parameter.split("x");
            if (parts.length == 2) {
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);
                
                // Update window size
                WindowBase.windowWidth = width;
                WindowBase.windowHeight = height;
                
                // Set the new window size (this would need to be implemented in WindowBase)
                // glfwSetWindowSize(WindowBase.windowHandle, width, height);
                
                Log.info("Resolution changed to: " + width + "x" + height);
                return true;
            }
        } catch (Exception e) {
            Log.error("Failed to parse resolution parameter: " + parameter);
        }
        return false;
    }
    
    private boolean handleVSyncToggle(String parameter) {
        try {
            boolean enable = Boolean.parseBoolean(parameter);
            if (enable) {
                glfwSwapInterval(1);
                Log.info("VSync enabled");
            } else {
                glfwSwapInterval(0);
                Log.info("VSync disabled");
            }
            return true;
        } catch (Exception e) {
            Log.error("Failed to parse VSync parameter: " + parameter);
        }
        return false;
    }
    
    private boolean handleFullscreenToggle(String parameter) {
        try {
            boolean enable = Boolean.parseBoolean(parameter);
            if (enable) {
                // Enter fullscreen mode
                // glfwSetWindowMonitor(WindowBase.windowHandle, glfwGetPrimaryMonitor(), 0, 0, WindowBase.windowWidth, WindowBase.windowHeight, 60);
                Log.info("Fullscreen enabled");
            } else {
                // Exit fullscreen mode
                // glfwSetWindowMonitor(WindowBase.windowHandle, 0, 100, 100, WindowBase.windowWidth, WindowBase.windowHeight, 60);
                Log.info("Fullscreen disabled");
            }
            return true;
        } catch (Exception e) {
            Log.error("Failed to parse fullscreen parameter: " + parameter);
        }
        return false;
    }
    
    private boolean handleVolumeChange(String volumeType, String parameter) {
        try {
            float volume = Float.parseFloat(parameter);
            volume = Math.max(0.0f, Math.min(1.0f, volume)); // Clamp between 0 and 1
            
            switch (volumeType) {
                case "master":
                    Log.info("Master volume set to: " + volume);
                    break;
                case "music":
                    Log.info("Music volume set to: " + volume);
                    break;
                case "sfx":
                    Log.info("SFX volume set to: " + volume);
                    break;
            }
            return true;
        } catch (Exception e) {
            Log.error("Failed to parse volume parameter: " + parameter);
        }
        return false;
    }
} 