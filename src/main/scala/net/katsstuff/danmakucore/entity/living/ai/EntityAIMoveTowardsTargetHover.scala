package net.katsstuff.danmakucore.entity.living.ai

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
