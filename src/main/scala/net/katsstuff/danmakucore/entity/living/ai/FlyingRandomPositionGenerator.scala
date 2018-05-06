/*
 * This class was created by <Katrix>. It's distributed as
 * part of the Journey To Gensokyo Mod. Get the Source Code in github:
 * https://github.com/Katrix-/JTG
 *
 * Journey To Gensokyo is Open Source and distributed under the
 * a modifed Botania license: https://github.com/Katrix-/JTG/blob/devDanmakuCore/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.living.ai

import javax.annotation.Nullable

import net.minecraft.entity.EntityCreature
import net.minecraft.util.math.{BlockPos, MathHelper, Vec3d}

object FlyingRandomPositionGenerator {
  private var staticVector = Vec3d.ZERO

  @Nullable
  def findRandomTarget(entity: EntityCreature, xz: Int, y: Int): Vec3d =
    findRandomTargetBlock(entity, xz, y, null)

  @Nullable
  def findRandomTargetBlockTowards(entity: EntityCreature, xz: Int, y: Int, target: Vec3d): Vec3d = {
    staticVector = target.subtract(entity.posX, entity.posY, entity.posZ)
    findRandomTargetBlock(entity, xz, y, staticVector)
  }

  @Nullable
  def findRandomTargetBlockAwayFrom(entity: EntityCreature, xz: Int, y: Int, awayFrom: Vec3d): Vec3d = {
    staticVector = new Vec3d(entity.posX, entity.posY, entity.posZ).subtract(awayFrom)
    findRandomTargetBlock(entity, xz, y, staticVector)
  }

  @Nullable
  private def findRandomTargetBlock(entity: EntityCreature, maxXz: Int, maxY: Int, @Nullable targetVec3: Vec3d) = {
    val navigate = entity.getNavigator
    val rand     = entity.getRNG
    var flag     = false
    var xDest    = 0D
    var yDest    = 0D
    var zDest    = 0D
    var weight   = -99999.0F

    if (entity.hasHome) {
      val d0 = entity.getHomePosition.distanceSq(
        MathHelper.floor(entity.posX),
        MathHelper.floor(entity.posY),
        MathHelper.floor(entity.posZ)
      ) + 4.0D
      val maxDist = entity.getMaximumHomeDistance + maxXz
      flag = d0 < maxDist * maxDist
    } else flag = false

    for (_ <- 0 until 10) {
      var xMod = rand.nextInt(2 * maxXz + 1) - maxXz
      var yMod = rand.nextInt(2 * maxY + 1) - maxY
      var zMod = rand.nextInt(2 * maxXz + 1) - maxXz
      if (targetVec3 == null || xMod * targetVec3.x + yMod * targetVec3.y + zMod * targetVec3.z >= 0.0D) {
        if (entity.hasHome && maxXz > 1) {
          val homePos = entity.getHomePosition
          xMod += (if (entity.posX > homePos.getX) -rand.nextInt(maxXz / 2) else rand.nextInt(maxXz / 2))
          yMod += (if (entity.posY > homePos.getY) -rand.nextInt(maxY / 2) else rand.nextInt(maxY / 2))
          zMod += (if (entity.posZ > homePos.getZ) -rand.nextInt(maxXz / 2) else rand.nextInt(maxXz / 2))
        }

        val pos = new BlockPos(xMod + entity.posX, yMod + entity.posY, zMod + entity.posZ)
        if ((!flag || entity.isWithinHomeDistanceFromPosition(pos)) && (navigate.canEntityStandOnPos(pos) || entity.world
              .isAirBlock(pos))) {
          val currentWeight = entity.getBlockPathWeight(pos)
          if (currentWeight > weight) {
            weight = currentWeight
            xDest = xMod
            yDest = yMod
            zDest = zMod
            flag = true
          }
        }
      }
    }

    if (flag) new Vec3d(xDest + entity.posX, yDest + entity.posY, zDest + entity.posZ)
    else null
  }
}
