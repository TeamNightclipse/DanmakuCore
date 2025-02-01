#version 330

#moj_import <danmakucore:common_vertex.glsl>

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

in layout (location = 0) vec3 Position;
in layout (location = 1) vec3 Normal;

in layout (location = 2) mat4 ModelViewMatrixIn;

in layout (location = 6) vec4 MainColor;
in layout (location = 7) vec4 SecondaryColor;
in layout (location = 8) float CoreSize;
in layout (location = 9) float CoreHardness;
in layout (location = 10) float EdgeHardness;
in layout (location = 11) float EdgeGlow;

out vec3 view;

out ExtraParams {
    vec4 MainColor;
    vec4 SecondaryColor;
    float CoreSize;
    float CoreHardness;
    float EdgeHardness;
    float EdgeGlow;
} extraParams;

out vec3 InterpolatedNormal;

void main() {
    view = normalize(inverse(ModelViewMatrixIn)[3].xyz - Position);
    InterpolatedNormal = Normal;
    extraParams.MainColor = MainColor;
    extraParams.SecondaryColor = SecondaryColor;
    extraParams.CoreSize = CoreSize;
    extraParams.CoreHardness = CoreHardness;
    extraParams.EdgeHardness = EdgeHardness;
    extraParams.EdgeGlow = EdgeGlow;

    gl_Position = ProjMat * ModelViewMatrixIn * vec4(Position, 1.0);
}
