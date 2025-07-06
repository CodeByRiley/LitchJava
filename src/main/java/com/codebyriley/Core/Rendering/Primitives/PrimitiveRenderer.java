package com.codebyriley.Core.Rendering.Primitives;

import com.codebyriley.Core.Rendering.BatchedRenderer;
import com.codebyriley.Util.Math.Vector2f;
import com.codebyriley.Util.Math.Vector3f;

/**
 * Provides basic primitive rendering using the BatchedRenderer.
 * Supports rectangles, circles, triangles, and lines.
 */
public class PrimitiveRenderer {
    private BatchedRenderer renderer;
    private int whiteTextureId;
    
    public PrimitiveRenderer(BatchedRenderer renderer, int whiteTextureId) {
        this.renderer = renderer;
        this.whiteTextureId = whiteTextureId;
    }
    
    /**
     * Draw a filled rectangle
     */
    public void drawRect(float x, float y, float width, float height, Vector3f color, float alpha) {
        drawRect(x, y, width, height, color, alpha, 0.0f);
    }
    
    /**
     * Draw a filled rectangle with rotation
     */
    public void drawRect(float x, float y, float width, float height, Vector3f color, float alpha, float rotation) {
        renderer.addQuad(
            x, y, width, height,
            0.0f, 0.0f, 1.0f, 1.0f, // Full texture UV
            color.x, color.y, color.z, alpha,
            whiteTextureId
        );
    }
    
    /**
     * Draw a rectangle outline
     */
    public void drawRectOutline(float x, float y, float width, float height, Vector3f color, float alpha, float thickness) {
        drawRectOutline(x, y, width, height, color, alpha, thickness, 0.0f);
    }
    
    /**
     * Draw a rectangle outline with rotation
     */
    public void drawRectOutline(float x, float y, float width, float height, Vector3f color, float alpha, float thickness, float rotation) {
        // Top edge
        drawRect(x, y + height - thickness, width, thickness, color, alpha, rotation);
        // Bottom edge
        drawRect(x, y, width, thickness, color, alpha, rotation);
        // Left edge
        drawRect(x, y + thickness, thickness, height - 2 * thickness, color, alpha, rotation);
        // Right edge
        drawRect(x + width - thickness, y + thickness, thickness, height - 2 * thickness, color, alpha, rotation);
    }
    
    /**
     * Draw a filled circle using multiple triangles
     */
    public void drawCircle(float centerX, float centerY, float radius, Vector3f color, float alpha) {
        drawCircle(centerX, centerY, radius, color, alpha, 32); // 32 segments by default
    }
    
    /**
     * Draw a filled circle with specified number of segments
     */
    public void drawCircle(float centerX, float centerY, float radius, Vector3f color, float alpha, int segments) {
        float angleStep = (float) (2.0 * Math.PI / segments);
        
        for (int i = 0; i < segments; i++) {
            float angle1 = i * angleStep;
            float angle2 = (i + 1) * angleStep;
            
            float x1 = centerX + (float) Math.cos(angle1) * radius;
            float y1 = centerY + (float) Math.sin(angle1) * radius;
            float x2 = centerX + (float) Math.cos(angle2) * radius;
            float y2 = centerY + (float) Math.sin(angle2) * radius;
            
            // Draw triangle fan segment
            drawTriangle(centerX, centerY, x1, y1, x2, y2, color, alpha);
        }
    }
    
    /**
     * Draw a circle outline
     */
    public void drawCircleOutline(float centerX, float centerY, float radius, Vector3f color, float alpha, float thickness) {
        drawCircleOutline(centerX, centerY, radius, color, alpha, thickness, 32);
    }
    
    /**
     * Draw a circle outline with specified number of segments
     */
    public void drawCircleOutline(float centerX, float centerY, float radius, Vector3f color, float alpha, float thickness, int segments) {
        float angleStep = (float) (2.0 * Math.PI / segments);
        
        for (int i = 0; i < segments; i++) {
            float angle1 = i * angleStep;
            float angle2 = (i + 1) * angleStep;
            
            float x1 = centerX + (float) Math.cos(angle1) * radius;
            float y1 = centerY + (float) Math.sin(angle1) * radius;
            float x2 = centerX + (float) Math.cos(angle2) * radius;
            float y2 = centerY + (float) Math.sin(angle2) * radius;
            
            // Draw line segment as thin rectangle
            drawLine(x1, y1, x2, y2, color, alpha, thickness);
        }
    }
    
    /**
     * Draw a triangle
     */
    public void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, Vector3f color, float alpha) {
        // Calculate center for rotation
        float centerX = (x1 + x2 + x3) / 3.0f;
        float centerY = (y1 + y2 + y3) / 3.0f;
        
        // For now, we'll use the batched renderer's quad method
        // In a more sophisticated implementation, you might want a dedicated triangle renderer
        float minX = Math.min(Math.min(x1, x2), x3);
        float minY = Math.min(Math.min(y1, y2), y3);
        float maxX = Math.max(Math.max(x1, x2), x3);
        float maxY = Math.max(Math.max(y1, y2), y3);
        
        float width = maxX - minX;
        float height = maxY - minY;
        
        // Draw bounding rectangle as approximation
        // TODO: Implement proper triangle rendering
        drawRect(centerX, centerY, width, height, color, alpha);
    }
    
    /**
     * Draw a line
     */
    public void drawLine(float x1, float y1, float x2, float y2, Vector3f color, float alpha, float thickness) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        float angle = (float) Math.atan2(dy, dx);
        
        float centerX = (x1 + x2) / 2.0f;
        float centerY = (y1 + y2) / 2.0f;
        
        drawRect(centerX, centerY, length, thickness, color, alpha, angle);
    }
    
    /**
     * Draw a point (small circle)
     */
    public void drawPoint(float x, float y, float size, Vector3f color, float alpha) {
        drawCircle(x, y, size / 2.0f, color, alpha);
    }
    
    /**
     * Draw a polygon from vertices
     */
    public void drawPolygon(Vector2f[] vertices, Vector3f color, float alpha) {
        if (vertices.length < 3) return;
        
        // Calculate center
        float centerX = 0, centerY = 0;
        for (Vector2f vertex : vertices) {
            centerX += vertex.x;
            centerY += vertex.y;
        }
        centerX /= vertices.length;
        centerY /= vertices.length;
        
        // Draw triangles fan
        for (int i = 1; i < vertices.length - 1; i++) {
            drawTriangle(
                centerX, centerY,
                vertices[i].x, vertices[i].y,
                vertices[i + 1].x, vertices[i + 1].y,
                color, alpha
            );
        }
    }
    
    /**
     * Draw a polygon outline
     */
    public void drawPolygonOutline(Vector2f[] vertices, Vector3f color, float alpha, float thickness) {
        if (vertices.length < 2) return;
        
        for (int i = 0; i < vertices.length; i++) {
            Vector2f current = vertices[i];
            Vector2f next = vertices[(i + 1) % vertices.length];
            drawLine(current.x, current.y, next.x, next.y, color, alpha, thickness);
        }
    }
} 