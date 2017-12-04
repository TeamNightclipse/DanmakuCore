/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.helper

import net.katsstuff.danmakucore.capability.danmakuhit.{
  AllyDanmakuHitBehavior,
  CapabilityDanmakuHitBehaviorJ,
  DanmakuHitBehavior
}
import net.katsstuff.danmakucore.data.{MovementData, RotationData, Vector3}
import net.katsstuff.danmakucore.entity.danmaku.{DamageSourceDanmaku, EntityDanmaku}
import net.katsstuff.danmakucore.lib.LibSounds
import net.katsstuff.danmakucore.scalastuff.DanCoreImplicits._
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.util.{EnumParticleTypes, SoundCategory, SoundEvent}
import net.minecraft.world.World

trait TDanmakuHelper {
  val GravityDefault: Double = -0.03D

  /**
    * Plays the iconic shot sound
    */
  def playShotSound(entity: Entity): Unit = entity.playSound(LibSounds.SHOT1, 2.0F, 1.2F)

  def playShotSound(world: World, pos: Vector3): Unit =
    world.playSound(null, pos.x, pos.y, pos.z, LibSounds.SHOT1, SoundCategory.NEUTRAL, 2F, 1.2F)

  def playLaserSound(entity: Entity): Unit = entity.playSound(LibSounds.LASER2, 2.0F, 1.2F)

  def playLaserSound(world: World, pos: Vector3): Unit =
    world.playSound(null, pos.x, pos.y, pos.z, LibSounds.LASER2, SoundCategory.NEUTRAL, 2F, 1.2F)

  private def explosionEffect(world: World, pos: Vector3, explosionSize: Float, sound: SoundEvent): Unit = {
    world.playSound(null, pos.toBlockPos, sound, SoundCategory.HOSTILE, 2.0F, 3.0F)
    if (explosionSize >= 2.0F)
      world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, pos.x, pos.y, pos.z, 1.0D, 0.0D, 0.0D)
    else world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, pos.x, pos.y, pos.z, 1.0D, 0.0D, 0.0D)
  }

  def explosionEffect(world: World, pos: Vector3, explosionSize: Float): Unit =
    explosionEffect(world, pos, explosionSize, LibSounds.BOSS_EXPLODE)

  def explosionEffect2(world: World, pos: Vector3, explosionSize: Float): Unit =
    explosionEffect(world, pos, explosionSize, LibSounds.SHOT2)

  /**
    * Create a chain explosion, damaging any entity with [[AllyDanmakuHitBehavior]] in the vicinity.
    */
  def chainExplosion(deadEntity: Entity, range: Float, maxDamage: Float): Unit = {
    val behavior = CapabilityDanmakuHitBehaviorJ.HIT_BEHAVIOR

    deadEntity.world
      .collectEntitiesWithinAABBExcludingEntity(Some(deadEntity), deadEntity.getEntityBoundingBox.grow(range)) {
        case e: EntityLivingBase
            if AllyDanmakuHitBehavior == e.getCapability(behavior, null) =>
          e
      }
      .foreach { entity =>
        val distance = entity.getDistance(deadEntity)
        if (distance <= range) {
          val damage = maxDamage * (1.0F - (distance / range))
          entity.attackEntityFrom(DamageSourceDanmaku.causeDanmakuDamage(entity, deadEntity), damage)
        }
      }
  }

  /**
    * Remove danmaku in the specific area
    *
    * @param centerEntity Where to center the removal around
    * @param range The range to remove in
    * @param mode What should be removed and what should be left behind
    * @param dropBonus Should the danmaku drop bonus when removed
    * @return The amount of danmaku removed
    */
  def removeDanmaku(centerEntity: Entity, range: Double, mode: RemoveMode, dropBonus: Boolean): Int = {
    val present = centerEntity.world
      .collectEntitiesWithinAABBExcludingEntity(Some(centerEntity), centerEntity.getEntityBoundingBox.grow(range)) {
        case e: EntityDanmaku if e.getUser.isPresent => e -> e.getUser.get()
      }

    present.foreach {
      case (danmaku, user) =>
        mode match {
          case RemoveMode.All =>
            finishOrKillDanmaku(danmaku, dropBonus)
          case RemoveMode.Enemy =>
            if (!user.isInstanceOf[EntityPlayer]) {
              finishOrKillDanmaku(danmaku, dropBonus)
            }
          case RemoveMode.Player =>
            if (user.isInstanceOf[EntityPlayer]) {
              finishOrKillDanmaku(danmaku, dropBonus)
            }
          case RemoveMode.Other =>
            if (!(user == centerEntity)) {
              finishOrKillDanmaku(danmaku, dropBonus)
            }
        }
    }

    present.size
  }

  private def finishOrKillDanmaku(entity: EntityDanmaku, dropBonus: Boolean): Unit =
    if (dropBonus) entity.danmakuFinishBonus()
    else entity.setDead()

  def adjustDamageTarget(base: Float, target: Entity): Float =
    if (target.isInstanceOf[EntityPlayer]) base * 3.5F
    else base

  //TODO: For simpler cases (no acceleration), there has to be a simpler way to do this
  def simulateRotation(pos: Vector3, direction: Vector3, movement: MovementData, rotation: RotationData): Vector3 = {
    val negDirection = direction.negate //Need this for it to work correctly?
    val quat         = rotation.getRotationQuat
    val origin       = pos

    def inner(dir: Vector3, last1: Double, last2: Double, counter: Int, motion: Vector3, pos: Vector3): Vector3 = {
      if (last1 >= last2 && counter < 999) {
        val newDir    = dir.rotate(quat)
        val newMotion = simulateAccelerate(movement, motion, newDir)
        val newPos    = pos.add(newMotion)

        inner(
          dir = newDir,
          last1 = newPos.distanceSquared(origin),
          last2 = last1,
          counter = counter + 1,
          newMotion,
          newPos
        )
      } else origin.add(pos.subtract(origin).divide(2))
    }

    inner(negDirection, 0D, 0D, 0, simulateSetSpeed(negDirection, movement.speedOriginal), pos)
  }

  def simulateAccelerate(movement: MovementData, currentMotion: Vector3, direction: Vector3): Vector3 = {
    val speedAccel      = movement.getSpeedAcceleration
    val upperSpeedLimit = movement.getUpperSpeedLimit
    val lowerSpeedLimit = movement.getLowerSpeedLimit
    val currentSpeed    = currentMotion.length
    if (MathUtil.fuzzyCompare(currentSpeed, upperSpeedLimit) >= 0 && speedAccel >= 0D)
      simulateSetSpeed(direction, upperSpeedLimit)
    else if (MathUtil.fuzzyCompare(currentSpeed, lowerSpeedLimit) <= 0 && speedAccel <= 0D)
      simulateSetSpeed(direction, lowerSpeedLimit)
    else {
      val newMotion       = simulateAddSpeed(direction, speedAccel, currentMotion)
      val newCurrentSpeed = newMotion.length
      if (MathUtil.fuzzyCompare(newCurrentSpeed, upperSpeedLimit) > 0) simulateSetSpeed(direction, upperSpeedLimit)
      else if (MathUtil.fuzzyCompare(newCurrentSpeed, lowerSpeedLimit) < 0) simulateSetSpeed(direction, lowerSpeedLimit)
      else newMotion
    }
  }

  def simulateSetSpeed(direction: Vector3, speed: Double): Vector3 = direction.multiply(speed)

  def simulateAddSpeed(direction: Vector3, speed: Double, currentMotion: Vector3): Vector3 =
    currentMotion.offset(direction, speed)
}
