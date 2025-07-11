# Configuration Management System

A robust, modern configuration management system for the Litch game engine with UI integration, hot-reloading, and comprehensive validation.

## Features

### 🎯 **Modern Design Practices**
- **Type-safe configuration** with generics
- **Observer pattern** for reactive UI updates
- **Singleton pattern** for global access
- **Builder pattern** for complex configurations
- **Validation framework** with custom predicates
- **Change tracking** and undo/redo support

### 🔧 **Core Functionality**
- **Category-based organization** for logical grouping
- **Default value management** with fallbacks
- **Persistence** with JSON format
- **Hot-reloading** via file system watching
- **Thread-safe operations** with concurrent collections
- **Error handling** with comprehensive logging

### 🎨 **UI Integration**
- **Reactive UI updates** when configs change
- **Category-based UI panels**
- **Type-specific controls** (sliders, checkboxes, text inputs)
- **Real-time validation feedback**
- **Save/reset functionality**
- **Change indicator** for unsaved modifications

## Architecture

### Core Classes

#### `Config<T>`
Represents a single configuration entry with:
- Type-safe value storage
- Validation support
- Change callbacks
- Default value management
- Change tracking

#### `ConfigManager`
Singleton manager providing:
- Configuration registration
- Observer pattern implementation
- File persistence
- Hot-reloading
- Category management
- Validation framework

#### `ConfigUI`
UI integration layer with:
- Category-based navigation
- Type-specific controls
- Real-time updates
- Save/reset functionality

## Usage Examples

### Basic Configuration Setup

```java
// Get the singleton instance
ConfigManager configManager = ConfigManager.getInstance();

// Register a simple boolean config
Config<Boolean> vsync = configManager.register(
    "graphics.vsync", 
    "Graphics", 
    "Enable Vertical Sync", 
    true, 
    Boolean.class
);

// Register with validation
Config<Integer> fpsLimit = configManager.register(
    "graphics.fps_limit", 
    "Graphics", 
    "FPS Limit", 
    60, 
    Integer.class,
    value -> value >= 30 && value <= 300 // Validation predicate
);
```

### Change Callbacks

```java
// Setup change callbacks
vsync.setOnChangeCallback(enabled -> {
    Log.info("VSync changed to: " + enabled);
    // Apply setting to graphics system
    graphicsSystem.setVSync(enabled);
});

// Global observer for all changes
configManager.addGlobalObserver(event -> {
    Log.info("Config changed: " + event.getKey() + " = " + event.getNewValue());
});
```

### UI Integration

```java
// Create UI components
ConfigUI configUI = new ConfigUI(configManager, uiManager, uiRenderer);

// Show/hide the config UI
configUI.setVisible(true);

// The UI automatically updates when configs change
```

### File Operations

```java
// Load existing configurations
configManager.load();

// Save current configurations
configManager.save();

// Start hot-reloading
configManager.startFileWatching();

// Check for unsaved changes
if (configManager.hasUnsavedChanges()) {
    // Prompt user to save
}
```

## Configuration File Format

The system saves configurations in JSON format:

```json
{
  "version": "1.0",
  "configs": {
    "graphics.vsync": true,
    "graphics.fps_limit": 60,
    "audio.master_volume": 0.8,
    "game.fullscreen": false
  }
}
```

## Advanced Features

### Validation Framework

```java
// Custom validation predicates
Predicate<String> playerNameValidator = name -> 
    name != null && 
    !name.trim().isEmpty() && 
    name.length() <= 20;

Config<String> playerName = configManager.register(
    "game.player_name", 
    "Game", 
    "Player Name", 
    "Player", 
    String.class,
    playerNameValidator
);
```

### Category Operations

```java
// Get all categories
Set<String> categories = configManager.getCategories();

// Get configs in a category
List<Config<?>> graphicsConfigs = configManager.getConfigsInCategory("Graphics");

// Reset entire category
configManager.resetCategory("Audio");
```

### Change Tracking

```java
// Check for changes
boolean hasChanges = configManager.hasUnsavedChanges();

// Get changed configs
List<Config<?>> changedConfigs = configManager.getChangedConfigs();

// Validate all configs
Map<String, String> errors = configManager.validateAll();
```

