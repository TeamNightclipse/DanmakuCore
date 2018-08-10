package net.katsstuff.teamnightclipse.danmakucore.entity.living.ai

import net.minecraft.entity.ai.{EntityFlyHelper, EntityMoveHelper}
import net.minecraft.entity.{EntityLiving, SharedMonsterAttributes}
import net.minecraft.pathfinding.PathNodeType
import net.minecraft.util.math.MathHelper

class EntityHoverHelper(entity: EntityLiving) extends EntityFlyHelper(entity) {

  override def onUpdateMoveHelper(): Unit = {
    if (action == EntityMoveHelper.Action.STRAFE) {
      entity.setNoGravity(true)
      val mediumSpeed =
        if (entity.onGround)
          (speed * entity
            .getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
            .getAttributeValue).toFloat
        else (speed * entity.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue).toFloat

      var moveSpeed = speed.toFloat * mediumSpeed
      var forward   = moveForward
      var strafe    = moveStrafe
      var dist      = MathHelper.sqrt(forward * forward + strafe * strafe)

      if (dist < 1.0F) dist = 1.0F

      dist = moveSpeed / dist
      forward = forward * dist
      strafe = strafe * dist

      val sinYaw = MathHelper.sin(entity.rotationYaw * 0.017453292F)
      val cosYaw = MathHelper.cos(entity.rotationYaw * 0.017453292F)

      val movX         = forward * cosYaw - strafe * sinYaw
      val movY         = strafe * cosYaw + forward * sinYaw
      val pathNavigate = entity.getNavigator

      if (pathNavigate != null) {
        val nodeProcessor = pathNavigate.getNodeProcessor
        if (nodeProcessor != null && (nodeProcessor
              .getPathNodeType(
                entity.world,
                MathHelper.floor(entity.posX + movX),
                MathHelper.floor(entity.posY),
                MathHelper.floor(entity.posZ + movY)
              ) !=
              PathNodeType.WALKABLE)) {
          moveForward = 1.0F
          moveStrafe = 0.0F
          moveSpeed = mediumSpeed
        }
      }

      entity.setAIMoveSpeed(moveSpeed)
      entity.setMoveForward(moveForward)
      entity.setMoveStrafing(moveStrafe)
      action = EntityMoveHelper.Action.WAIT
    } else {
      super.onUpdateMoveHelper()
    }
  }

}
