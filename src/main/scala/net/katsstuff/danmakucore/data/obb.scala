/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.data

import javax.annotation.Nullable

import net.minecraft.util.math.AxisAlignedBB

case class OrientedBoundingBox(boundingBox: AxisAlignedBB, pos: Vector3, orientation: Quat) {

  //http://www.dyn4j.org/2010/01/sat/
  @Nullable
  def intersects(thatBoundingBox: AxisAlignedBB): Boolean = {
    val rotated = orientation * Vector3.Forward

    def points(aabb: AxisAlignedBB): Array[Vector3] = Array(
      Vector3(aabb.minX, aabb.minY, aabb.minZ),
      Vector3(aabb.maxX, aabb.minY, aabb.minZ),
      Vector3(aabb.minX, aabb.minY, aabb.maxZ),
      Vector3(aabb.maxX, aabb.minY, aabb.maxZ),
      Vector3(aabb.minX, aabb.maxY, aabb.minZ),
      Vector3(aabb.maxX, aabb.maxY, aabb.minZ),
      Vector3(aabb.minX, aabb.maxY, aabb.maxZ),
      Vector3(aabb.maxX, aabb.maxY, aabb.maxZ)
    )

    def project(points: Array[Vector3], axis: Vector3): Projection = {
      var min = axis.dot(points(0))
      var max = min
      var i   = 1
      while (i < points.length) {
        {
          val p = axis.dot(points(i))
          if (p < min) min = p
          else if (p > max) max = p
        }
        i += 1
      }

      Projection(min, max)
    }

    if (rotated.distanceSquared(Vector3.Forward) < 0.1 * 0.1) {
      boundingBox.intersects(thatBoundingBox)
    } else {
      val thisPoints = points(boundingBox).map(p => (orientation * (p - pos)) + pos)
      val thatPoints = points(thatBoundingBox)

      val usedAxes = Array.concat(OrientedBoundingBox.IdentityAxes, getAxes)

      usedAxes.forall { axis =>
        val thisProj = project(thisPoints, axis)
        val thatProj = project(thatPoints, axis)
        thisProj.overlaps(thatProj)
      }
    }
  }

  def getAxes: Array[Vector3] = OrientedBoundingBox.IdentityAxes.map(orientation * _)
}
object OrientedBoundingBox {
  final val IdentityAxes = Array(Vector3.Forward, Vector3.Up, Vector3.Right)
}

case class Projection(min: Double, max: Double) {
  def contains(that: Projection): Boolean = this.min <= that.min && this.max >= that.max

  def overlapAmount(that: Projection): Double = Math.max(0, Math.min(this.max, that.max) - Math.max(this.min, that.min))

  def overlaps(that: Projection): Boolean =
    this.min <= that.max && this.max >= that.min
}
