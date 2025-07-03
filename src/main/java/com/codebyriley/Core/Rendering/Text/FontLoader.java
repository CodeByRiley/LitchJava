package com.codebyriley.Core.Rendering.Text;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;


public class FontLoader {
    private int textureId;
    private STBTTBakedChar.Buffer charData;

    public final int bitmapWidth;
    public final int bitmapHeight;

    public FontLoader(String fontPath, int fontSize) {
        this.bitmapWidth = calculateAtlasSize(fontSize, 96, 2);
        this.bitmapHeight = calculateAtlasSize(fontSize, 96, 2);
        ByteBuffer font = null;
        try {
            font = ioResourceToByteBuffer(fontPath, 512 * 1024);
            if (font == null) {
                throw new IOException("Font file not found: " + fontPath);
            }
            System.out.println("Loaded font from: " + fontPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load font: " + fontPath, e);
        }
        ByteBuffer bitmap = ByteBuffer.allocateDirect(bitmapWidth * bitmapHeight);
        
        charData = STBTTBakedChar.malloc(96);

        STBTruetype.stbtt_BakeFontBitmap(font, fontSize, bitmap, bitmapWidth, bitmapHeight, 32, charData);

        for (int i = 0; i < 16; i++) {
            System.out.print((bitmap.get(i) & 0xFF) + " ");
        }
        System.out.println();

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, bitmapWidth, bitmapHeight, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getTextureId() {
        return textureId;
    }

    public STBTTBakedChar.Buffer getCharData() {
        return charData;
    }

    public int calculateAtlasSize(int fontSize, int numGlyphs, int padding) {
        int pNumGlyphs = numGlyphs; // ASCII 32-127
        int pPadding = padding;
        int pGlyphsPerRow = (int)Math.ceil(Math.sqrt(numGlyphs));
        int pRawSize = pGlyphsPerRow * (fontSize + padding);
        int pAtlasSize = nextPowerOfTwo(pRawSize);
        System.out.println("Calculated atlas size: " + pAtlasSize);
        return pAtlasSize;
    }

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

    // Utility to load a file into a ByteBuffer
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

}
