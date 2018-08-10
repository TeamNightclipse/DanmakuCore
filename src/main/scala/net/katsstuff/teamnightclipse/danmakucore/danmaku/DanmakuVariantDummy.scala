/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.danmaku

import net.katsstuff.teamnightclipse.danmakucore.data.{MovementData, RotationData, ShotData}

object DanmakuVariantDummy extends DanmakuVariant {
  def instance: DanmakuVariantDummy.type = this

  override def getShotData: ShotData         = ShotData.DefaultShotData
  override def getMovementData: MovementData = MovementData.constant(0D)
  override def getRotationData: RotationData = RotationData.none
}