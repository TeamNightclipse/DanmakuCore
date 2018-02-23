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

class ShapeDelay(shape: Shape, delay: Int) extends Shape {
  override def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult =
    if (tick >= delay) shape.draw(pos, orientation, tick - delay)
    else ShapeResult.notDone(Set.empty[DanmakuState])

  override def doEffects(
      pos: Vector3,
      orientation: Quat,
      tick: Int,
      spawnedThisTick: ShapeResult,
      allSpawned: Set[ShapeResult]
  ): Unit = if (tick >= delay) shape.doEffects(pos, orientation, tick - delay, spawnedThisTick, allSpawned)
}
