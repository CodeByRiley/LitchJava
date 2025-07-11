package com.codebyriley.Core.Config;

import com.codebyriley.Core.Rendering.UI.*;
import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Core.Rendering.UI.Text.TextRenderer;
import com.codebyriley.Core.Rendering.UI.Text.FontLoader;
import com.codebyriley.Util.Math.Vector2f;
import com.codebyriley.Util.Math.Vector3f;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * UI components for configuration management.
 * Provides interactive elements for viewing and modifying configuration values.
 */
public class ConfigUI {
    private final ConfigManager configManager;
    private final UIManager uiManager;
    private final UIRenderer uiRenderer;
    private TextRenderer textRenderer;
    private Panel mainPanel;
    private Panel categoryPanel;
    private Panel configPanel;
    private String currentCategory;
    
    public ConfigUI(ConfigManager configManager, UIManager uiManager, UIRenderer uiRenderer) {
        this.configManager = configManager;
        this.uiManager = uiManager;
        this.uiRenderer = uiRenderer;
        this.currentCategory = null;
        
        // Initialize text renderer (you'll need to provide a FontLoader instance)
        // this.textRenderer = new TextRenderer(uiRenderer, fontLoader);
        
        setupUI();
        setupObservers();
    }
    
    /**
     * Setup the main UI layout
     */
    private void setupUI() {
        // Main container panel
        mainPanel = new Panel(50, 50, 800, 600);
        mainPanel.setId("config_main_panel");
        mainPanel.setBackgroundColor(new Vector3f(0.1f, 0.1f, 0.1f));
        mainPanel.setBorderColor(new Vector3f(0.3f, 0.3f, 0.3f));
        mainPanel.setBorderThickness(2.0f);
        
        // Title - using a simple panel as label for now
        Panel titlePanel = new Panel(20, 20, 760, 40);
        titlePanel.setId("config_title");
        titlePanel.setBackgroundColor(new Vector3f(0.2f, 0.2f, 0.2f));
        mainPanel.addChild(titlePanel);
        
        // Category panel (left side)
        categoryPanel = new Panel(20, 80, 200, 480);
        categoryPanel.setId("config_category_panel");
        categoryPanel.setBackgroundColor(new Vector3f(0.15f, 0.15f, 0.15f));
        categoryPanel.setBorderColor(new Vector3f(0.4f, 0.4f, 0.4f));
        categoryPanel.setBorderThickness(1.0f);
        mainPanel.addChild(categoryPanel);
        
        // Config panel (right side)
        configPanel = new Panel(240, 80, 540, 480);
        configPanel.setId("config_config_panel");
        configPanel.setBackgroundColor(new Vector3f(0.15f, 0.15f, 0.15f));
        configPanel.setBorderColor(new Vector3f(0.4f, 0.4f, 0.4f));
        configPanel.setBorderThickness(1.0f);
        mainPanel.addChild(configPanel);
        
        // Control buttons
        setupControlButtons();
        
        // Add to UI manager
        uiManager.addElement(mainPanel);
        
        // Populate categories
        refreshCategories();
    }
    
    /**
     * Setup control buttons (Save, Reset, etc.)
     */
    private void setupControlButtons() {
        // Save button - using panels as buttons for now
        Panel saveButton = new Panel(20, 580, 100, 30);
        saveButton.setId("config_save_button");
        saveButton.setBackgroundColor(new Vector3f(0.3f, 0.7f, 0.3f));
        saveButton.setBorderColor(new Vector3f(0.5f, 0.5f, 0.5f));
        mainPanel.addChild(saveButton);
        
        // Reset button
        Panel resetButton = new Panel(140, 580, 100, 30);
        resetButton.setId("config_reset_button");
        resetButton.setBackgroundColor(new Vector3f(0.7f, 0.3f, 0.3f));
        resetButton.setBorderColor(new Vector3f(0.5f, 0.5f, 0.5f));
        mainPanel.addChild(resetButton);
        
        // Close button
        Panel closeButton = new Panel(680, 580, 100, 30);
        closeButton.setId("config_close_button");
        closeButton.setBackgroundColor(new Vector3f(0.3f, 0.3f, 0.7f));
        closeButton.setBorderColor(new Vector3f(0.5f, 0.5f, 0.5f));
        mainPanel.addChild(closeButton);
        
        updateSaveButtonState();
    }
    
    /**
     * Setup observers for configuration changes
     */
    private void setupObservers() {
        // Global observer to update UI when configs change
        configManager.addGlobalObserver(event -> {
            if (currentCategory != null && event.getCategory().equals(currentCategory)) {
                refreshCurrentCategory();
            }
            updateSaveButtonState();
        });
    }
    
    /**
     * Refresh the category list
     */
    public void refreshCategories() {
        categoryPanel.clearChildren();
        
        int yOffset = 10;
        for (String category : configManager.getCategories()) {
            Panel categoryButton = new Panel(10, yOffset, 180, 30);
            categoryButton.setId("category_" + category);
            categoryButton.setBackgroundColor(new Vector3f(0.2f, 0.2f, 0.2f));
            categoryButton.setBorderColor(new Vector3f(0.5f, 0.5f, 0.5f));
            
            categoryPanel.addChild(categoryButton);
            yOffset += 35;
        }
        
        // Select first category if none selected
        if (currentCategory == null && !configManager.getCategories().isEmpty()) {
            selectCategory(configManager.getCategories().iterator().next());
        }
    }
    
