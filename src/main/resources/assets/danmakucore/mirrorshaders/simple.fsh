#version 330 core

uniform sampler2D ourTexture;

uniform vec3 mainColor;
uniform vec3 secondaryColor;
uniform float coreSize;
uniform float coreHardness;
uniform float edgeHardness;
uniform float edgeGlow;

in vec3 oNormal;
in vec2 oTexCoord;
in vec3 viewVec;

out vec4 FragColor;

void main() {
    vec3 lightDir = normalize(vec3(0.4f, 0.2f, 0.5f));

    vec3 viewVecNorm = normalize(viewVec);
    vec3 normalNorm = normalize(oNormal);
    //FragColor = vec4((viewVecNorm + 1) / 2, 1F);
    //FragColor = vec4(viewVec, 1F);
    FragColor = vec4(vec3(dot(normalNorm, lightDir)), 1F);
    //FragColor = vec4(lightDir, 1.0f);
    //FragColor = texture(ourTexture, oTexCoord);
    //FragColor = vec4(oTexCoord, 1.0f, 1.0f);


}