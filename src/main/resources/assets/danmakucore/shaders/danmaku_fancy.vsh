#version 120
#pragma include "common_vertex.glsl"

varying vec3 view;
varying vec3 normal;

void main() {
	view = viewVec();
	normal = gl_Normal;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_TexCoord[0] = gl_MultiTexCoord0;
}