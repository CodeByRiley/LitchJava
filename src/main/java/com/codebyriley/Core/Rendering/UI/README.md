# UI Elements Documentation

This document describes all available UI elements in the Litch engine's UI system.

## Overview

The UI system is built around the `UIElement` base class, with each specific element type extending it. All UI elements are managed by the `UIManager` class, which handles input events, rendering coordination, and element lifecycle.

## Base UIElement

All UI elements inherit from `UIElement` and provide these common features:

- **Positioning**: `x`, `y` coordinates
- **Sizing**: `width`, `height` dimensions
- **Visibility**: `visible` and `enabled` states
- **Styling**: Background color, border color, alpha values
- **Input handling**: Mouse click detection and hover states

## Available UI Elements

### 1. Button

A clickable button with text and hover effects.

```java
Button button = new Button(x, y, width, height, "Button Text", textRenderer);
button.setOnClick(btn -> {
    // Handle button click
    System.out.println("Button clicked!");
});
uiManager.addElement(button);
```

**Features:**
- Text display with customizable font and color
- Hover and pressed state visual feedback
- Click callback support
- Customizable colors for different states

**Methods:**
- `setOnClick(Consumer<Button> callback)` - Set click handler
- `setText(String text)` - Change button text
- `setTextColor(Vector3f color)` - Set text color
- `setHoverColor(Vector3f color)` - Set hover state color
- `setPressedColor(Vector3f color)` - Set pressed state color

### 2. Label

A simple text label for displaying information.

```java
Label label = new Label(x, y, "Label Text", textRenderer);
label.setTextColor(new Vector3f(1.0f, 1.0f, 1.0f));
label.setTextScale(1.2f);
uiManager.addElement(label);
```

**Features:**
- Text display with alignment options
- Auto-sizing based on text content
- Customizable font, color, and scale
- Transparent background by default

**Methods:**
- `setText(String text)` - Change label text
- `setTextColor(Vector3f color)` - Set text color
- `setTextScale(float scale)` - Set text size
- `setAlignment(TextAlignment alignment)` - Set text alignment (LEFT, CENTER, RIGHT)
- `setAutoSize(boolean autoSize)` - Enable/disable auto-sizing

### 3. Panel

A container element that can hold other UI elements and provide layout functionality.

```java
Panel panel = new Panel(x, y, width, height);
panel.setLayoutType(Panel.LayoutType.VERTICAL);
panel.setPadding(10.0f);
panel.setSpacing(5.0f);
panel.addChild(childElement);
uiManager.addElement(panel);
```

**Features:**
- Container for other UI elements
- Automatic layout management (VERTICAL, HORIZONTAL, GRID, NONE)
- Padding and spacing control
- Child element clipping

**Layout Types:**
- `NONE` - No automatic layout
- `VERTICAL` - Stack children vertically
- `HORIZONTAL` - Stack children horizontally
- `GRID` - Arrange children in a grid

**Methods:**
- `addChild(UIElement child)` - Add a child element
- `removeChild(UIElement child)` - Remove a child element
- `setLayoutType(LayoutType type)` - Set layout behavior
- `setPadding(float padding)` - Set internal padding
- `setSpacing(float spacing)` - Set spacing between children

### 4. Slider

A draggable slider for numeric input with visual feedback.

```java
Slider slider = new Slider(x, y, width, height, minValue, maxValue, initialValue);
slider.setOnValueChanged(value -> {
    System.out.println("Slider value: " + value);
});
uiManager.addElement(slider);
```

**Features:**
- Horizontal and vertical orientation (auto-detected)
- Draggable handle with visual feedback
- Value range with min/max bounds
- Value change callbacks
- Customizable colors for track, fill, and handle

**Methods:**
- `setValue(float value)` - Set current value
- `getValue()` - Get current value
- `setRange(float min, float max)` - Set value range
- `setOnValueChanged(Consumer<Float> callback)` - Set value change handler
- `setHandleColor(Vector3f color)` - Set handle color
- `setTrackColor(Vector3f color)` - Set track color
- `setFillColor(Vector3f color)` - Set fill color

### 5. Checkbox

A toggleable checkbox for boolean input.

```java
Checkbox checkbox = new Checkbox(x, y, size, "Option Label", initialValue);
checkbox.setOnCheckedChanged(checked -> {
    System.out.println("Checkbox: " + checked);
});
uiManager.addElement(checkbox);
```

**Features:**
- Toggle functionality with visual check mark
- Optional text label
- Customizable colors and check mark style
- State change callbacks

**Methods:**
- `setChecked(boolean checked)` - Set checked state
- `isChecked()` - Get checked state
- `toggle()` - Toggle current state
- `setOnCheckedChanged(Consumer<Boolean> callback)` - Set state change handler
- `setCheckColor(Vector3f color)` - Set check mark color
- `setLabel(String label)` - Set label text

### 6. ProgressBar

A progress indicator for showing completion or loading states.

```java
ProgressBar progressBar = new ProgressBar(x, y, width, height, initialProgress, textRenderer);
progressBar.setShowText(true);
progressBar.setAnimated(true);
uiManager.addElement(progressBar);
```

**Features:**
- Progress display from 0.0 to 1.0
- Optional text overlay (percentage or custom text)
- Smooth animation support
- Customizable colors for fill and background
- Read-only by default (can be made interactive)

