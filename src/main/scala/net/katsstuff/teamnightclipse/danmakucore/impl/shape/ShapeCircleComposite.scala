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

import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}
import net.katsstuff.teamnightclipse.danmakucore.shape.{Shape, ShapeResult}
import net.minecraft.world.World

class ShapeCircleComposite(world: World, shape: Shape, amount: Int, baseAngle: Float) extends Shape {
  override def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult = {
    val usedBaseAngle = if (amount % 2 == 0) baseAngle + 360F / (amount * 2F) else baseAngle
    new ShapeWideComposite(world, shape, amount, 360F - 360F / amount, usedBaseAngle).draw(pos, orientation, tick)
  }
}
