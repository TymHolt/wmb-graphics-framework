#version 330 core

in vec2 pTextureUV;
in vec3 pNormal;

uniform vec3 uLightDirection;
uniform float uAmbienceFactor;
uniform vec4 uColor;
uniform sampler2D uTexture;
uniform int uRenderMode;

out vec4 oFragColor;

void main() {
    vec4 albedoColor = vec4(0.0, 0.0, 0.0, 1.0);
    switch (uRenderMode) {
        case 0: // COLORED
            albedoColor = uColor;
            break;

        case 1: // TEXTURED
            albedoColor = texture(uTexture, pTextureUV);
            break;

        case 2: // MIXED
            vec4 texColor = texture(uTexture, pTextureUV);
            albedoColor = texColor * uColor;
            break;
    }

    float diffuseFactor = max(-dot(normalize(pNormal), normalize(uLightDirection)), 0.0);
    float shadeFactor = clamp(diffuseFactor + uAmbienceFactor, 0.0, 1.0);
    oFragColor = vec4(albedoColor.xyz * shadeFactor, albedoColor.w);
}