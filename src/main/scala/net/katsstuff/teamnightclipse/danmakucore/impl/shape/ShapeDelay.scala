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
package net.katsstuff.teamnightclipse.danmakucore.impl.shape

import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}
import net.katsstuff.teamnightclipse.danmakucore.shape.{Shape, ShapeResult}

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
