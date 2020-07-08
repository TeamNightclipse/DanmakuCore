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
package net.katsstuff.teamnightclipse.danmakucore.javastuff

import java.util

import scala.collection.JavaConverters._

import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DanmakuState, DanmakuTemplate}
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.{DanmakuCreationHelper => ScalaDanmakuCreationHelper}
import net.katsstuff.teamnightclipse.mirror.data.Quat

/**
  * A few helper methods for the most used shapes.
  */
object DanmakuCreationHelper {
  def createWideShot(
      orientation: Quat, //Don't use. Set in the danmaku itself instead
      danmaku: DanmakuTemplate,
      amount: Int,
      wideAngle: Float,
      baseAngle: Float,
      distance: Double
  ): util.Set[DanmakuState] =
    ScalaDanmakuCreationHelper
      .createWideShot(danmaku, amount, wideAngle, baseAngle, distance, spawnDanmaku = true)
      .asJava

  def createCircleShot(
      orientation: Quat,
      danmaku: DanmakuTemplate,
      amount: Int,
      baseAngle: Float,
      distance: Double
  ): util.Set[DanmakuState] =
    ScalaDanmakuCreationHelper.createCircleShot(danmaku, amount, baseAngle, distance, spawnDanmaku = true).asJava

  def createRingShot(
      orientation: Quat,
      danmaku: DanmakuTemplate,
      amount: Int,
      size: Float,
      baseAngle: Float,
      distance: Double
  ): util.Set[DanmakuState] =
    ScalaDanmakuCreationHelper.createRingShot(danmaku, amount, size, baseAngle, distance, spawnDanmaku = true).asJava

  def createRandomRingShot(
      orientation: Quat,
      danmaku: DanmakuTemplate,
      amount: Int,
      size: Float,
      distance: Double
  ): util.Set[DanmakuState] =
    ScalaDanmakuCreationHelper.createRandomRingShot(danmaku, amount, size, distance, spawnDanmaku = true).asJava

  def createSphereShot(
      orientation: Quat,
      danmaku: DanmakuTemplate,
      rings: Int,
      bands: Int,
      baseAngle: Float,
      distance: Double
  ): util.Set[DanmakuState] =
    ScalaDanmakuCreationHelper.createSphereShot(danmaku, rings, bands, baseAngle, distance, spawnDanmaku = true).asJava
}
