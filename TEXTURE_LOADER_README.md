# Texture Loader Usage Guide

This guide explains how to use the `TextureLoader` class with the base `Texture` class in the Litch engine.

## Overview

The `TextureLoader` class provides methods to load image files and create `Texture` objects that can be used for rendering. It uses STB (stb_image) for image decoding and supports common formats like PNG, JPG, BMP, etc.

## Basic Usage

### 1. Loading a Texture

```java
// Basic texture loading with default settings
Texture myTexture = TextureLoader.LoadTexture("textures/my_image.png");

// Loading with custom filtering
Texture myTexture = TextureLoader.LoadTexture("textures/my_image.png", GL_LINEAR, GL_NEAREST);
```

### 2. Using the Texture

```java
// Bind the texture for rendering
myTexture.bind();

// Or bind to a specific texture unit
myTexture.bind(0); // Bind to texture unit 0

// Check if texture is valid
if (myTexture.isValid()) {
    // Use the texture
    System.out.println("Texture size: " + myTexture.mWidth + "x" + myTexture.mHeight);
    System.out.println("Aspect ratio: " + myTexture.getAspectRatio());
}

// Unbind when done
myTexture.unbind();
```

### 3. Using with the Renderer

```java
// In your scene's Draw method
renderer.beginBatch();

// Draw textured quads using batching
renderer.drawTexturedQuadBatch(x, y, width, height, myTexture.mId, r, g, b, a);

renderer.endBatch();
```

## Texture File Requirements

1. **File Format**: Supported formats include PNG, JPG, BMP, TGA, and more
2. **Location**: Place texture files in `src/main/resources/textures/` directory
3. **Path**: Use relative paths from resources, e.g., `"textures/player.png"`

## Advanced Usage

### Custom Filtering

```java
// Linear filtering (smooth)
Texture smoothTexture = TextureLoader.LoadTexture("textures/smooth.png", GL_LINEAR, GL_LINEAR);

// Nearest neighbor filtering (pixelated)
Texture pixelatedTexture = TextureLoader.LoadTexture("textures/pixelated.png", GL_NEAREST, GL_NEAREST);

// Mipmap filtering
Texture mipmapTexture = TextureLoader.LoadTexture("textures/mipmap.png", GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR);
```

### Manual Texture Management

```java
// Bind/unbind textures manually
TextureLoader.BindTexture(myTexture);
// ... render with texture ...
TextureLoader.UnbindTexture();

// Delete texture when no longer needed
TextureLoader.DeleteTexture(myTexture);
// or
myTexture.delete();
```

## Example Scene

The `TextureDemoScene` demonstrates:
- Loading textures programmatically
- Using textures with the batching system
- Proper texture cleanup
- Error handling for failed loads

## Key Features

1. **Automatic Format Detection**: STB automatically detects image format
2. **Memory Management**: Proper cleanup of OpenGL resources
3. **Error Handling**: Comprehensive error messages for debugging
4. **Batching Support**: Works seamlessly with the quad batching system
5. **Flexible Filtering**: Support for various texture filtering modes

## Performance Tips

1. **Reuse Textures**: Load textures once and reuse them
2. **Use Batching**: Group textured quads together for better performance
3. **Clean Up**: Always delete textures when they're no longer needed
4. **Power of Two**: Use power-of-two dimensions for best performance (though not required)

## Troubleshooting

### Common Issues

1. **File Not Found**: Ensure texture files are in the correct resources directory
2. **Invalid Format**: Check that the image file is not corrupted
3. **Memory Issues**: Make sure to delete textures when done using them
4. **Rendering Issues**: Ensure textures are bound before rendering

### Debug Information

The loader prints information about loaded textures:
```
Loaded texture: textures/my_image.png (256x256)
```

Error messages will help identify issues:
```
Failed to load texture: textures/missing.png - Resource not found
``` 