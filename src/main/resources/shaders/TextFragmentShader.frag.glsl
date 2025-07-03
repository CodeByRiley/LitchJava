#version 450 core
in vec2 TexCoord;
out vec4 FragColor;
uniform sampler2D uFontAtlas;
uniform vec3 uTextColor;
void main() {
    float alpha = texture(uFontAtlas, TexCoord).r;
    FragColor = vec4(uTextColor, alpha);
} 