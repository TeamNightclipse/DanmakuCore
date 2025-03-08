package net.katsstuff.danmakucore.util

import org.joml.Vector2d

// https://pomax.github.io/bezierinfo/
// https://ciechanow.ski/drawing-bezier-curves/
// https://www.youtube.com/watch?v=aVwxzDHniEw
object Bezier {

  case class BezierPoint(point: Vector2d, normal: Vector2d) {
    val tangent = new Vector2d(normal.y, -normal.x)
  }

  def cubicBezier2(p0: Vector2d, p1: Vector2d, p2: Vector2d, p3: Vector2d, t: Double): BezierPoint = {
    val t2 = t * t
    val t3 = t2 * t
    val mt = 1 - t
    val mt2 = mt * mt
    val mt3 = mt2 * mt

    def quadraticBezier(p0: Double, p1: Double, p2: Double, t: Double): Double =
      p0 * mt2 + p1 * 2 * mt * t + p2 * t2

    def cubicBezier(p0: Double, p1: Double, p2: Double, p3: Double, t: Double): Double =
      p0 * mt3 + 3 * p1 * mt2 * t + 3 * p2 * mt * t2 + p3 * t3

    val x = cubicBezier(p0.x, p1.x, p2.x, p3.x, t)
    val y = cubicBezier(p0.y, p1.y, p2.y, p3.y, t)

    val p0d = Vector2d(p1).sub(p0).mul(3)
    val p1d = Vector2d(p2).sub(p1).mul(3)
    val p2d = Vector2d(p3).sub(p2).mul(3)

    val dx = quadraticBezier(p0d.x, p1d.x, p2d.x, t)
    val dy = quadraticBezier(p0d.y, p1d.y, p2d.y, t)

    BezierPoint(new Vector2d(x, y), new Vector2d(dx, dy).normalize())
  }
}
