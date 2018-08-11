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
package net.katsstuff.teamnightclipse.danmakucore.capability.callableentity

import net.minecraft.entity.{EntityLiving, EntityLivingBase}

class DefaultCallableEntity extends CallableEntity {
  private var distance = 16

  override def onEntityCall(caller: EntityLivingBase, target: EntityLivingBase): Unit = {
    val optAttackTarget = Option(caller.getRevengeTarget)
      .orElse(Option(caller.getLastDamageSource).flatMap { source =>
        source.getTrueSource match {
          case living: EntityLiving => Some(living)
          case _                    => None
        }
      })
      .orElse(caller match {
        case living: EntityLiving => Option(living.getAttackTarget)
        case _                    => Option(caller.getAttackingEntity)
      })
      .orElse(Option(caller.getLastAttackedEntity))

    optAttackTarget.foreach { attackTarget =>
      target match {
        case living: EntityLiving => living.setAttackTarget(attackTarget)
        case _                    => target.setRevengeTarget(attackTarget)
      }
    }
  }

  override def getCallDistance: Int = distance

  override def setCallDistance(distance: Int): Unit = this.distance = distance
}
