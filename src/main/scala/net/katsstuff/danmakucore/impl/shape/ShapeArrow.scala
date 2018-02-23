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
import net.katsstuff.danmakucore.shape.{Shape, ShapeResult}
import net.katsstuff.mirror.data.{Quat, Vector3}

/**
  * An arrow shape.
  *
  * @param template The danmaku to use
  * @param amount How many danmaku should appear on each side - 1
  * @param distance How much depth should be between each wave of danmaku.
  * @param width How much width should there be between two waves of danmaku.
  */
class ShapeArrow(template: DanmakuTemplate, amount: Int, distance: Double, width: Double) extends Shape {
  override def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult = {
    if (!template.world.isRemote) {
      val localForward = Vector3.Forward.rotate(orientation)
      val localLeft    = Vector3.Left.rotate(orientation)
      val localRight   = Vector3.Right.rotate(orientation)

      val builder = template.toBuilder
      builder.direction = localForward

      val res = for (i <- 0 until amount) yield {
        val newDistance   = -i * distance
        val newWidth      = -i * width
        val newPosNeutral = pos.offset(localForward, newDistance)
        builder.pos = newPosNeutral.offset(localLeft, newWidth)
        val createdLeft = builder.build.asEntity
        builder.pos = newPosNeutral.offset(localRight, newWidth)
        val createdRight = builder.build.asEntity

        Set(createdLeft, createdRight)
      }

      ShapeResult.done(res.toSet.flatten)
    } else ShapeResult.done(Set.empty[DanmakuState])
  }
}
