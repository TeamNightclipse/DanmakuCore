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
package net.katsstuff.teamnightclipse.danmakucore.impl.phase

import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuVariant
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.{Phase, PhaseType}
import net.katsstuff.teamnightclipse.danmakucore.helper.LogHelper
import net.katsstuff.teamnightclipse.danmakucore.registry.DanmakuRegistry
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanmakuCreationHelper
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.{Phase, PhaseManager, PhaseType}
import net.katsstuff.teamnightclipse.mirror.data.Vector3

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
      variant.create(user.world, Some(user), alternateMode = false, pos, direction, None).foreach { template =>
        DanmakuCreationHelper.createCircleShot(template.toBuilder.setUser(user).build, amount, 0F, 0.2D)
      }
    }
  }
}
