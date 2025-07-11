#version 330 core
layout(location = 0) in vec2 aPos;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoord;
layout(location = 3) in int aTexIndex;

uniform vec2 uWindowSize;

out vec4 vColor;
out vec2 vTexCoord;
flat out int vTexIndex;

void main() {
    // Convert from screen coordinates to NDC
    float ndc_x = (aPos.x / uWindowSize.x) * 2.0 - 1.0;
    float ndc_y = 1.0 - (aPos.y / uWindowSize.y) * 2.0;
    
    gl_Position = vec4(ndc_x, ndc_y, 0.0, 1.0);
    
    vColor = aColor;
    vTexCoord = aTexCoord;
    vTexIndex = aTexIndex;
} 