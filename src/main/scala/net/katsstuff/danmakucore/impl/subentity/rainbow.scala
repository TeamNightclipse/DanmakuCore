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

import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.danmaku.subentity.{SubEntity, SubEntityType}

private[danmakucore] class SubEntityTypeRainbow(name: String) extends SubEntityType(name) {
  override def instantiate: SubEntity = new SubEntityRainbow()
}

private[subentity] class SubEntityRainbow extends SubEntityDefault {
  override def onCreate(danmaku: DanmakuState): DanmakuState = {
    val hue = (danmaku.world.getWorldTime % 50) / 50F
    val color = Color.getHSBColor(hue, 1F, 1F).getRGB
    danmaku.copy(extra = danmaku.extra.copy(shot = danmaku.shot.setMainColor(color)))
  }
}