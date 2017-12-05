/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.shape

import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.data.{Quat, Vector3}
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate
import net.katsstuff.danmakucore.shape.{Shape, ShapeResult}

class ShapeSphere(danmaku: DanmakuTemplate, rings: Int, bands: Int, baseAngle: Float, distance: Double) extends Shape {
  private val usedBans = bands / 2

  override def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult = {
    if (!danmaku.world.isRemote) {
      val rotatedForward = orientation.multiply(Quat.fromAxisAngle(Vector3.Forward, 90))
      val increment      = 180F / usedBans
      val shape          = new ShapeCircle(danmaku, rings, baseAngle, distance)

      val res = for (i <- 0 until usedBans) yield {
        val rotate = Quat.fromAxisAngle(Vector3.Up, increment * i)
        shape.draw(pos, rotate.multiply(rotatedForward), tick).spawnedDanmaku
      }

      ShapeResult.done(res.toSet.flatten)
    } else ShapeResult.done(Set.empty[DanmakuState])
  }
}
