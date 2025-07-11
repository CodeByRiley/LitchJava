package com.codebyriley.Core.Rendering.UI.Text;

import com.codebyriley.Core.Rendering.UIRenderer;
import com.codebyriley.Util.Log;
import com.codebyriley.Util.Math.Vector2f;
import com.codebyriley.Util.Math.Vector3f;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.opengl.GL43;
import static org.lwjgl.opengl.GL43.*;
import org.lwjgl.stb.STBTTAlignedQuad;

public class TextRenderer {
    private final TextBatchRenderer batchRenderer;
    private final UIRenderer renderer;
    private final STBTTBakedChar.Buffer charData;
    private final int textureId;
    private final int bitmapWidth, bitmapHeight;

    public TextRenderer(UIRenderer renderer, FontLoader fontLoader) {
        this.renderer = renderer;
        this.charData = fontLoader.getCharData();
        this.textureId = fontLoader.getTextureId();
        this.bitmapWidth = fontLoader.bitmapWidth;
        this.bitmapHeight = fontLoader.bitmapHeight;
        this.batchRenderer = new TextBatchRenderer();
    }

    // Main drawText (with scale)
    public void drawText(String text, float x, float y, Vector3f color, float alpha, float scale) {
        //System.out.println("Current VAO: " + glGetInteger(GL_VERTEX_ARRAY_BINDING));
        //System.out.println("Current Program: " + glGetInteger(GL_CURRENT_PROGRAM));
        //Log.info("Drawing text: " + text);
        batchRenderer.begin();
        float[] xPos = {x};
        float[] yPos = {y};
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                xPos[0] = x;
                yPos[0] += getLineHeight() * scale;
                continue;
            }
            if (c == ' ') {
                xPos[0] += getCharWidth(' ') * scale;
                continue;
            }
            if (c < 32 || c > 127) continue;
            STBTTAlignedQuad quad = STBTTAlignedQuad.malloc();
            STBTruetype.stbtt_GetBakedQuad(charData, bitmapWidth, bitmapHeight, c - 32, xPos, yPos, quad, false);
    
            // Correct quad position: add pen position to quad offsets
            float x0 = quad.x0();
            float y0 = quad.y0();
            float x1 = quad.x1();
            float y1 = quad.y1();
            float u0 = quad.s0();
            float v0 = quad.t0();
            float u1 = quad.s1();
            float v1 = quad.t1();
    
            batchRenderer.addCharQuad(x0, y0, x1, y1, u0, v0, u1, v1);
            quad.free();
        }
        // Use the font texture and color (alpha is handled in the shader)
        batchRenderer.end(textureId, color.x, color.y, color.z, alpha);
        //System.out.println("Current VAO: " + glGetInteger(GL_VERTEX_ARRAY_BINDING));
        //System.out.println("Current Program: " + glGetInteger(GL_CURRENT_PROGRAM));
    }

    // Overload for drawText (no scale, defaults to 1.0f)
    public void drawText(String text, float x, float y, Vector3f color, float alpha) {
        drawText(text, x, y, color, alpha, 1.0f);
    }

    // Centered text
    public void drawTextCentered(String text, float centerX, float centerY, Vector3f color, float alpha, float scale) {
        Vector2f textSize = getTextSize(text, scale);
        float x = centerX - textSize.x / 2.0f;
        float y = centerY - textSize.y / 2.0f + (textSize.y * 8000f);
        drawText(text, x, y, color, alpha, scale);
    }
    public void drawTextCentered(String text, float centerX, float centerY, Vector3f color, float alpha) {
        drawTextCentered(text, centerX, centerY, color, alpha, 1.0f);
    }

    // Right-aligned text
    public void drawTextRight(String text, float rightX, float y, Vector3f color, float alpha, float scale) {
        Vector2f textSize = getTextSize(text, scale);
        float x = rightX - textSize.x;
        drawText(text, x, y, color, alpha, scale);
    }
    public void drawTextRight(String text, float rightX, float y, Vector3f color, float alpha) {
        drawTextRight(text, rightX, y, color, alpha, 1.0f);
    }

    // Text size
    public Vector2f getTextSize(String text, float scale) {
        if (charData == null) return new Vector2f(0, 0);
        float maxWidth = 0, currentWidth = 0;
        float lineHeight = getLineHeight() * scale;
        int lineCount = 1;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                maxWidth = Math.max(maxWidth, currentWidth);
                currentWidth = 0;
                lineCount++;
                continue;
            }
            if (c == ' ') {
                currentWidth += getCharWidth(' ') * scale;
                continue;
            }
            if (c < 32 || c > 127) continue;
            currentWidth += charData.get(c - 32).xadvance() * scale;
        }
        maxWidth = Math.max(maxWidth, currentWidth);
        float totalHeight = lineHeight * lineCount;
        return new Vector2f(maxWidth, totalHeight);
    }
    public Vector2f getTextSize(String text) {
        return getTextSize(text, 1.0f);
    }

    public float getCharWidth(char c) {
        if (c < 32 || c > 127) return 0;
        return charData.get(c - 32).xadvance();
    }

    public float getLineHeight() {
        return charData.get(0).y1() - charData.get(0).y0();
    }
}