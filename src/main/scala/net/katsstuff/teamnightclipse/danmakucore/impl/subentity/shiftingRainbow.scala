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
package net.katsstuff.teamnightclipse.danmakucore.impl.subentity

import java.awt.Color

import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DanmakuState, DanmakuUpdate}
import net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity.{SubEntity, SubEntityType}

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
