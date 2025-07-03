#version 450 core
in vec2 aPos;
out vec2 TexCoord;
uniform vec2 uPos;
uniform vec2 uSize;
uniform vec2 uWindowSize;
void main() {
    vec2 scaled = aPos * uSize + uPos;
    float ndc_x = (scaled.x / uWindowSize.x) * 2.0 - 1.0;
    float ndc_y = 1.0 - (scaled.y / uWindowSize.y) * 2.0;
    gl_Position = vec4(ndc_x, ndc_y, 0.0, 1.0);
    TexCoord = vec2(aPos.x, 1.0 - aPos.y);
}