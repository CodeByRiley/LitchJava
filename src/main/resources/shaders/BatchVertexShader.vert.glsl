#version 450 core

in vec2 aPos;
in vec4 aColor;
in vec2 aTexCoord;
in vec4 aRotation; // centerX, centerY, cosA, sinA

uniform vec2 uWindowSize;

out vec4 vColor;
out vec2 vTexCoord;

void main() {
    // Apply rotation if needed (when rotation is not identity)
    vec2 finalPos = aPos;
    if (aRotation.z != 1.0 || aRotation.w != 0.0) { // cosA != 1.0 or sinA != 0.0
        vec2 center = aRotation.xy;
        vec2 offset = aPos - center;
        float cosA = aRotation.z;
        float sinA = aRotation.w;
        finalPos = center + vec2(
            offset.x * cosA - offset.y * sinA,
            offset.x * sinA + offset.y * cosA
        );
    }
    
    // Convert from screen coordinates to NDC
    float ndc_x = (finalPos.x / uWindowSize.x) * 2.0 - 1.0;
    float ndc_y = 1.0 - (finalPos.y / uWindowSize.y) * 2.0;
    
    gl_Position = vec4(ndc_x, ndc_y, 0.0, 1.0);
    
    vColor = aColor;
    vTexCoord = aTexCoord;
} 