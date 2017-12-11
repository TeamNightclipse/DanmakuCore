#version 120

uniform vec3 overwriteColor;
uniform vec3 realColor;

void main() {

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    if(gl_Color.rgb == overwriteColor) {
    	gl_FrontColor = vec4(realColor, gl_Color.a);
    }
    else {
    	gl_FrontColor = vec4(gl_Color.rgb, gl_Color.a);
    }
    gl_TexCoord[0] = gl_MultiTexCoord0;
}