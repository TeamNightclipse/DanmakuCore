/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.impl.shape

import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuTemplate
import net.katsstuff.teamnightclipse.danmakucore.shape.{Shape, ShapeResult}
import net.katsstuff.teamnightclipse.mirror.data.{Quat, Vector3}

class ShapeCircle(danmaku: DanmakuTemplate, amount: Int, baseAngle: Float, distance: Double) extends Shape {
  override def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult = {
    val usedBaseAngle = if (amount % 2 == 0) baseAngle + 360F / (amount * 2F) else baseAngle
    new ShapeWide(danmaku, amount, 360F - 360F / amount, usedBaseAngle, distance).draw(pos, orientation, tick)
  }
}