package com.codebyriley.Core.Config;

import com.codebyriley.Core.Rendering.UI.UIManager;
import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Util.Log;

/**
 * Demo class showing how to use the ConfigManager with UI integration.
 * This demonstrates the robust configuration system with modern design practices.
 */
public class ConfigDemo {

    /**
     * Setup various configuration types with validation and callbacks
     */
    private static void setupConfigurations(ConfigManager configManager) {
        // Graphics settings
        Config<Boolean> vsync = configManager.register(
            "graphics.vsync", 
            "Graphics", 
            "Enable Vertical Sync", 
            true, 
            Boolean.class,
            value -> true // Always valid
        );
        
        Config<Integer> fpsLimit = configManager.register(
            "graphics.fps_limit", 
            "Graphics", 
            "FPS Limit", 
            60, 
            Integer.class,
            value -> value >= 30 && value <= 300 // Valid range
        );
        
        Config<Float> brightness = configManager.register(
            "graphics.brightness", 
            "Graphics", 
            "Brightness", 
            1.0f, 
            Float.class,
            value -> value >= 0.0f && value <= 2.0f // Valid range
        );
        
        // Audio settings
        Config<Float> masterVolume = configManager.register(
            "audio.master_volume", 
            "Audio", 
            "Master Volume", 
            0.8f, 
            Float.class,
            value -> value >= 0.0f && value <= 1.0f
        );
        
        Config<Boolean> enableMusic = configManager.register(
            "audio.enable_music", 
            "Audio", 
            "Enable Music", 
            true, 
            Boolean.class
        );
        
        Config<String> audioDevice = configManager.register(
            "audio.device", 
            "Audio", 
            "Audio Device", 
            "Default", 
            String.class
        );
        
        // Game settings
        Config<Boolean> fullscreen = configManager.register(
            "game.fullscreen", 
            "Game", 
            "Fullscreen Mode", 
            false, 
            Boolean.class
        );
        
        Config<Integer> difficulty = configManager.register(
            "game.difficulty", 
            "Game", 
            "Difficulty Level", 
            2, 
            Integer.class,
            value -> value >= 1 && value <= 5
        );
        
        Config<String> playerName = configManager.register(
            "game.player_name", 
            "Game", 
            "Player Name", 
            "Player", 
            String.class,
            value -> value != null && !value.trim().isEmpty()
        );
        
        // Setup change callbacks for specific configs
        vsync.setOnChangeCallback(enabled -> {
            Log.info("VSync changed to: " + enabled);
            // Apply VSync setting to graphics system
        });
        
        fpsLimit.setOnChangeCallback(limit -> {
            Log.info("FPS limit changed to: " + limit);
            // Apply FPS limit to game loop
        });
        
        masterVolume.setOnChangeCallback(volume -> {
            Log.info("Master volume changed to: " + volume);
            // Apply volume to audio system
        });
        
        fullscreen.setOnChangeCallback(enabled -> {
            Log.info("Fullscreen changed to: " + enabled);
            // Apply fullscreen setting to window
        });
    }
    
    /**
     * Demonstrate various config manager features
     */
    private static void demonstrateUsage(ConfigManager configManager) {
        Log.info("=== Configuration Demo ===");
        
        // Get and set values
        Boolean vsync = configManager.getValue("graphics.vsync");
        Log.info("Current VSync setting: " + vsync);
        
        configManager.setValue("graphics.vsync", false);
        Log.info("VSync set to: " + configManager.getValue("graphics.vsync"));
        
        // Category operations
        Log.info("Available categories: " + configManager.getCategories());
        
        var graphicsConfigs = configManager.getConfigsInCategory("Graphics");
        Log.info("Graphics configs: " + graphicsConfigs.size());
        
        // Validation
        boolean valid = configManager.setValue("graphics.fps_limit", 1000); // Invalid value
        Log.info("Setting invalid FPS limit: " + valid);
        
        valid = configManager.setValue("graphics.fps_limit", 120); // Valid value
        Log.info("Setting valid FPS limit: " + valid);
        
        // Change tracking
        Log.info("Has unsaved changes: " + configManager.hasUnsavedChanges());
        
        var changedConfigs = configManager.getChangedConfigs();
        Log.info("Changed configs: " + changedConfigs.size());
        
        // Save configurations
        configManager.save();
        Log.info("Configurations saved");
        
        // Reset operations
        configManager.resetCategory("Audio");
        Log.info("Audio category reset to defaults");
        
        // Validation check
        var errors = configManager.validateAll();
        if (!errors.isEmpty()) {
            Log.warn("Configuration validation errors:");
            errors.forEach((key, error) -> Log.warn("  " + key + ": " + error));
        } else {
            Log.info("All configurations are valid");
        }
    }
    
    /**
     * Setup UI integration (commented out as it requires UI system instances)
     */
    /*
    private static void setupUI(ConfigManager configManager) {
        // In a real application, you would have these instances from your game engine
        UIManager uiManager = null; // Get from your engine
        UIRenderer uiRenderer = null; // Get from your engine
        
        if (uiManager != null && uiRenderer != null) {
            ConfigUI configUI = new ConfigUI(configManager, uiManager, uiRenderer);
            
            // Show the config UI
            configUI.setVisible(true);
            
            // Add global observer for logging
            configManager.addGlobalObserver(event -> {
                Log.info("Config changed: " + event.getKey() + " = " + event.getNewValue());
            });
        }
    }
    */
} 