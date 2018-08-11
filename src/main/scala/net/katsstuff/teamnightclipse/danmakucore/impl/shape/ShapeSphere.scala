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

import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DanmakuState, DanmakuTemplate}
import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}
import net.katsstuff.teamnightclipse.danmakucore.shape.{Shape, ShapeResult}

class ShapeSphere(danmaku: DanmakuTemplate, rings: Int, bands: Int, baseAngle: Float, distance: Double) extends Shape {
  private val usedBands = bands / 2

  override def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult = {
    if (!danmaku.world.isRemote) {
      val rotatedForward = orientation.multiply(Quat.fromAxisAngle(Vector3.Forward, 90))
      val increment      = 180F / usedBands
      val shape          = new ShapeCircle(danmaku, rings, baseAngle, distance)

      val res = for (i <- 0 until usedBands) yield {
        val rotate = Quat.fromAxisAngle(Vector3.Up, increment * i)
        shape.draw(pos, rotate.multiply(rotatedForward), tick).spawnedDanmaku
      }

      ShapeResult.done(res.toSet.flatten)
    } else ShapeResult.done(Set.empty[DanmakuState])
  }
}
