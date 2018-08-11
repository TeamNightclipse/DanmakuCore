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
package net.katsstuff.teamnightclipse.danmakucore.shape

import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}

/**
  * Something that can be drawn as danmaku over several ticks. Call [[ShapeHandler.createShape]] to create the shape.
  */
@FunctionalInterface
trait Shape {

  /**
    * Draws a shape for the given tick.
    *
    * @param pos The position to draw the shape at.
    * @param orientation The orientation that was registered for this shape.
    * @param tick The tick.
    * @return The result of drawing for this tick.
    */
  def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult

  /**
    * Executes some sort of effect on the danmaku spawned. This can be used
    * as a cheaper [[net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity.SubEntity]].
    *
    * @param pos The position the shape was drawn at.
    * @param orientation The orientation that was registered for this shape.
    * @param tick The tick.
    * @param resultThisTick The result for this tick.
    * @param allResults All results that have been produces so far.
    */
  def doEffects(
      pos: Vector3,
      orientation: Quat,
      tick: Int,
      resultThisTick: ShapeResult,
      allResults: Set[ShapeResult]
  ): Unit = {
    //Do nothing by default}
  }
}
