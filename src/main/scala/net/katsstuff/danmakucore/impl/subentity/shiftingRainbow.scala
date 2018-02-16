/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.subentity

import java.awt.Color

import net.katsstuff.danmakucore.danmaku.{DanmakuState, DanmakuUpdate}
import net.katsstuff.danmakucore.danmaku.subentity.{SubEntity, SubEntityType}

private[danmakucore] class SubEntityTypeShiftingRainbow(name: String) extends SubEntityType(name) {
  override def instantiate: SubEntity = new SubEntityShiftingRainbow()
}

private[subentity] class SubEntityShiftingRainbow extends SubEntityDefault {
  private var originalHue: Float = _

  override def onInstantiate(danmaku: DanmakuState): DanmakuState = {
    val hsb      = new Array[Float](3)
    val colorObj = new Color(danmaku.shot.mainColor)
    Color.RGBtoHSB(colorObj.getRed, colorObj.getGreen, colorObj.getBlue, hsb)
    originalHue = hsb(0)
    danmaku
  }

  override def subEntityTick(danmaku: DanmakuState): DanmakuUpdate = {
    val hue        = originalHue + (danmaku.ticksExisted % 50) / 50F
    val color      = Color.HSBtoRGB(hue, 1F, 1F)
    val newDanmaku = danmaku.copy(extra = danmaku.extra.copy(shot = danmaku.shot.setMainColor(color)))

    super.subEntityTick(newDanmaku)
  }
}
