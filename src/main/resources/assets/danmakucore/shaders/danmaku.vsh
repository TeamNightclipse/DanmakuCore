#version 120

uniform vec3 overwriteColorEdge;
uniform vec3 overwriteColorCore;
uniform vec3 coreColor;
uniform vec3 edgeColor;

void main() {

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    if(gl_Color.rgb == overwriteColorEdge) {
    	gl_FrontColor = vec4(edgeColor, gl_Color.a);
    }
    else if(gl_Color.rgb == overwriteColorCore) {
    	gl_FrontColor = vec4(coreColor, gl_Color.a);
    }
    else {
    	gl_FrontColor = gl_Color;
    }
    gl_TexCoord[0] = gl_MultiTexCoord0;
}