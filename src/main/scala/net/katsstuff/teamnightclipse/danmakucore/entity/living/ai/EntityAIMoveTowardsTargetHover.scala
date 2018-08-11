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

import net.minecraft.entity.EntityCreature
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget
import net.minecraft.util.math.Vec3d

class EntityAIMoveTowardsTargetHover(creature: EntityCreature, speed: Double, targetMaxDistance: Float)
    extends EntityAIMoveTowardsTarget(creature, speed, targetMaxDistance) {

  override def shouldExecute: Boolean = {
    targetEntity = creature.getAttackTarget
    if (targetEntity == null) false
    else if (targetEntity.getDistanceSq(creature) > (targetMaxDistance * targetMaxDistance)) false
    else {
      val vec3d = FlyingRandomPositionGenerator.findRandomTargetBlockTowards(
        this.creature,
        16,
        7,
        new Vec3d(targetEntity.posX, targetEntity.posY, targetEntity.posZ)
      )
      if (vec3d == null) false
      else {
        movePosX = vec3d.x
        movePosY = vec3d.y
        movePosZ = vec3d.z
        true
      }
    }
  }
}
