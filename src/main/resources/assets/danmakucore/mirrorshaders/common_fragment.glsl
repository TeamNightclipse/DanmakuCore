#pragma Mirror include "common.glsl"

//https://github.com/dfelinto/blender/blob/master/intern/cycles/kernel/shaders/node_brightness.osl
float brightContrast(float value, float brightness, float contrast) {
    float a = 1.0 + contrast;
    float b = brightness - contrast * 0.5;

    return max(a * value + b, 0.0);
}

vec3 brightContrast(vec3 color, float brightness, float contrast) {
    float a = 1.0 + contrast;
    float b = brightness - contrast * 0.5;

    return max(a * color + b, 0.0);
}

vec4 brightContrast(vec4 color, float brightness, float contrast) {
    return vec4(brightContrast(color.rgb, brightness, contrast), color.a);
}

float gamma(float value, float gam) {
    return pow(value, gam);
}

vec3 gamma(vec3 color, float value) {
    return pow(color.rgb, vec3(value));
}

vec4 gamma(vec4 color, float value) {
    return vec4(gamma(color.rgb, value), color.a);
}

float gamma22(float value) {
    return gamma(value, 1/2.2);
}

vec3 gamma22(vec3 color) {
    return gamma(color.rgb, 1/2.2);
}

vec4 gamma22(vec4 color) {
    return gamma(color, 1/2.2);
}