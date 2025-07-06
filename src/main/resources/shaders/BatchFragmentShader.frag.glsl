#version 330 core
in vec4 vColor;
in vec2 vTexCoord;
layout(location = 0) out vec4 FragColor;

uniform sampler2D uTexture;
uniform bool uUseTexture;

void main() {
    if (uUseTexture) {
        // Sample texture and multiply by vertex color
        vec4 texColor = texture(uTexture, vTexCoord);
        FragColor = texColor * vColor;
    } else {
        // Use vertex color only
        FragColor = vColor;
    }
} 