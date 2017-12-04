/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.javastuff

import java.util

import net.katsstuff.danmakucore.data.Quat
import net.katsstuff.danmakucore.entity.danmaku.{DanmakuTemplate, EntityDanmaku}
import net.katsstuff.danmakucore.impl.shape.{ShapeCircle, ShapeRandomRing, ShapeRing, ShapeSphere, ShapeWide}
import net.katsstuff.danmakucore.shape.Shape

/**
  * A few helper methods for the most used shapes.
  */
object DanmakuCreationHelper {
  def createWideShot(
      orientation: Quat,
      danmaku: DanmakuTemplate,
      amount: Int,
      wideAngle: Float,
      baseAngle: Float,
      distance: Double
  ): util.Set[EntityDanmaku] =
    drawSingle(danmaku, orientation, new ShapeWide(danmaku, amount, wideAngle, baseAngle, distance))

  def createCircleShot(
      orientation: Quat,
      danmaku: DanmakuTemplate,
      amount: Int,
      baseAngle: Float,
      distance: Double
  ): util.Set[EntityDanmaku] = drawSingle(danmaku, orientation, new ShapeCircle(danmaku, amount, baseAngle, distance))

  def createRingShot(
      orientation: Quat,
      danmaku: DanmakuTemplate,
      amount: Int,
      size: Float,
      baseAngle: Float,
      distance: Double
  ): util.Set[EntityDanmaku] =
    drawSingle(danmaku, orientation, new ShapeRing(danmaku, amount, size, baseAngle, distance))

  def createRandomRingShot(
      orientation: Quat,
      danmaku: DanmakuTemplate,
      amount: Int,
      size: Float,
      distance: Double
  ): util.Set[EntityDanmaku] = drawSingle(danmaku, orientation, new ShapeRandomRing(danmaku, amount, size, distance))

  def createSphereShot(
      orientation: Quat,
      danmaku: DanmakuTemplate,
      rings: Int,
      bands: Int,
      baseAngle: Float,
      distance: Double
  ): util.Set[EntityDanmaku] =
    drawSingle(danmaku, orientation, new ShapeSphere(danmaku, rings, bands, baseAngle, distance))

  private def drawSingle(danmaku: DanmakuTemplate, orientation: Quat, shape: Shape) =
    shape.draw(danmaku.pos, orientation, 0).getSpawnedDanmaku
}
