# DanmakuCore DanModel Specification

The DanmakuCore DanModel format is a binary model format used by DanmakuCore for storing custom form models efficiently, with little leeway for what is allowed, and what isn't.

A string in this format is defined as a short, defining the length of the bytes, followed by UTF-8 encoded bytes. 
A boolean is represented as a byte where any value other than 0 represents true.

All DanModel files a required to start with the string "DanmakuCore DanModel". After that, is the version of the format, specified as a short. The current version is 1.

After that is the model description, which consists of strings. The description values are required, but the strings can be empty.
First is the model name, then the model description, and last the model author.

After this, the main part of the model starts.
First is an int specifying the amount of pieces the model is divided up into. A piece is defined as a piece of model data that can stand and be rendered on it's own.
Then is the model length in bytes, which is specified as an int.
After that, the specified amount of pieces follow.

A piece consists of (in order)
* Int: A glMode specifying how to render the vertices. Valid glModes a specified below.
* Int: Vertices. The amount of vertices contained within.
* Boolean: If this is a danmaku colored vertex.
* The specified amount of vertices.

A vertex consists of (in order)
* Double: x coordinate
* Double: y coordinate
* Double: z coordinate
* Double: u texture coordinate
* Double: v texture coordinate
* Float: Red color. Only present if not a danmaku colored vertex.
* Float: Green color. Only present if not a danmaku colored vertex.
* Float: Blue color. Only present if not a danmaku colored vertex.
* Float: Alpha color. Only present if not a danmaku colored vertex.
* Float: Normal x.
* Float: Normal y.
* Float: Normal z.

Descriptions from here: https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glBegin.xml
Valid glModes:
| Name               | Int representation | Description                                                                                                                                                                                                                                                                                                                                                                                    |
|--------------------|--------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| GL_POINTS          | 0x0                | Treats each vertex as a single point.Vertex `n` defines point `n`. `N` points are drawn.                                                                                                                                                                                                                                                                                                       |
| GL_LINES           | 0x1                | Treats each pair of vertices as an independent line segment.Vertices `2n - 1` and `2n` define line `n`. `N/2` lines are drawn.                                                                                                                                                                                                                                                                 |
| GL_LINE_LOOP       | 0x2                | Draws a connected group of line segments from the first vertex to the last. Vertices `n` and `n + 1` define line `n`. `N - 1` lines are drawn.                                                                                                                                                                                                                                                 |
| GL_LINE_STRIP      | 0x3                | Draws a connected group of line segments from the first vertex to the last, then back to the first. Vertices `n` and `n + 1` define line `n`. The last line, however, is defined by vertices `N` and `1`. `N` lines are drawn.                                                                                                                                                                 |
| GL_TRIANGLES       | 0x4                | Treats each triplet of vertices as an independent triangle. Vertices `3n - 2`, `3n - 1`, and `3n` define triangle `n`. `N/3` triangles are drawn.                                                                                                                                                                                                                                              |
| GL_TRIANGLES_STRIP | 0x5                | Draws a connected group of triangles. One triangle is defined for each vertex presented after the first two vertices. For odd `n`, vertices `n`, `n + 1`, and `n + 2` define triangle `n`. For even `n`, vertices `n + 1`, `n`, and `n + 2` define triangle `n`. `N - 2` triangles are drawn.                                                                                                  |
| GL_TRIANGLES_FAN   | 0x6                | Draws a connected group of triangles. One triangle is defined for each vertex presented after the first two vertices. Vertices `1`, `n + 1`, and `n + 2` define triangle `n`. `N - 2` triangles are drawn.                                                                                                                                                                                     |
| GL_QUADS           | 0x7                | Treats each group of four vertices as an independent quadrilateral. Vertices `4n - 3`, `4n - 2`, `4n - 1`, and `4n` define quadrilateral `n`. `N/4` quadrilaterals are drawn.                                                                                                                                                                                                                  |
| GL_QUAD_STRIP      | 0x8                | Draws a connected group of quadrilaterals. One quadrilateral is defined for each pair of vertices presented after the first pair. Vertices `2n - 1`, `2n`, `2n + 2`, and `2n + 1` define quadrilateral `n`. `N/2 - 1` quadrilaterals are drawn. Note that the order in which vertices are used to construct a quadrilateral from strip data is different from that used with independent data. |
| GL_POLYGON         | 0x9                | Draws a single, convex polygon. Vertices `1` through `N` define this polygon.                                                                                                                                                                                                                                                                                                                  |