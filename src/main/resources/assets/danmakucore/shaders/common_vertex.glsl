#pragma Mirror include "common.glsl"

vec3 viewVec() {
	vec3 normal = gl_Normal;
    vec3 cameraPos = inverse(gl_ModelViewMatrix)[3].xyz;
    return normalize(cameraPos - gl_Vertex.xyz);
}