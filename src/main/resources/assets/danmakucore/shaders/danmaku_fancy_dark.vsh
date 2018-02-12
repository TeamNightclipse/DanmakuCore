#version 120
#pragma include "common_vertex.glsl"

uniform vec3 overwriteColor;
uniform vec3 realColor;

varying vec3 view;
varying vec3 normal;

void main() {
	view = viewVec();
	normal = gl_Normal;

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    if(gl_Color.rgb == overwriteColor) {
    	gl_FrontColor = vec4(realColor, gl_Color.a);
    }
    else {
    	gl_FrontColor = vec4(gl_Color.rgb, gl_Color.a);
    }
    gl_TexCoord[0] = gl_MultiTexCoord0;
}