# Generic GetComponent Method Usage Guide

This guide demonstrates how to use the generic `GetComponent<T>` method in the Entity class to retrieve components of any type that extends `ComponentBase`.

## Overview

The `Entity` class now has a generic `GetComponent` method that allows you to retrieve components by their class type, providing type safety and eliminating the need for casting.

## Method Signature

```java
public <T extends ComponentBase> T GetComponent(Class<T> componentClass)
```

## Basic Usage

### 1. Creating Components

First, create components that extend `ComponentBase`:

```java
// Example: TexturedComponent
public class TexturedComponent extends ComponentBase {
    public Texture mTexture;
    public String mTexturePath;
    public float mWidth, mHeight;
    
    public TexturedComponent(String texturePath, float width, float height) {
        super("TexturedComponent");
        mTexturePath = texturePath;
        mWidth = width;
        mHeight = height;
    }
}

// Example: TransformComponent
public class TransformComponent extends ComponentBase {
    public Vector3f mPosition;
    public Vector3f mRotation;
    public Vector3f mScale;
    
    public TransformComponent() {
        super("TransformComponent");
        mPosition = new Vector3f(0, 0, 0);
        mRotation = new Vector3f(0, 0, 0);
        mScale = new Vector3f(1, 1, 1);
    }
}
```

### 2. Adding Components to Entities

```java
public class MyEntity extends Entity {
    public MyEntity() {
        super(true, true);
        
        // Add components
        AddComponent(new TexturedComponent("textures/player.png", 64, 64));
        AddComponent(new TransformComponent());
    }
}
```

### 3. Retrieving Components Using Generic Method

```java
// Type-safe component retrieval
TexturedComponent textured = GetComponent(TexturedComponent.class);
TransformComponent transform = GetComponent(TransformComponent.class);

// No casting needed - the method returns the correct type
if (textured != null) {
    System.out.println("Texture path: " + textured.mTexturePath);
    System.out.println("Size: " + textured.mWidth + "x" + textured.mHeight);
}

if (transform != null) {
    System.out.println("Position: " + transform.mPosition);
}
```

## Advanced Usage Examples

### Example 1: Entity with Multiple Components

```java
public class PlayerEntity extends Entity {
    public PlayerEntity() {
        super(true, true);
        
        // Add multiple components
        AddComponent(new TexturedComponent("textures/player.png", 64, 64));
        AddComponent(new TransformComponent());
        AddComponent(new HealthComponent(100));
        AddComponent(new MovementComponent(5.0f));
    }
    
    @Override
    public void Update() {
        // Get components using generic method
        TransformComponent transform = GetComponent(TransformComponent.class);
        MovementComponent movement = GetComponent(MovementComponent.class);
        HealthComponent health = GetComponent(HealthComponent.class);
        
        // Use components safely
        if (transform != null && movement != null) {
            // Update position based on movement
            transform.mPosition.x += movement.mSpeed * deltaTime;
        }
        
        if (health != null && health.mCurrentHealth <= 0) {
            // Player is dead
            SetActive(false);
        }
    }
    
    @Override
    public void Draw(Renderer renderer) {
        TexturedComponent textured = GetComponent(TexturedComponent.class);
        TransformComponent transform = GetComponent(TransformComponent.class);
        
        if (textured != null && transform != null) {
            renderer.drawTexturedQuadBatch(
                transform.mPosition.x, transform.mPosition.y,
                textured.mWidth, textured.mHeight,
                textured.mTexture.mId, 1.0f, 1.0f, 1.0f, 1.0f
            );
        }
    }
}
```

### Example 2: Component-Specific Methods

```java
public class TexturedEntity extends Entity {
    public TexturedEntity(String texturePath, float width, float height) {
        super(true, true);
        AddComponent(new TexturedComponent(texturePath, width, height));
    }
    
    // Convenience methods that use the generic GetComponent
    public void setTextureColor(float r, float g, float b, float a) {
        TexturedComponent textured = GetComponent(TexturedComponent.class);
        if (textured != null) {
            textured.mR = r;
            textured.mG = g;
            textured.mB = b;
            textured.mA = a;
        }
    }
    
    public void setTextureSize(float width, float height) {
        TexturedComponent textured = GetComponent(TexturedComponent.class);
        if (textured != null) {
            textured.mWidth = width;
            textured.mHeight = height;
        }
    }
    
    public TexturedComponent getTexturedComponent() {
        return GetComponent(TexturedComponent.class);
    }
}
```

### Example 3: Scene-Level Component Processing

```java
public class GameScene extends SceneBase {
    @Override
    public void Update(float dT) {
        // Update all entities
        for (Entity entity : entities) {
            if (entity.mIsActive) {
                entity.Update();
            }
        }
    }
    
    @Override
    public void Draw(Renderer renderer, TextRenderer textRenderer) {
        renderer.beginBatch();
        
        // Draw all entities with textured components
        for (Entity entity : entities) {
            if (entity.mIsVisible) {
                // Use generic GetComponent to find textured components
                TexturedComponent textured = entity.GetComponent(TexturedComponent.class);
                TransformComponent transform = entity.GetComponent(TransformComponent.class);
                
                if (textured != null && transform != null) {
                    renderer.drawTexturedQuadBatch(
                        transform.mPosition.x, transform.mPosition.y,
                        textured.mWidth, textured.mHeight,
                        textured.mTexture.mId,
                        textured.mR, textured.mG, textured.mB, textured.mA
                    );
                }
            }
        }
        
        renderer.endBatch();
    }
    
    // Load textures for all entities with textured components
    public void loadAllTextures() {
        for (Entity entity : entities) {
            TexturedComponent textured = entity.GetComponent(TexturedComponent.class);
            if (textured != null && textured.mTexture == null) {
                try {
                    textured.mTexture = TextureLoader.LoadTexture(textured.mTexturePath);
                } catch (Exception e) {
                    System.err.println("Failed to load texture: " + textured.mTexturePath);
                }
            }
        }
    }
}
```

## Benefits of the Generic Method

### 1. Type Safety
```java
// Old way (unsafe)
TexturedComponent textured = (TexturedComponent) GetComponent("TexturedComponent");

// New way (type-safe)
TexturedComponent textured = GetComponent(TexturedComponent.class);
```

### 2. No Casting Required
```java
// The method returns the exact type you request
TransformComponent transform = GetComponent(TransformComponent.class);
// transform is already of type TransformComponent, no casting needed
```

### 3. Compile-Time Error Checking
```java
// This will cause a compile error if the component doesn't exist
HealthComponent health = GetComponent(HealthComponent.class);
// If HealthComponent doesn't extend ComponentBase, you'll get a compile error
```

### 4. IDE Support
- Auto-completion works correctly
- Refactoring tools can track component usage
- Better code navigation

## Performance Considerations

The generic method uses `isInstance()` and `cast()` which are very fast operations. The performance is equivalent to the previous implementation but with added type safety.

## Migration from Old Method

If you were using the old string-based or non-generic methods:

```java
// Old way
ComponentBase component = GetComponent("TexturedComponent");
TexturedComponent textured = (TexturedComponent) component;

// New way
TexturedComponent textured = GetComponent(TexturedComponent.class);
```

The new method is safer, more readable, and provides better IDE support. 