**Methods:**
- `setProgress(float progress)` - Set progress (0.0-1.0)
- `getProgress()` - Get current progress
- `setProgressAnimated(float progress)` - Set progress with animation
- `setShowText(boolean show)` - Show/hide text overlay
- `setText(String text)` - Set custom text
- `setAnimated(boolean animated)` - Enable/disable animation
- `setFillColor(Vector3f color)` - Set fill color
- `setEmptyColor(Vector3f color)` - Set background color

### 7. TextField

A text input field with cursor and keyboard support.

```java
TextField textField = new TextField(x, y, width, height, initialText, textRenderer);
textField.setPlaceholder("Enter text here...");
textField.setOnTextChanged(text -> {
    System.out.println("Text changed: " + text);
});
uiManager.addElement(textField);
```

**Features:**
- Text input with cursor display
- Placeholder text support
- Password mode (masks text with asterisks)
- Keyboard navigation (arrow keys, backspace, delete)
- Focus state with visual feedback
- Character input validation

**Methods:**
- `setText(String text)` - Set text content
- `getText()` - Get current text
- `setPlaceholder(String placeholder)` - Set placeholder text
- `setFocused(boolean focused)` - Set focus state
- `setOnTextChanged(Consumer<String> callback)` - Set text change handler
- `setOnEnterPressed(Consumer<String> callback)` - Set enter key handler
- `setPasswordMode(boolean passwordMode)` - Enable password masking
- `setMaxLength(int maxLength)` - Set maximum text length

### 8. Dropdown

A selection dropdown with expandable menu.

```java
List<String> options = Arrays.asList("Option 1", "Option 2", "Option 3");
Dropdown dropdown = new Dropdown(x, y, width, height, options, textRenderer);
dropdown.setOnSelectionChanged(index -> {
    System.out.println("Selected index: " + index);
});
uiManager.addElement(dropdown);
```

**Features:**
- Expandable menu with options list
- Visual arrow indicator
- Selection highlighting
- Callback support for selection changes
- Customizable colors and styling

**Methods:**
- `setSelectedIndex(int index)` - Set selected option
- `getSelectedIndex()` - Get selected index
- `getSelectedText()` - Get selected text
- `setExpanded(boolean expanded)` - Open/close dropdown
- `addOption(String option)` - Add new option
- `removeOption(String option)` - Remove option
- `setOptions(List<String> options)` - Replace all options
- `setOnSelectionChanged(Consumer<Integer> callback)` - Set selection handler
- `setOnSelectionChangedText(Consumer<String> callback)` - Set text selection handler

## UIManager Integration

The `UIManager` class handles all UI elements and provides these key features:

### Input Handling

```java
// Mouse events
uiManager.onMouseMove(mouseX, mouseY);
uiManager.onMousePress(mouseX, mouseY, button);
uiManager.onMouseRelease(mouseX, mouseY, button);

// Keyboard events
uiManager.onKeyPress(key, mods);
uiManager.onCharInput(character);
```

### Element Management

```java
// Add/remove elements
uiManager.addElement(element);
uiManager.removeElement(element);
uiManager.clear(); // Remove all elements

// Find elements
UIElement element = uiManager.getElement("elementId");
List<Button> buttons = uiManager.getElementsOfType(Button.class);
```

### Rendering

```java
// Render all UI elements
uiManager.render();
```

## Best Practices

1. **Element IDs**: Set unique IDs for elements you need to reference later
2. **Callbacks**: Use lambda expressions for clean callback handling
3. **Layout**: Use Panels with appropriate layout types for organized UI
4. **Styling**: Create consistent color schemes across your UI
5. **Performance**: Avoid creating/destroying UI elements frequently
6. **Accessibility**: Provide clear labels and logical tab order

## Example: Complete UI Setup

```java
// Initialize UI system
UIManager uiManager = new UIManager(uiRenderer);
TextRenderer textRenderer = new TextRenderer(uiRenderer, fontLoader);

// Create main panel
Panel mainPanel = new Panel(50, 50, 600, 400);
mainPanel.setLayoutType(Panel.LayoutType.VERTICAL);
mainPanel.setPadding(20.0f);
uiManager.addElement(mainPanel);

// Add title
Label title = new Label(100, 30, "Settings", textRenderer);
title.setTextScale(1.5f);
uiManager.addElement(title);

// Add slider for volume
Slider volumeSlider = new Slider(100, 100, 200, 20, 0.0f, 100.0f, 50.0f);
volumeSlider.setOnValueChanged(value -> {
    // Handle volume change
    setVolume(value / 100.0f);
});
uiManager.addElement(volumeSlider);

// Add checkbox for fullscreen
Checkbox fullscreenCheck = new Checkbox(100, 150, 20, "Fullscreen", false);
fullscreenCheck.setOnCheckedChanged(checked -> {
    // Handle fullscreen toggle
    setFullscreen(checked);
});
uiManager.addElement(fullscreenCheck);

// Add text field for player name
TextField nameField = new TextField(100, 200, 200, 25, "", textRenderer);
nameField.setPlaceholder("Enter player name...");
nameField.setOnTextChanged(name -> {
    // Handle name change
    setPlayerName(name);
});
uiManager.addElement(nameField);

// Add buttons
Button saveButton = new Button(100, 250, 80, 30, "Save", textRenderer);
saveButton.setOnClick(btn -> saveSettings());
uiManager.addElement(saveButton);

Button cancelButton = new Button(200, 250, 80, 30, "Cancel", textRenderer);
cancelButton.setOnClick(btn -> cancelSettings());
uiManager.addElement(cancelButton);
```

This UI system provides a comprehensive foundation for creating rich, interactive user interfaces in your Litch engine applications. 