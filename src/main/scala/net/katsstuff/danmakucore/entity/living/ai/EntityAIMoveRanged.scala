/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.ai

import net.minecraft.entity.EntityLiving
import net.minecraft.entity.ai.EntityAIBase

//EntityAIAttackRangedBow without attack part
class EntityAIMoveRanged(
    val entity: EntityLiving,
    val moveSpeedAmp: Double,
    val maxDistanceXZ: Float,
    val maxDistanceY: Float
) extends EntityAIBase {

  private val maxAttackDistanceXZ = maxDistanceXZ * maxDistanceXZ
  private val maxAttackDistanceY  = maxDistanceY * maxDistanceY
  private var seeTime             = 0
  private var strafingClockwise   = false
  private var strafingBackwards   = false
  private var flyingUp            = false
  private var strafingTime        = -1

  setMutexBits(3)

  override def shouldExecute: Boolean = this.entity.getAttackTarget != null

  def continueExecuting: Boolean = this.shouldExecute || !this.entity.getNavigator.noPath

  override def resetTask(): Unit = {
    super.resetTask()
    seeTime = 0
  }

  //Should be kept as similar as the one found in EntityAIAttackRangedBow as possible
  override def updateTask(): Unit = {
    val target = entity.getAttackTarget
    if (target != null) {
      //We ignore the y coordinate here
      val distXZ   = entity.getDistanceSq(target.posX, entity.posY, target.posZ)
      val distY    = entity.posY - target.posY
      val canSee   = entity.getEntitySenses.canSee(target)
      val haveSeen = seeTime > 0

      if (canSee != haveSeen) seeTime = 0

      if (canSee) seeTime += 1 else seeTime -= 1

      if ((distXZ <= maxAttackDistanceXZ || Math.abs(distY) <= maxAttackDistanceY) && seeTime >= 20) {
        entity.getNavigator.clearPath()
        strafingTime += 1
      } else {
        entity.getNavigator.tryMoveToEntityLiving(target, moveSpeedAmp)
        strafingTime = -1
      }

      if (strafingTime >= 20) {
        if (entity.getRNG.nextFloat < 0.3D) strafingClockwise = !strafingClockwise
        if (entity.getRNG.nextFloat < 0.3D) strafingBackwards = !strafingBackwards
        strafingTime = 0
      }

      if (strafingTime > -1) {
        if (distXZ > (maxAttackDistanceXZ * 0.75F)) strafingBackwards = false
        else if (distXZ < maxAttackDistanceXZ * 0.25F) strafingBackwards = true

        if (distY > (maxAttackDistanceY * 0.5F)) flyingUp = false
        else if (distY < maxAttackDistanceY * -0.5F) flyingUp = true

        entity.getMoveHelper.strafe(if (strafingBackwards) -0.5F else 0.5F, if (strafingClockwise) 0.5F else -0.5F)
        entity.setMoveVertical(if(flyingUp) 0.5F else -0.5F)
        entity.faceEntity(target, 30.0F, 30.0F)
      } else entity.getLookHelper.setLookPositionWithEntity(target, 30.0F, 30.0F)
    }
  }
}
