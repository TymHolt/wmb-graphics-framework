#version 330 core

layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTextureUV;
layout (location = 2) in vec3 aNormal;

uniform mat4 uView;
uniform mat4 uProjection;
uniform mat4 uRotation;
uniform mat4 uTransform;

out vec2 pTextureUV;
out vec3 pNormal;

void main() {
    mat4 cameraMatrix = uProjection * uView;
    gl_Position = cameraMatrix * uTransform * vec4(aPosition, 1.0);
    pTextureUV = aTextureUV;
    pNormal = (uRotation * vec4(aNormal, 0.0)).xyz;
}