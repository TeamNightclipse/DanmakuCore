#version 120
#pragma include "common_fragment.glsl"

uniform vec3 coreColor;
uniform vec3 edgeColor;
uniform float coreContrast;
uniform float edgeSize;

varying vec3 view;
varying vec3 normal;

vec3 core2Color(vec3 color) {
	return clamp(brightContrast(color, 0.5, 0.0), 0, 1);
}

vec4 coreGlow(float edgeFacing) {
	float coreGlowFacing = facing_ratio(view, normal, 0.3);

	float coreFacingRatio = brightContrast(coreGlowFacing, coreContrast, coreContrast * 2);
	vec4 glow = mix(vec4(core2Color(coreColor), 0.2), vec4(coreColor, 1.0), coreFacingRatio);

	return glow;
}

vec4 edgeGlow(float edgeFacing) {
	float edgeGlowFacing = brightContrast(edgeFacing, edgeSize / -2.0, edgeSize);
	return mix(vec4(0.0), vec4(edgeColor, 1.0) * 3.0, gamma(edgeGlowFacing, 3.0));
}

void main() {
    float edgeFacing = facing_ratio(view, normal, 0.9);
    vec4 core = coreGlow(edgeFacing);
    vec4 edge = edgeGlow(edgeFacing);
	gl_FragColor = core + edge;
}