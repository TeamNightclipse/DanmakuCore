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
package net.katsstuff.teamnightclipse.danmakucore.scalastuff

import scala.collection.JavaConverters._

import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DanmakuState, DanmakuTemplate}
import net.katsstuff.teamnightclipse.danmakucore.impl.shape.{ShapeCircle, ShapeRandomRing, ShapeRing, ShapeSphere, ShapeWide}
import net.katsstuff.teamnightclipse.danmakucore.shape.Shape

/**
  * A few helper methods for the most used shapes.
  */
object DanmakuCreationHelper {

  def createWideShot(
      danmaku: DanmakuTemplate,
      amount: Int,
      wideAngle: Float,
      baseAngle: Float,
      distance: Double
  ): Set[DanmakuState] =
    drawSingle(danmaku, new ShapeWide(danmaku, amount, wideAngle, baseAngle, distance))

  def createCircleShot(
      danmaku: DanmakuTemplate,
      amount: Int,
      baseAngle: Float,
      distance: Double
  ): Set[DanmakuState] = drawSingle(danmaku, new ShapeCircle(danmaku, amount, baseAngle, distance))

  def createRingShot(
      danmaku: DanmakuTemplate,
      amount: Int,
      size: Float,
      baseAngle: Float,
      distance: Double
  ): Set[DanmakuState] =
    drawSingle(danmaku, new ShapeRing(danmaku, amount, size, baseAngle, distance))

  def createRandomRingShot(
      danmaku: DanmakuTemplate,
      amount: Int,
      size: Float,
      distance: Double
  ): Set[DanmakuState] = drawSingle(danmaku, new ShapeRandomRing(danmaku, amount, size, distance))

  def createSphereShot(
      danmaku: DanmakuTemplate,
      rings: Int,
      bands: Int,
      baseAngle: Float,
      distance: Double
  ): Set[DanmakuState] =
    drawSingle(danmaku, new ShapeSphere(danmaku, rings, bands, baseAngle, distance))

  private def drawSingle(danmaku: DanmakuTemplate, shape: Shape): Set[DanmakuState] = {
    val res = shape.draw(danmaku.pos, danmaku.orientation, 0).spawnedDanmaku
    DanmakuCore.proxy.spawnDanmaku(res.toSeq)
    res
  }

}
