# Batching System for Litch Engine

This document explains the batching system implemented in the Litch engine for efficient 2D rendering.

## Overview

The batching system combines multiple draw calls into single GPU operations, significantly improving performance when rendering many similar objects (quads, sprites, text, etc.).

## How It Works

### Traditional Rendering (Inefficient)
```java
// Each quad = 1 draw call
renderer.DrawSquare(100, 100, 50, 50, 1.0f, 0.0f, 0.0f);  // Draw call 1
renderer.DrawSquare(200, 100, 50, 50, 0.0f, 1.0f, 0.0f);  // Draw call 2
renderer.DrawSquare(300, 100, 50, 50, 0.0f, 0.0f, 1.0f);  // Draw call 3
// ... 1000 quads = 1000 draw calls
```

### Batched Rendering (Efficient)
```java
renderer.beginBatch();
renderer.drawColoredQuadBatch(100, 100, 50, 50, 1.0f, 0.0f, 0.0f, 1.0f);
renderer.drawColoredQuadBatch(200, 100, 50, 50, 0.0f, 1.0f, 0.0f, 1.0f);
renderer.drawColoredQuadBatch(300, 100, 50, 50, 0.0f, 0.0f, 1.0f, 1.0f);
// ... 1000 quads = 1 draw call
renderer.endBatch();
```

## Key Components

### 1. QuadBatch Class
- **Location**: `Core/Rendering/Primatives/QuadBatch.java`
- **Purpose**: Manages vertex data accumulation and batch flushing
- **Features**:
  - Supports both colored and textured quads
  - Automatic batch flushing when switching textures
  - Configurable batch size (default: 1000 quads)

### 2. Batch Shaders
- **Vertex Shader**: `resources/shaders/BatchVertexShader.glsl`
- **Fragment Shader**: `resources/shaders/BatchFragmentShader.glsl`
- **Features**:
  - Handles both colored and textured quads
  - Per-vertex colors and texture coordinates
  - Screen-space to NDC conversion

### 3. Renderer Integration
- **New Methods**:
  - `beginBatch()` - Start a new batch
  - `endBatch()` - Flush and end the current batch
  - `drawColoredQuadBatch()` - Add colored quad to batch
  - `drawTexturedQuadBatch()` - Add textured quad to batch

## Usage Examples

### Basic Colored Quads
```java
renderer.beginBatch();

// All these will be batched together (same shader, no texture)
renderer.drawColoredQuadBatch(100, 100, 50, 50, 1.0f, 0.0f, 0.0f, 1.0f);
renderer.drawColoredQuadBatch(200, 100, 50, 50, 0.0f, 1.0f, 0.0f, 1.0f);
renderer.drawColoredQuadBatch(300, 100, 50, 50, 0.0f, 0.0f, 1.0f, 1.0f);

renderer.endBatch(); // Flushes all quads in one draw call
```

### Textured Quads
```java
renderer.beginBatch();

// These will be batched together (same texture)
renderer.drawTexturedQuadBatch(100, 100, 50, 50, textureId, 1.0f, 1.0f, 1.0f, 1.0f);
renderer.drawTexturedQuadBatch(200, 100, 50, 50, textureId, 1.0f, 1.0f, 1.0f, 1.0f);

// Different texture = automatic batch flush + new batch
renderer.drawTexturedQuadBatch(300, 100, 50, 50, differentTextureId, 1.0f, 1.0f, 1.0f, 1.0f);

renderer.endBatch();
```

## Demo Scenes

### BatchDemoScene
- **Key**: Press `B`
- **Features**: 
  - 100 animated colored quads
  - 5 textured quads using font atlas
  - 20 random colored quads
  - Demonstrates efficient batching

### NonBatchDemoScene
- **Key**: Press `N`
- **Features**:
  - Same visual output as BatchDemoScene
  - Uses individual `DrawSquare()` calls
  - Demonstrates performance difference

## Performance Benefits

### Before Batching
- 125+ draw calls per frame
- High CPU overhead
- Poor GPU utilization

### After Batching
- 3-4 draw calls per frame
- Minimal CPU overhead
- Optimal GPU utilization

## Best Practices

1. **Group Similar Objects**: Keep colored quads together, textured quads with the same texture together
2. **Minimize Texture Changes**: Each texture change causes a batch flush
3. **Use Appropriate Batch Size**: Default 1000 quads is good for most cases
4. **Always Call endBatch()**: Ensures all quads are rendered

## Technical Details

### Vertex Format
```java
// Each vertex: x, y, r, g, b, a, u, v
float[] vertex = {
    x, y,           // Position
    r, g, b, a,     // Color
    u, v            // Texture coordinates
};
```

### Batch Flush Conditions
- Batch is full (1000 quads)
- Switching from colored to textured quads
- Switching to a different texture
- Manual `endBatch()` call

### Memory Management
- Dynamic VBO for vertex data
- Static IBO for indices (pre-calculated)
- Automatic cleanup in `Renderer.Shutdown()` 