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
