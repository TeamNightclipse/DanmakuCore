# DanOBJ Format
DanmakuCore allows you to load a danmaku model from a .danobj file. A DanOBJ file is a modified OBJ file to support vertex data and some other stuff, while excluding what isn't needed for danmaku. A DanOBJ file is much stricter in what's optional (nothing atm), than a normal OBJ file.

While DanOBJ should be able to represent most danmaku, it's not going to be able to represent all danmaku.

## Structure
A DanOBJ file is structured somewhat similarly to an OBJ file. First comes the header, where the version, the danmaku marker color, and the glow marker color are defined. After that comes the geometry data itself. First comes the vertex data, then the UV data, the the color data, then the normal data, and finally the face data. The order of these are not optional. Each section is separated by a newline.

### The header
The header consists of a version, a danmaku marker color that represents a color that should be switched out with the color of the actual danmaku, a sequence of glow colors that represents colors that should glow, and a texture. Each of these are separated by a newline.
The version is a line beginning with `version`, then a space, and then a whole number.
The danmaku marker color is a line beginning with `danmakuColor`, then a space, and a whole number pointing to the color to use(in the color data section).
The glow marker color is a line beginning with `glowColor`, then a space, and a sequence of whole number pointing to the color to use(in the color data section). Each number is separated by a space.
The texture is a normal resource path as used in texture pack json files. The texture should point to a .png file. It starts with the word `texture`, then a space, and then the resource location. If no texture is present, `danmakucore:textures/entity/danmalu/White.png` should be used.

### The Vertex data
The vertex data are lines starting with `v` followed by three decimal numbers, which corresponds the the X, Y and Z coordinates. Each element is separated by a space.
Example: `v 1.2 1.5 1.6`

### The UV data
The UV data are lines starting with `vt` followed by two decimal numbers ranging from 0 to 1, which corresponds the the U and V coordinates. Each element is separated by a space.
Example: `vt 0.2 0.5`

### The Color data
The color data are lines starting with `vc` followed by four decimal numbers ranging from 0 to 1, which corresponds the the red, green, blue, and alpha values. Each element is separated by a space.
Example: `vc 0.0 0.0 1.0 1.0`

### The Normal data
The normal data are lines starting with `vn` followed by three decimal numbers, which corresponds the the X, Y and Z coordinates. Each element is separated by a space.
Example: `vn 0.0 1.0 0.0`

### The Face data
The face data are lines starting with `f` followed by whole number specifying the vertex data to use, then a number specifying the UV data to use, then a number specifying the color data to use, then a number specifying the normal data to use. The numbers are separated by a forward slash(`/`). After that follows a space, and the whole thing is repeated two more times. On time for each vertex that the face is composed if. The amount of vertices for a face must be exactly three.
Example: `f 0/0/5/0 1/1/5/0 2/2/5/0`