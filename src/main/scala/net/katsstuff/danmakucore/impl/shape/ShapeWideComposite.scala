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
import net.katsstuff.mirror.data.{Quat, Vector3}
import net.katsstuff.danmakucore.shape.{Shape, ShapeResult}
import net.minecraft.world.World

class ShapeWideComposite(world: World, shape: Shape, amount: Int, wideAngle: Float, baseAngle: Float) extends Shape {
  override def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult = {
    if (!world.isRemote) {
      var rotateAngle = -wideAngle / 2D
      val stepSize    = wideAngle / (amount - 1)
      rotateAngle += baseAngle

      val res = for (_ <- 0 until amount) yield {
        val rotate = orientation.multiply(Quat.fromAxisAngle(Vector3.Up, rotateAngle))
        rotateAngle += stepSize
        shape.draw(pos, rotate, tick)
      }
      val done    = res.map(_.isDone).forall(identity)
      val spawned = res.flatMap(_.spawnedDanmaku).toSet

      ShapeResult(done, spawned)
    } else ShapeResult.done(Set.empty[DanmakuState])
  }
}
