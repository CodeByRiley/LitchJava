#version 330 core
layout(location = 0) in vec2 aPos;
layout(location = 1) in vec2 aTexCoord;
out vec2 TexCoord;
uniform vec2 uWindowSize;
void main() {
    float ndc_x = (aPos.x / uWindowSize.x) * 2.0 - 1.0;
    float ndc_y = 1.0 - (aPos.y / uWindowSize.y) * 2.0;
    gl_Position = vec4(ndc_x, ndc_y, 0.0, 1.0);
    TexCoord = aTexCoord;
}