#version 330 core
in vec4 vColor;
in vec2 vTexCoord;
flat in int vTexIndex;
layout(location = 0) out vec4 FragColor;

uniform sampler2D uTexture0;
uniform sampler2D uTexture1;
uniform sampler2D uTexture2;
uniform sampler2D uTexture3;
uniform sampler2D uTexture4;
uniform sampler2D uTexture5;
uniform sampler2D uTexture6;
uniform sampler2D uTexture7;
uniform sampler2D uTexture8;
uniform sampler2D uTexture9;
uniform sampler2D uTexture10;
uniform sampler2D uTexture11;
uniform sampler2D uTexture12;
uniform sampler2D uTexture13;
uniform sampler2D uTexture14;
uniform sampler2D uTexture15;
uniform bool uUseTexture;

void main() {
    if (uUseTexture) {
        // Sample texture from the appropriate slot and multiply by vertex color
        vec4 texColor;
        if (vTexIndex == 0) texColor = texture(uTexture0, vTexCoord);
        else if (vTexIndex == 1) texColor = texture(uTexture1, vTexCoord);
        else if (vTexIndex == 2) texColor = texture(uTexture2, vTexCoord);
        else if (vTexIndex == 3) texColor = texture(uTexture3, vTexCoord);
        else if (vTexIndex == 4) texColor = texture(uTexture4, vTexCoord);
        else if (vTexIndex == 5) texColor = texture(uTexture5, vTexCoord);
        else if (vTexIndex == 6) texColor = texture(uTexture6, vTexCoord);
        else if (vTexIndex == 7) texColor = texture(uTexture7, vTexCoord);
        else if (vTexIndex == 8) texColor = texture(uTexture8, vTexCoord);
        else if (vTexIndex == 9) texColor = texture(uTexture9, vTexCoord);
        else if (vTexIndex == 10) texColor = texture(uTexture10, vTexCoord);
        else if (vTexIndex == 11) texColor = texture(uTexture11, vTexCoord);
        else if (vTexIndex == 12) texColor = texture(uTexture12, vTexCoord);
        else if (vTexIndex == 13) texColor = texture(uTexture13, vTexCoord);
        else if (vTexIndex == 14) texColor = texture(uTexture14, vTexCoord);
        else texColor = texture(uTexture15, vTexCoord);
        
        FragColor = texColor * vColor;
    } else {
        // Use vertex color only
        FragColor = vColor;
    }
} 