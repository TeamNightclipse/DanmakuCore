#version 330

#moj_import <danmakucore:common_fragment.glsl>

in vec3 InterpolatedNormal;
in vec3 view;

in ExtraParams {
    vec4 MainColor;
    vec4 SecondaryColor;
    float CoreSize;
    float CoreHardness;
    float EdgeHardness;
    float EdgeGlow;
} extraParams;

uniform vec4 ColorModulator;

out vec4 fragColor;

vec4 coreGlow() {
    //We try to get rid of the ugly edge
    float facing = facing_ratio(view, InterpolatedNormal, 0.01);
    return mix(vec4(extraParams.MainColor.xyz, 1.0), vec4(0.0), facing);
}

float edgeDefine(float edgeFacing) {
    return brightContrast(edgeFacing, extraParams.EdgeHardness / -2, extraParams.EdgeHardness);
}

vec4 mixEdgeGlow(float edgeFacing) {
    return mix(vec4(extraParams.SecondaryColor.xyz, 1.0) * extraParams.EdgeGlow, vec4(extraParams.SecondaryColor.xyz, 0.0), edgeDefine(edgeFacing));
}

void main() {
    float edgeFacing = facing_ratio(view, InterpolatedNormal, 0.9);
    float edgeFacingRatio = brightContrast(edgeFacing, extraParams.CoreSize * -1.0, extraParams.CoreHardness);
    vec4 core = coreGlow();
    vec4 edge = mixEdgeGlow(edgeFacing);
    fragColor = mix(core, edge, edgeFacingRatio);

    //fragColor = vec4(vec3(edgeFacing), 1.0);
}