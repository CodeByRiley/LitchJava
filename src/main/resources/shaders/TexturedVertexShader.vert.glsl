#version 330 core
layout(location = 0) in vec2 aPos;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoord;
layout(location = 3) in vec4 aRotation; // centerX, centerY, cosA, sinA

uniform vec2 uWindowSize;

out vec2 TexCoord;
out vec4 vColor;

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
    TexCoord = aTexCoord;
    vColor = aColor;
}