## Best Practices

### 1. **Organize by Categories**
Group related configurations logically:
- `graphics.*` - Visual settings
- `audio.*` - Sound settings  
- `game.*` - Gameplay settings
- `input.*` - Control settings

### 2. **Use Descriptive Keys**
Choose clear, hierarchical keys:
```java
// Good
"graphics.resolution.width"
"audio.music.volume"
"game.difficulty.level"

// Avoid
"res_w"
"vol"
"diff"
```

### 3. **Provide Meaningful Descriptions**
Descriptions appear in the UI:
```java
Config<Float> brightness = configManager.register(
    "graphics.brightness", 
    "Graphics", 
    "Screen Brightness (0.0 - 2.0)", 
    1.0f, 
    Float.class
);
```

### 4. **Set Appropriate Defaults**
Choose sensible default values:
```java
// Good defaults
Config<Boolean> fullscreen = configManager.register(
    "game.fullscreen", "Game", "Fullscreen Mode", false, Boolean.class
);

Config<Integer> fpsLimit = configManager.register(
    "graphics.fps_limit", "Graphics", "FPS Limit", 60, Integer.class
);
```

### 5. **Validate Input Ranges**
Always validate numeric ranges:
```java
// Validate ranges
value -> value >= 0.0f && value <= 1.0f  // 0-100%
value -> value >= 30 && value <= 300     // FPS limits
value -> value >= 1 && value <= 5        // Difficulty levels
```

### 6. **Handle Changes Reactively**
Use callbacks to apply changes immediately:
```java
fpsLimit.setOnChangeCallback(limit -> {
    gameLoop.setTargetFPS(limit);
    Log.info("FPS limit updated to: " + limit);
});
```

## Integration with Game Engine

### Initialization
```java
public class GameEngine {
    private ConfigManager configManager;
    private ConfigUI configUI;
    
    public void initialize() {
        // Initialize config system
        configManager = ConfigManager.getInstance();
        setupConfigurations();
        configManager.load();
        configManager.startFileWatching();
        
        // Setup UI integration
        configUI = new ConfigUI(configManager, uiManager, uiRenderer);
    }
}
```

### Runtime Usage
```java
// Access configs throughout the engine
boolean vsync = configManager.getValue("graphics.vsync");
float volume = configManager.getValue("audio.master_volume");
String playerName = configManager.getValue("game.player_name");

// Apply settings
graphicsSystem.setVSync(vsync);
audioSystem.setMasterVolume(volume);
gameState.setPlayerName(playerName);
```

## Performance Considerations

### Memory Efficiency
- Configurations are stored in concurrent collections for thread safety
- Lazy loading of file watchers
- Efficient change tracking with minimal overhead

### File I/O
- JSON format for human readability
- Buffered I/O operations
- File watching with debouncing to prevent excessive reloads

### UI Performance
- Reactive updates only when values change
- Efficient UI element reuse
- Minimal redraws with change detection

## Error Handling

The system provides comprehensive error handling:

```java
// Validation errors
if (!config.setValue(invalidValue)) {
    Log.warn("Invalid value rejected for: " + config.getKey());
}

// File I/O errors
try {
    configManager.save();
} catch (Exception e) {
    Log.error("Failed to save configuration: " + e.getMessage());
}

// Validation check
Map<String, String> errors = configManager.validateAll();
if (!errors.isEmpty()) {
    // Handle validation errors
}
```

## Future Enhancements

### Planned Features
- **XML format support** for complex configurations
- **Encryption** for sensitive settings
- **Cloud sync** for cross-device settings
- **Profile system** for multiple user configurations
- **Advanced UI controls** (color pickers, file browsers)
- **Configuration templates** for different game modes

### Extension Points
- **Custom validators** for complex validation logic
- **Custom UI controls** for specialized configuration types
- **Plugin system** for third-party configuration extensions
- **Migration system** for configuration format updates

## Conclusion

This configuration management system provides a robust, modern foundation for managing game settings with excellent UI integration. It follows modern design practices, provides comprehensive validation, and supports hot-reloading for a smooth development experience.

The system is designed to be extensible and maintainable, making it easy to add new configuration types and UI controls as your game engine evolves. 