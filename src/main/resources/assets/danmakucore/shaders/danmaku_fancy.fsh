#version 120
#pragma include "common_fragment.glsl"

uniform vec3 coreColor;
uniform vec3 edgeColor;
uniform float coreSize;
uniform float coreHardness;
uniform float edgeHardness;
uniform float edgeGlow;

varying vec3 view;
varying vec3 normal;

vec4 coreGlow() {
	//We try to get rid of the ugly edge
	float facing = facing_ratio(view, normal, 0.01);
	return mix(vec4(coreColor, 1.0), vec4(0.0), facing);
}

float edgeDefine(float edgeFacing) {
	return brightContrast(edgeFacing, edgeHardness / -2, edgeHardness);
}

vec4 mixEdgeGlow(float edgeFacing) {
	return mix(vec4(edgeColor, gl_Color.a) * edgeGlow, vec4(edgeColor, 0.0), edgeDefine(edgeFacing));
}

void main() {
	float edgeFacing = facing_ratio(view, normal, 0.9);
	float edgeFacingRatio = brightContrast(edgeFacing, coreSize * -1.0, coreHardness);
	vec4 core = coreGlow();
	vec4 edge = mixEdgeGlow(edgeFacing);
	gl_FragColor = mix(core, edge, edgeFacingRatio);
}
