package net.katsstuff.danmakucore.entity.living.ai

import net.minecraft.entity.EntityCreature
import net.minecraft.entity.ai.EntityAIWander
import net.minecraft.util.math.Vec3d

class EntityAIWanderHover(creature: EntityCreature, speedIn: Double, chance: Int) extends EntityAIWander(creature, speedIn, chance) {

  override def getPosition: Vec3d = FlyingRandomPositionGenerator.findRandomTarget(entity, 10, 7)

}
