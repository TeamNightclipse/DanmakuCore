#version 120
#pragma include "common_fragment.glsl"

const float CORE_CONTRAST = 1.0;
const float EDGE_SIZE = 6.0;
const vec4 EDGE_COLOR = vec4(1.0, 1.0, 1.0, 1.0);

varying vec3 view;
varying vec3 normal;

vec3 core2Color(vec3 color) {
	return clamp(brightContrast(color, 0.5, 0.0), 0, 1);
}

vec4 coreGlow(float edgeFacing) {
	float coreGlowFacing = facing_ratio(view, normal, 0.3);

	float coreFacingRatio = brightContrast(coreGlowFacing, CORE_CONTRAST, CORE_CONTRAST * 2);
	vec4 glow = mix(vec4(core2Color(gl_Color.rgb), 0.2), gl_Color, coreFacingRatio);

	return glow;
}

vec4 edgeGlow(float edgeFacing) {
	float edgeGlowFacing = brightContrast(edgeFacing, EDGE_SIZE / -2.0, EDGE_SIZE);
	return mix(vec4(0.0), EDGE_COLOR * 3.0, gamma(edgeGlowFacing, 3.0));
}

void main() {
    float edgeFacing = facing_ratio(view, normal, 0.9);
    vec4 core = coreGlow(edgeFacing);
    vec4 edge = edgeGlow(edgeFacing);
	gl_FragColor = core + edge;
}