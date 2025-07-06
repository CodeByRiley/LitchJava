#version 330 core
in vec2 TexCoord;
out vec4 FragColor;
uniform sampler2D uFontAtlas;
uniform vec3 uTextColor;
uniform float uTextAlpha;
void main() {
    float alpha = texture(uFontAtlas, TexCoord).a;
    FragColor = vec4(uTextColor, alpha * uTextAlpha);
}