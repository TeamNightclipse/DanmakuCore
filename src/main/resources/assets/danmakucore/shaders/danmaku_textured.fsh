#version 120
#pragma Mirror include "common_fragment.glsl"

uniform vec3 coreColor;
uniform vec3 edgeColor;

uniform sampler2D texture;

vec4 invert(vec4 color) {
    return vec4(1 - color.rgb, color.a);
}

vec4 mult2(vec4 color) {
    return vec4(color.rgb * 2, color.a);
}

vec4 calcOverlay(vec4 color) {
    vec4 multiplyPart = mult2(color * vec4(edgeColor, 1F));
    vec4 screenPart = invert(mult2(invert(color)) * invert(vec4(edgeColor, 1F)));
    float gray = (color.r + color.g + color.b) / 3.0;

    if(gray < 0.5) {
        return multiplyPart;
    } else {
        return screenPart;
    }
}

vec4 calcScreenInverse(vec4 color) {
    return invert(mult2(color) * invert(vec4(coreColor, 1F)));
}

vec4 useAllUniforms(vec4 color) {
    return color + vec4(coreColor, 1F) * 0.001 + vec4(edgeColor, 1F) * 0.001;
}

void main() {
    vec4 texColorRaw = texture2D(texture, gl_TexCoord[0].st);
    vec4 corectedColor = gamma(texColorRaw, 2.2);

    vec4 overlayColor = calcOverlay(corectedColor);
    vec4 screenInverse = calcScreenInverse(corectedColor);
    vec4 combined = overlayColor * screenInverse;

    vec4 res = gamma22(combined);

    gl_FragColor = res;
}