/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.shape

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.data.{Quat, Vector3}
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate
import net.katsstuff.danmakucore.handler.DanmakuState
import net.katsstuff.danmakucore.shape.{Shape, ShapeResult}
import net.minecraft.util.math.MathHelper

class ShapeWide(template: DanmakuTemplate, amount: Int, wideAngle: Float, baseAngle: Float, distance: Double)
    extends Shape {

  override def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult = {
    if (!template.world.isRemote) {
      var rotateAngle = -wideAngle / 2D
      val stepSize    = wideAngle / (amount - 1)
      rotateAngle += baseAngle

      val builder = template.toBuilder
      val res = for (_ <- 0 until amount) yield {
        val rotate = orientation.multiply(Quat.fromAxisAngle(Vector3.Up, rotateAngle))
        builder.direction = Vector3.Forward.rotate(rotate)
        builder.pos = pos.offset(builder.direction, distance)
        builder.orientation = Quat.fromEuler(0F, 0F, (orientation.pitch * MathHelper.sin(rotateAngle.toFloat)).toFloat)
        rotateAngle += stepSize

        val spawned = builder.build.asEntity
        DanmakuCore.proxy.spawnDanmaku(spawned)
        spawned
      }

      ShapeResult.done(res.toSet)
    } else ShapeResult.done(Set.empty[DanmakuState])
  }
}
