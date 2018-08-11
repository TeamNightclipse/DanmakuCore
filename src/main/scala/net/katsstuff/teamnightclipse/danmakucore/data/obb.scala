/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.katsstuff.teamnightclipse.danmakucore.data

import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}
import net.minecraft.util.math.AxisAlignedBB

case class OrientedBoundingBox(aabb: AxisAlignedBB, pos: Vector3, orientation: Quat) {

  //http://www.dyn4j.org/2010/01/sat/
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
      aabb.intersects(thatBoundingBox)
    } else {
      val thisPoints = points(aabb).map(p => (orientation * (p - pos)) + pos)
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
