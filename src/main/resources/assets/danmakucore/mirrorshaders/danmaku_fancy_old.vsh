#version 150
#pragma Mirror include "common_vertex.glsl"

inout layout (location = 0) mat4 modelViewMat;
inout layout (location = 4) vec3 mainColor;
inout layout (location = 5) vec3 secondaryColor;
inout layout (location = 6) float coreSize;
inout layout (location = 7) float coreHardness;
inout layout (location = 8) float edgeHardness;
inout layout (location = 9) float edgeGlow;

out vec3 view;
out vec3 normal;

void main() {
    view = viewVec();
    normal = gl_Normal;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_TexCoord[0] = gl_MultiTexCoord0;


}