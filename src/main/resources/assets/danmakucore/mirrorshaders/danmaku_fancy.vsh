#version 150
#pragma Mirror include "common_vertex.glsl"

uniform mat4 projectionMatrix;

in layout (location = 0) vec3 position;
inout layout (location = 1) vec3 normal;
in layout (location = 2) mat4 modelViewMatrix;

inout layout (location = 6) vec3 mainColor;
inout layout (location = 7) vec3 secondaryColor;
inout layout (location = 8) float coreSize;
inout layout (location = 9) float coreHardness;
inout layout (location = 10) float edgeHardness;
inout layout (location = 11) float edgeGlow;

out vec3 view;

void main() {
    view = position;
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
    gl_TexCoord[0] = gl_MultiTexCoord0;
}