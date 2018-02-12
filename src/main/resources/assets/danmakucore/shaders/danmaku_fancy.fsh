#version 120
#pragma include "common_fragment.glsl"

const vec4 CORE_COLOR = vec4(1.0, 1.0, 1.0, 1.0);
const float CORE_SIZE = 1.1;
const float CORE_HARDNESS = 2.5;
const float EDGE_HARDNESS = 3.0;
const float EDGE_GLOW = 3.0;

varying vec3 view;
varying vec3 normal;

vec4 coreGlow() {
	//We try to get rid of the ugly edge
	float facing = facing_ratio(view, normal, 0.01);
	return mix(CORE_COLOR, vec4(0.0), facing);
}

float edgeDefine(float edgeFacing) {
	return brightContrast(edgeFacing, EDGE_HARDNESS / -2, EDGE_HARDNESS);
}

vec4 edgeGlow(float edgeFacing) {
	return mix(gl_Color * EDGE_GLOW, vec4(gl_Color.rgb, 0.0), edgeDefine(edgeFacing));
}

void main() {
	float edgeFacing = facing_ratio(view, normal, 0.9);
	float edgeFacingRatio = brightContrast(edgeFacing, CORE_SIZE * -1.0, CORE_HARDNESS);
	vec4 core = coreGlow();
	vec4 edge = edgeGlow(edgeFacing);
	gl_FragColor = mix(core, edge, edgeFacingRatio);
}
