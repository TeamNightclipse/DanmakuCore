#version 150
#pragma Mirror include "common_fragment.glsl"

in mat4 modelViewMat;
in vec3 mainColor;
in vec3 secondaryColor;
in float coreSize;
in float coreHardness;
in float edgeHardness;
in float edgeGlow;

in vec3 view;
in vec3 normal;

vec4 coreGlow() {
    //We try to get rid of the ugly edge
    float facing = facing_ratio(view, normal, 0.01);
    return mix(vec4(mainColor, 1.0), vec4(0.0), facing);
}

float edgeDefine(float edgeFacing) {
    return brightContrast(edgeFacing, edgeHardness / -2, edgeHardness);
}

vec4 mixEdgeGlow(float edgeFacing) {
    return mix(vec4(secondaryColor, gl_Color.a) * edgeGlow, vec4(secondaryColor, 0.0), edgeDefine(edgeFacing));
}

void main() {
    float edgeFacing = facing_ratio(view, normal, 0.9);
    float edgeFacingRatio = brightContrast(edgeFacing, coreSize * -1.0, coreHardness);
    vec4 core = coreGlow();
    vec4 edge = mixEdgeGlow(edgeFacing);
    gl_FragColor = mix(core, edge, edgeFacingRatio);
}
