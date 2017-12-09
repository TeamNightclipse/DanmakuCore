/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.scalastuff

import scala.collection.JavaConverters._

import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.entity.danmaku.DanmakuTemplate
import net.katsstuff.danmakucore.impl.shape.{ShapeCircle, ShapeRandomRing, ShapeRing, ShapeSphere, ShapeWide}
import net.katsstuff.danmakucore.shape.Shape

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

  private def drawSingle(danmaku: DanmakuTemplate, shape: Shape): Set[DanmakuState] =
    shape.draw(danmaku.pos, danmaku.orientation, 0).getSpawnedDanmaku.asScala.toSet

}
