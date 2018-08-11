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

import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity.{SubEntity, SubEntityType}

private[danmakucore] class SubEntityTypeRainbow(name: String) extends SubEntityType(name) {
  override def instantiate: SubEntity = new SubEntityRainbow()
}

private[subentity] class SubEntityRainbow extends SubEntityDefault {
  override def onCreate(danmaku: DanmakuState): DanmakuState = {
    val hue   = (danmaku.world.getWorldTime % 50) / 50F
    val color = Color.getHSBColor(hue, 1F, 1F).getRGB
    danmaku.copy(extra = danmaku.extra.copy(shot = danmaku.shot.setMainColor(color)))
  }
}
