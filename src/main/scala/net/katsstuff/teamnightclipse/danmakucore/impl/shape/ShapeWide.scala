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
        builder.orientation = rotate
        rotateAngle += stepSize

        builder.build.asEntity
      }

      ShapeResult.done(res.toSet)
    } else ShapeResult.done(Set.empty[DanmakuState])
  }
}
