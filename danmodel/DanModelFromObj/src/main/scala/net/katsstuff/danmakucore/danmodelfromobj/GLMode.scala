package net.katsstuff.danmakucore.danmodelfromobj

sealed abstract case class GLMode private (int: Int)
object GLMode {
  object GLPoints        extends GLMode(0x0)
  object GLLines         extends GLMode(0x1)
  object GLLineLoop      extends GLMode(0x2)
  object GLLineStrip     extends GLMode(0x3)
  object GLTriangles     extends GLMode(0x4)
  object GLTriangleStrip extends GLMode(0x5)
  object GLTrianglesFan  extends GLMode(0x6)
  object GLQuads         extends GLMode(0x7)
  object GLQuadStrip     extends GLMode(0x8)
  object GLPolygon       extends GLMode(0x9)
}
