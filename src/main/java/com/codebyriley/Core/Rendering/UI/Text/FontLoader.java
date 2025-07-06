package com.codebyriley.Core.Rendering.UI.Text;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.lwjgl.opengl.GL33.*;
import com.codebyriley.Util.Log;

/**
 * Loads a font from resources, bakes a glyph atlas, and creates an OpenGL texture.
 * Uses stb_truetype for robust font baking. Supports dumping the atlas for debugging.
 */
public class FontLoader {
    private int textureId;
    private STBTTBakedChar.Buffer charData;
    public final int bitmapWidth;
    public final int bitmapHeight;

    /**
     * Create a FontLoader with a recommended atlas size for the given font size and glyph count.
     */
    public static FontLoader create(String fontPath, int fontSize, int glyphCount) {
        int atlasSize = recommendedAtlasSize(fontSize, glyphCount);
        return new FontLoader(fontPath, fontSize, atlasSize, atlasSize, glyphCount);
    }

    /**
     * Main constructor. Loads font, bakes atlas, creates OpenGL texture.
     * @param fontPath Path to font file in resources (e.g. "fonts/DejaVuSerif.ttf")
     * @param fontSize Font size in pixels
     * @param bitmapWidth Atlas width
     * @param bitmapHeight Atlas height
     * @param glyphCount Number of glyphs to bake (usually 96 for ASCII 32-127)
     */
    public FontLoader(String fontPath, int fontSize, int bitmapWidth, int bitmapHeight, int glyphCount) {
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
        ByteBuffer font = null;
        try {
            font = ioResourceToByteBuffer(fontPath, 512 * 1024);
            if (font == null || font.remaining() == 0) {
                throw new IOException("Font file not found or empty: " + fontPath);
            }
            Log.info("Loaded font from: " + fontPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load font: " + fontPath, e);
        }
        ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmapWidth * bitmapHeight);
        charData = STBTTBakedChar.malloc(glyphCount);
        int bakeResult = STBTruetype.stbtt_BakeFontBitmap(
            font, fontSize, bitmap, bitmapWidth, bitmapHeight, 32, charData
        );
        if (bakeResult <= 0) {
            Log.error("Font baking failed for: " + fontPath + " (result=" + bakeResult + ")");
        } else {
            Log.info("Font baking succeeded, baked " + bakeResult + " glyphs.");
        }
        // Debug: print first 16 bytes
        for (int i = 0; i < 16; i++) {
            Log.debug("Bitmap data[" + i + "]: " + (bitmap.get(i) & 0xFF));
        }
        bitmap.rewind();
        // Convert the bitmap to RGBA format (white RGB, alpha from baked value)
        ByteBuffer rgbaBitmap = BufferUtils.createByteBuffer(bitmapWidth * bitmapHeight * 4);
        for (int i = 0; i < bitmapWidth * bitmapHeight; i++) {
            byte value = bitmap.get(i);
            rgbaBitmap.put((byte)255).put((byte)255).put((byte)255).put(value); // R, G, B, A
        }
        rgbaBitmap.flip();
        // Create OpenGL texture
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmapWidth, bitmapHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, rgbaBitmap);
        glBindTexture(GL_TEXTURE_2D, textureId);
        Log.info("Font atlas texture created: " + textureId + " (" + bitmapWidth + "x" + bitmapHeight + ")");
        dumpAtlasToPNG("fonts/font_atlas.png");
    }

    /**
     * Get the OpenGL texture ID for the font atlas.
     */
    public int getTextureId() {
        return textureId;
    }

    /**
     * Get the baked char data for glyph layout.
     */
    public STBTTBakedChar.Buffer getCharData() {
        return charData;
    }

    /**
     * Utility: Calculate a recommended atlas size for a given font size and glyph count.
     */
    public static int recommendedAtlasSize(int fontSize, int glyphCount) {
        int glyphsPerRow = (int)Math.ceil(Math.sqrt(glyphCount));
        int rawSize = glyphsPerRow * (fontSize + 2); // +2 for padding
        return nextPowerOfTwo(rawSize);
    }

    /**
     * Utility: Next power of two for OpenGL textures.
     */
    public static int nextPowerOfTwo(int x) {
        if (x <= 0) return 1;
        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }

    /**
     * Utility: Load a file from resources into a ByteBuffer.
     */
    private static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        try (InputStream source = FontLoader.class.getClassLoader().getResourceAsStream(resource)) {
            if (source == null)
                throw new IOException("Resource not found: " + resource);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = source.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            ByteBuffer byteBuffer = BufferUtils.createByteBuffer(out.size());
            byteBuffer.put(out.toByteArray());
            byteBuffer.flip();
            return byteBuffer;
        }
    }

    /**
     * Utility: Dump the font atlas to a PNG file for debugging (optional).
     */
    public void dumpAtlasToPNG(String filename) {
        // Only for debugging: dump the alpha channel as grayscale PNG
        ByteBuffer pixels = BufferUtils.createByteBuffer(bitmapWidth * bitmapHeight);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glGetTexImage(GL_TEXTURE_2D, 0, GL_ALPHA, GL_UNSIGNED_BYTE, pixels);
        glBindTexture(GL_TEXTURE_2D, 0);
        // Use your favorite PNG writer here, e.g. STBImageWrite.stbi_write_png
        STBImageWrite.stbi_write_png("font_atlas.pngth", bitmapWidth, bitmapHeight, 1, pixels, bitmapWidth);
    }
}
