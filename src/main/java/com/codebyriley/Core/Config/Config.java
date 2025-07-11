package com.codebyriley.Core.Config;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Represents a single configuration entry with type safety, validation, and
 * change tracking.
 * Supports various data types and provides a robust foundation for
 * configuration management.
 */
public class Config<T> {
    private final String key;
    private final String category;
    private final String description;
    private T value;
    private T defaultValue;
    private T previousValue;
    private Predicate<T> validator;
    private Consumer<T> onChangeCallback;
    private boolean hasChanged;
    private boolean isPersistent;
    private final Class<T> type;

    /**
     * Creates a new configuration entry
     * 
     * @param key          Unique identifier for this config
     * @param category     Category for organization
     * @param description  Human-readable description
     * @param defaultValue Default value
     * @param type         Type class for type safety
     */
    public Config(String key, String category, String description, T defaultValue, Class<T> type) {
        this.key = key;
        this.category = category;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.previousValue = defaultValue;
        this.type = type;
        this.hasChanged = false;
        this.isPersistent = true;
        this.validator = null;
        this.onChangeCallback = null;
    }

    /**
     * Creates a new configuration entry with validation
     */
    public Config(String key, String category, String description, T defaultValue,
            Class<T> type, Predicate<T> validator) {
        this(key, category, description, defaultValue, type);
        this.validator = validator;
    }

    /**
     * Set the current value, triggering validation and change callbacks
     */
    public boolean setValue(T newValue) {
        // Validate the new value
        if (validator != null && !validator.test(newValue)) {
            return false;
        }

        // Check if value actually changed
        if (Objects.equals(this.value, newValue)) {
            return true;
        }

        // Store previous value for undo functionality
        this.previousValue = this.value;
        this.value = newValue;
        this.hasChanged = true;

        // Trigger change callback
        if (onChangeCallback != null) {
            try {
                onChangeCallback.accept(newValue);
            } catch (Exception e) {
                System.err.println("Error in config change callback for " + key + ": " + e.getMessage());
            }
        }

        return true;
    }

    /**
     * Get the current value
     */
    public T getValue() {
        return value;
    }

    /**
     * Get the default value
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Get the previous value (for undo functionality)
     */
    public T getPreviousValue() {
        return previousValue;
    }

    /**
     * Reset to default value
     */
    public void resetToDefault() {
        setValue(defaultValue);
    }

    /**
     * Undo the last change
     */
    public void undo() {
        if (hasChanged) {
            T temp = value;
            value = previousValue;
            previousValue = temp;
            hasChanged = false;

            if (onChangeCallback != null) {
                try {
                    onChangeCallback.accept(value);
                } catch (Exception e) {
                    System.err.println("Error in config undo callback for " + key + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Set a callback to be called when the value changes
     */
    public void setOnChangeCallback(Consumer<T> callback) {
        this.onChangeCallback = callback;
    }

    /**
     * Set a validator for this config
     */
    public void setValidator(Predicate<T> validator) {
        this.validator = validator;
    }

    /**
     * Validate the current value
     */
    public boolean isValid() {
        return validator == null || validator.test(value);
    }

    /**
     * Check if the value has changed from its default
     */
    public boolean hasChanged() {
        return hasChanged;
    }

    /**
     * Mark as unchanged (useful after saving)
     */
    public void markUnchanged() {
        this.hasChanged = false;
    }

    /**
     * Set whether this config should be persisted
     */
    public void setPersistent(boolean persistent) {
        this.isPersistent = persistent;
    }

    /**
     * Check if this config should be persisted
     */
    public boolean isPersistent() {
        return isPersistent;
    }

    // Getters for metadata
    public String getKey() {
        return key;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("Config{key='%s', category='%s', value=%s, hasChanged=%s}",
                key, category, value, hasChanged);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Config<?> config = (Config<?>) obj;
        return Objects.equals(key, config.key) && Objects.equals(category, config.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, category);
    }
}
