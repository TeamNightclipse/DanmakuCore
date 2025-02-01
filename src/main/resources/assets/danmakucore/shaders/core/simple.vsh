#version 330 core

uniform mat4 modelMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform float ticks;

in layout (location = 0) vec3 position;
in layout (location = 1) vec3 iNormal;
in layout (location = 2) vec2 iTexCoord;

//in layout (location = 2) mat4 modelMat;

out vec3 oNormal;
out vec2 oTexCoord;
out vec3 viewVec;

mat4 mat4Id() {
    mat4 ret = mat4(0);
    ret[0][0] = 1.0f;
    ret[1][1] = 1.0f;
    ret[2][2] = 1.0f;
    ret[3][3] = 1.0f;
    return ret;
}

void main() {
    float sinTicks = sin((ticks * 24000) / 8);
    vec4 worldPos = modelMatrix * vec4(position, 1.0);
    vec4 viewPos = modelViewMatrix * worldPos;

    gl_Position = projectionMatrix * viewPos;
    oNormal = iNormal;
    oTexCoord = iTexCoord;
    viewVec = viewPos.xyz;
}