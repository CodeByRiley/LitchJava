# Quad Rotation Implementation Guide

This guide demonstrates how to use the new quad rotation feature in the `QuadBatch` system.

## Overview

The `QuadBatch` class now supports rotating quads around their center point. This is implemented by:

1. **Extended Vertex Structure**: Added rotation data (centerX, centerY, cosA, sinA) to each vertex
2. **Shader Support**: Updated `BatchVertexShader` to apply rotation transformations
3. **New Methods**: Added overloaded methods that accept rotation parameters

## Usage Examples

### 1. Basic Rotation

```java
// Draw a colored quad with rotation (rotation in radians)
renderer.beginBatch();
renderer.drawColoredQuadBatch(100, 100, 64, 64, 1.0f, 0.0f, 0.0f, 1.0f, (float)Math.PI / 4); // 45 degrees
renderer.endBatch();
```

### 2. Animated Rotation

```java
float elapsedTime = 0.0f;

@Override
public void Draw(Renderer renderer, TextRenderer textRenderer) {
    elapsedTime += deltaTime / 1000.0f; // Convert to seconds
    
    renderer.beginBatch();
    
    // Draw rotating colored quads
    for (int i = 0; i < 10; i++) {
        float x = 100 + i * 80;
        float y = 200;
        float rotation = elapsedTime + i * 0.5f; // Each quad rotates at different speed
        float r = 0.5f + 0.5f * (float)Math.sin(elapsedTime + i);
        float g = 0.5f + 0.5f * (float)Math.cos(elapsedTime + i);
        float b = 0.5f + 0.5f * (float)Math.sin(elapsedTime * 0.5f + i);
        
        renderer.drawColoredQuadBatch(x, y, 50, 50, r, g, b, 0.8f, rotation);
    }
    
    renderer.endBatch();
}
```

### 3. Textured Quad Rotation

```java
// Draw a textured quad with rotation
Texture playerTexture = TextureLoader.LoadTexture("textures/player.png");
renderer.beginBatch();
renderer.drawTexturedQuadBatch(300, 300, 100, 100, playerTexture.mId, 1.0f, 1.0f, 1.0f, 1.0f, (float)Math.PI / 6);
renderer.endBatch();
```

### 4. Entity with Rotation

```java
public class RotatingEntity extends Entity {
    private float rotation = 0.0f;
    private Texture texture;
    
    public RotatingEntity(String texturePath) {
        super(true, true);
        texture = TextureLoader.LoadTexture(texturePath);
    }
    
    @Override
    public void Update() {
        rotation += 0.02f; // Rotate 2 degrees per frame
    }
    
    @Override
    public void Draw(Renderer renderer) {
        renderer.drawTexturedQuadBatch(
            mTransform.mPosition.x, mTransform.mPosition.y,
            64, 64, texture.mId, 1.0f, 1.0f, 1.0f, 1.0f, rotation
        );
    }
}
```

## Available Methods

### Colored Quads
```java
// Without rotation
drawColoredQuadBatch(x, y, width, height, r, g, b, a)

// With rotation (rotation in radians)
drawColoredQuadBatch(x, y, width, height, r, g, b, a, rotation)
```

### Textured Quads
```java
// Without rotation
drawTexturedQuadBatch(x, y, width, height, textureId, r, g, b, a)

// With rotation
drawTexturedQuadBatch(x, y, width, height, textureId, r, g, b, a, rotation)

// With custom UV coordinates
drawTexturedQuadBatch(x, y, width, height, textureId, u1, v1, u2, v2, r, g, b, a)

// With custom UV coordinates and rotation
drawTexturedQuadBatch(x, y, width, height, textureId, u1, v1, u2, v2, r, g, b, a, rotation)
```

## Technical Details

### Vertex Structure
Each vertex now contains 12 floats:
- `x, y` - Position
- `r, g, b, a` - Color
- `u, v` - Texture coordinates
- `centerX, centerY, cosA, sinA` - Rotation data

### Rotation Center
Quads rotate around their center point, not their top-left corner.

### Performance
- Rotation calculations are done on the GPU in the vertex shader
- No additional CPU overhead for rotation
- Maintains the same batching efficiency

### Shader Implementation
The vertex shader applies rotation using a 2D rotation matrix:
```glsl
vec2 finalPos = aPos;
if (aRotation.z != 1.0 || aRotation.w != 0.0) { // Check if rotation needed
    vec2 center = aRotation.xy;
    vec2 offset = aPos - center;
    float cosA = aRotation.z;
    float sinA = aRotation.w;
    finalPos = center + vec2(
        offset.x * cosA - offset.y * sinA,
        offset.x * sinA + offset.y * cosA
    );
}
```

## Tips

1. **Rotation Units**: Rotation is in radians (0 to 2π for full rotation)
2. **Performance**: Use rotation only when needed - unrotated quads are slightly faster
3. **Batching**: Rotated and unrotated quads can be batched together
4. **Precision**: For smooth rotation, use small increments (e.g., 0.02f radians per frame)

## Example Scene Integration

```java
@Override
public void Draw(Renderer renderer, TextRenderer textRenderer) {
    renderer.beginBatch();
    
    // Draw entities with rotation
    for (Entity entity : entities) {
        if (entity.mIsVisible) {
            TexturedComponent textured = entity.GetComponent(TexturedComponent.class);
            if (textured != null && textured.mTexture != null) {
                float rotation = elapsedTime + entity.mId * 0.1f; // Unique rotation per entity
                renderer.drawTexturedQuadBatch(
                    entity.mTransform.mPosition.x, entity.mTransform.mPosition.y,
                    textured.mWidth, textured.mHeight,
                    textured.mTexture.mId,
                    textured.mR, textured.mG, textured.mB, textured.mA,
                    rotation
                );
            }
        }
    }
    
    renderer.endBatch();
}
``` 