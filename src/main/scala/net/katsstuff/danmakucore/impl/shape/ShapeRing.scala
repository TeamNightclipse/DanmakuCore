/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.shape

import net.katsstuff.danmakucore.danmaku.{DanmakuState, DanmakuTemplate}
import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}
import net.katsstuff.danmakucore.shape.{Shape, ShapeResult}

class ShapeRing(template: DanmakuTemplate, amount: Int, radius: Float, baseAngle: Float, distance: Double)
    extends Shape {

  override def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult = {
    if (!template.world.isRemote) {
      val wideAngle   = 360F
      var rotateAngle = -wideAngle / 2D
      val stepSize    = wideAngle / (amount - 1)
      rotateAngle += baseAngle

      val rotatedOrientation = orientation.multiply(Quat.fromAxisAngle(Vector3.Right, 90))

      val builder = template.toBuilder

      val res = for (_ <- 0 until amount) yield {
        val rotate = rotatedOrientation.multiply(
          Quat.fromAxisAngle(Vector3.Up, rotateAngle).multiply(Quat.fromAxisAngle(Vector3.Left, 90D - radius))
        )
        builder.direction = Vector3.Forward.rotate(rotate)
        builder.pos = pos.offset(builder.direction, distance)
        builder.orientation = rotate
        rotateAngle += stepSize
        val spawned = builder.build.asEntity
        spawned
      }

      ShapeResult.done(res.toSet)
    } else ShapeResult.done(Set.empty[DanmakuState])
  }
}
