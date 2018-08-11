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
import net.katsstuff.teamnightclipse.danmakucore.shape.{Shape, ShapeResult}
import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}

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