    /**
     * Select a category and show its configurations
     */
    public void selectCategory(String category) {
        currentCategory = category;
        
        // Update category button colors
        for (String cat : configManager.getCategories()) {
            Panel button = (Panel) categoryPanel.findChild("category_" + cat);
            if (button != null) {
                if (cat.equals(category)) {
                    button.setBackgroundColor(new Vector3f(0.3f, 0.5f, 0.7f));
                } else {
                    button.setBackgroundColor(new Vector3f(0.2f, 0.2f, 0.2f));
                }
            }
        }
        
        refreshCurrentCategory();
    }
    
    /**
     * Refresh the current category's configuration display
     */
    public void refreshCurrentCategory() {
        if (currentCategory == null) return;
        
        configPanel.clearChildren();
        
        List<Config<?>> configs = configManager.getConfigsInCategory(currentCategory);
        int yOffset = 10;
        
        for (Config<?> config : configs) {
            yOffset = createConfigElement(config, yOffset);
        }
    }
    
    /**
     * Create a UI element for a configuration
     */
    private int createConfigElement(Config<?> config, int yOffset) {
        // Config label - using panel as label
        Panel label = new Panel(10, yOffset, 200, 25);
        label.setId("label_" + config.getKey());
        label.setBackgroundColor(new Vector3f(0.2f, 0.2f, 0.2f));
        configPanel.addChild(label);
        
        yOffset += 30;
        
        // Create appropriate control based on type
        Class<?> type = config.getType();
        if (type == Boolean.class || type == boolean.class) {
            createBooleanControl(config, yOffset);
            yOffset += 40;
        } else if (type == Integer.class || type == int.class) {
            yOffset = createIntegerControl(config, yOffset);
        } else if (type == Float.class || type == float.class) {
            yOffset = createFloatControl(config, yOffset);
        } else if (type == String.class) {
            yOffset = createStringControl(config, yOffset);
        } else {
            // Generic text display
            Panel valuePanel = new Panel(10, yOffset, 300, 25);
            valuePanel.setId("value_" + config.getKey());
            valuePanel.setBackgroundColor(new Vector3f(0.3f, 0.3f, 0.3f));
            configPanel.addChild(valuePanel);
            yOffset += 35;
        }
        
        // Reset button for this config
        Panel resetButton = new Panel(320, yOffset - 35, 60, 25);
        resetButton.setId("reset_" + config.getKey());
        resetButton.setBackgroundColor(new Vector3f(0.6f, 0.3f, 0.3f));
        configPanel.addChild(resetButton);
        
        return yOffset;
    }
    
    /**
     * Create a boolean checkbox control
     */
    private void createBooleanControl(Config<?> config, int yOffset) {
        boolean value = (Boolean) config.getValue();
        
        Panel checkbox = new Panel(10, yOffset, 25, 25);
        checkbox.setId("checkbox_" + config.getKey());
        checkbox.setBackgroundColor(value ? new Vector3f(0.3f, 0.7f, 0.3f) : new Vector3f(0.3f, 0.3f, 0.3f));
        checkbox.setBorderColor(new Vector3f(0.6f, 0.6f, 0.6f));
        configPanel.addChild(checkbox);
    }
    
    /**
     * Create an integer slider control
     */
    private int createIntegerControl(Config<?> config, int yOffset) {
        int value = (Integer) config.getValue();
        
        // Simple panel as slider for now
        Panel slider = new Panel(10, yOffset, 200, 20);
        slider.setId("slider_" + config.getKey());
        slider.setBackgroundColor(new Vector3f(0.4f, 0.4f, 0.4f));
        configPanel.addChild(slider);
        
        // Value panel
        Panel valuePanel = new Panel(220, yOffset, 80, 20);
        valuePanel.setId("value_" + config.getKey());
        valuePanel.setBackgroundColor(new Vector3f(0.3f, 0.3f, 0.3f));
        configPanel.addChild(valuePanel);
        
        return yOffset + 30;
    }
    
    /**
     * Create a float slider control
     */
    private int createFloatControl(Config<?> config, int yOffset) {
        float value = (Float) config.getValue();
        
        // Simple panel as slider for now
        Panel slider = new Panel(10, yOffset, 200, 20);
        slider.setId("slider_" + config.getKey());
        slider.setBackgroundColor(new Vector3f(0.4f, 0.4f, 0.4f));
        configPanel.addChild(slider);
        
        // Value panel
        Panel valuePanel = new Panel(220, yOffset, 80, 20);
        valuePanel.setId("value_" + config.getKey());
        valuePanel.setBackgroundColor(new Vector3f(0.3f, 0.3f, 0.3f));
        configPanel.addChild(valuePanel);
        
        return yOffset + 30;
    }
    
    /**
     * Create a string text input control
     */
    private int createStringControl(Config<?> config, int yOffset) {
        String value = (String) config.getValue();
        
        // Text input panel
        Panel textPanel = new Panel(10, yOffset, 300, 25);
        textPanel.setId("text_" + config.getKey());
        textPanel.setBackgroundColor(new Vector3f(0.3f, 0.3f, 0.3f));
        configPanel.addChild(textPanel);
        
        return yOffset + 35;
    }
    
    /**
     * Update the save button state based on whether there are unsaved changes
     */
    private void updateSaveButtonState() {
        Panel saveButton = (Panel) mainPanel.findChild("config_save_button");
        if (saveButton != null) {
            boolean hasChanges = configManager.hasUnsavedChanges();
            saveButton.setBackgroundColor(hasChanges ? 
                new Vector3f(0.3f, 0.7f, 0.3f) : new Vector3f(0.3f, 0.3f, 0.3f));
        }
    }
    
    /**
     * Show the configuration UI
     */
    public void setVisible(boolean visible) {
        mainPanel.setVisible(visible);
    }
    
    /**
     * Check if the UI is visible
     */
    public boolean isVisible() {
        return mainPanel.isVisible();
    }
}