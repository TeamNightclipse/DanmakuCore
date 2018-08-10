/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
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
