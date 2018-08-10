/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity

import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DanmakuState, DanmakuUpdate}

object SubEntityTypeDummy extends SubEntityType {
  def instance: SubEntityTypeDummy.type = this

  override def instantiate: SubEntity = new SubEntityDummy
}

private[subentity] class SubEntityDummy extends SubEntity {
  override def subEntityTick(danmaku: DanmakuState): DanmakuUpdate = DanmakuUpdate.noUpdates(danmaku)
}
