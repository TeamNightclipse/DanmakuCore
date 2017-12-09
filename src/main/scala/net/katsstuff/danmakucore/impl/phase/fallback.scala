/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.phase

import net.katsstuff.danmakucore.data.{Quat, Vector3}
import net.katsstuff.danmakucore.entity.danmaku.DanmakuVariant
import net.katsstuff.danmakucore.entity.living.phase.{Phase, PhaseManager, PhaseType}
import net.katsstuff.danmakucore.helper.LogHelper
import net.katsstuff.danmakucore.registry.DanmakuRegistry
import net.katsstuff.danmakucore.scalastuff.DanmakuCreationHelper

class PhaseTypeFallback extends PhaseType {
  override def instantiate(phaseManager: PhaseManager) = new PhaseFallback(phaseManager, this)
}

private[phase] class PhaseFallback(manager: PhaseManager, val phaseType: PhaseTypeFallback) extends Phase(manager) {
  final private val variant = DanmakuRegistry.getRandomObject(classOf[DanmakuVariant], entity.getRNG)

  final private val amount = entity.getRNG.nextInt(8)

  override def serverUpdate(): Unit = {
    super.serverUpdate()
    if (isCounterStart) {
      val user = entity
      LogHelper.warn(s"This is the fallback phase being used for $user. If you are seeing this something broke")

      val pos       = user.pos
      val direction = Vector3.directionEntity(user)
      variant.create(Some(user), alternateMode = false, pos, direction, None).foreach { template =>
        DanmakuCreationHelper.createCircleShot(template.toBuilder.setUser(user).build, amount, 0F, 0.2D)
      }
    }
  }
}
