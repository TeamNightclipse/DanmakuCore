/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.subentity

import java.util.Random
import java.util.function.Predicate

import net.katsstuff.danmakucore.data.Vector3
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity
import net.katsstuff.danmakucore.entity.danmaku.{DamageSourceDanmaku, EntityDanmaku}
import net.katsstuff.danmakucore.entity.living.DanmakuAlly
import net.katsstuff.danmakucore.handler.ConfigHandler
import net.katsstuff.danmakucore.helper.MathUtil._
import net.katsstuff.danmakucore.lib.LibSounds
import net.katsstuff.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.danmakucore.scalastuff.DanmakuHelper
import net.minecraft.entity.{Entity, EntityAgeable, EntityLivingBase, IEntityMultiPart}
import net.minecraft.init.Blocks
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World

abstract class SubEntityBase(world: World, danmaku: EntityDanmaku) extends SubEntity(world, danmaku) {
  protected val rand = new Random

  /**
    * Called when the danmaku hits a block.
    */
  protected def impactBlock(raytrace: RayTraceResult): Unit = {
    val shot = danmaku.getShotData
    if (shot.sizeZ <= 1F || shot.sizeZ / shot.sizeX <= 3 || shot.sizeZ / shot.sizeY <= 3) danmaku.delete()
  }

  /**
    * Called when the danmaku hits an entity.
    */
  protected def impactEntity(raytrace: RayTraceResult): Unit = {
    val hitEntity = raytrace.entityHit
    val optUser   = danmaku.getUser
    if (hitEntity.isInstanceOf[EntityLivingBase] && !hitEntity
          .isInstanceOf[EntityAgeable] && !(optUser.orElse(null).isInstanceOf[DanmakuAlly] && hitEntity
          .isInstanceOf[DanmakuAlly]) || (hitEntity.isInstanceOf[IEntityMultiPart] && !hitEntity
          .isInstanceOf[EntityDanmaku])) {
      attackEntity(danmaku, hitEntity)
    }
  }

  protected def attackEntity(danmaku: EntityDanmaku, entity: Entity): Unit = {
    val user        = danmaku.user
    val indirect    = user.orElse(danmaku.source)
    val shot        = danmaku.shotData
    val averageSize = (shot.sizeY + shot.sizeX + shot.sizeZ) / 3

    entity.attackEntityFrom(
      DamageSourceDanmaku.causeDanmakuDamage(danmaku, indirect),
      DanmakuHelper.adjustDanmakuDamage(user, entity, danmaku.shotData.damage, ConfigHandler.danmaku.danmakuLevel)
    )
    entity.playSound(LibSounds.DAMAGE, 1F, 1F)

    if (averageSize < 0.7F) danmaku.delete()
  }

  /**
    * Called on any impact. Called after [[impactBlock]] and [[impactEntity]].
    */
  protected def impact(raytrace: RayTraceResult): Unit = {}

  /**
    * Called when the danmaku is in water.
    */
  protected def waterMovement(): Unit = {
    val shot     = danmaku.getShotData
    val modifier = danmaku.getCurrentSpeed * (shot.sizeX * shot.sizeY * shot.sizeZ * 4)

    for (_ <- 0 until (2 * modifier).toInt) {
      val f4        = 0.25F
      val posCenter = Vector3.fromEntityCenter(danmaku)
      val randX     = rand.nextDouble * shot.sizeX - shot.sizeX / 2
      val randY     = rand.nextDouble * shot.sizeY - shot.sizeY / 2
      val randZ     = rand.nextDouble * shot.sizeZ - shot.sizeZ / 2

      world.spawnParticle(
        EnumParticleTypes.WATER_BUBBLE,
        posCenter.x + randX - danmaku.motionX * f4,
        posCenter.y + randY - danmaku.motionY * f4,
        posCenter.z + randZ - danmaku.motionZ * f4,
        danmaku.motionX,
        danmaku.motionY,
        danmaku.motionZ
      )
    }
    if (danmaku.motionX < 0.01 && danmaku.motionY < 0.01 && danmaku.motionZ < 0.01) danmaku.ticksExisted += 3
    danmaku.motionX *= 0.95F
    danmaku.motionY *= 0.95F
    danmaku.motionZ *= 0.95F
  }

  /**
    * Add the gravity to the motion.
    */
  protected def updateMotionWithGravity(): Unit = {
    val entity  = danmaku
    val gravity = danmaku.getMovementData.getGravity
    entity.motionX += gravity.x
    entity.motionY += gravity.y
    entity.motionZ += gravity.z
  }

  protected def hitCheck(exclude: Predicate[Entity]): Unit = hitCheck(exclude.asScala)

  protected def hitCheck(exclude: Entity => Boolean): Unit = {
    val shot      = danmaku.getShotData
    val direction = danmaku.getDirection
    val start     = new Vector3(danmaku).offset(direction, -shot.sizeZ / 2)
    val end       = start.offset(direction, shot.sizeZ).add(danmaku.motionX, danmaku.motionY, danmaku.motionZ)

    val bb = danmaku.getEntityBoundingBox.expand(danmaku.motionX, danmaku.motionY, danmaku.motionZ).grow(1D)
    val potentialHits = world.collectEntitiesWithinAABBExcludingEntity(Some(danmaku), bb) {
      case e if e.canBeCollidedWith && !e.noClip && exclude(e) => e
    }

    var groundRay: RayTraceResult = null
    val obb = danmaku.getOrientedBoundingBox
    for (potentialHit <- potentialHits) {
      val entityAabb = potentialHit.getEntityBoundingBox

      if (obb.intersects(entityAabb)) {
        val rayTraceResult = entityAabb.calculateIntercept(start.toVec3d, end.toVec3d)
        if (rayTraceResult != null) {
          val rayToHit = world.rayTraceBlocks(start.toVec3d, rayTraceResult.hitVec, false, true, false)
          if (rayToHit != null && (rayToHit.typeOfHit == RayTraceResult.Type.BLOCK)) groundRay = rayToHit
          else {
            val rayHit = new RayTraceResult(potentialHit)
            impactEntity(rayHit)
            impact(rayHit)
          }
        }
      }
    }

    if (groundRay == null) {
      val ray = world.rayTraceBlocks(start.toVec3d, end.toVec3d, false, true, false)
      if (ray != null && (ray.typeOfHit == RayTraceResult.Type.BLOCK)) groundRay = ray
    }

    if (groundRay != null) {
      if (world.getBlockState(groundRay.getBlockPos).getBlock == Blocks.PORTAL) danmaku.setPortal(groundRay.getBlockPos)
      impactBlock(groundRay)
      impact(groundRay)
    }
  }

  /**
    * Sets the direction based on the rotation.
    */
  protected def rotate(): Unit =
    danmaku.setDirection(danmaku.getDirection.rotate(danmaku.getRotationData.getRotationQuat))

  protected def rotateTowardsMovement(): Unit = {
    if ((danmaku.motionX !=~ 0D) &&
        (danmaku.motionY !=~ 0D) &&
        (danmaku.motionZ !=~ 0D)) { //Projectile helper is buggy. We use this instead

      val motion = new Vector3(danmaku.motionX, danmaku.motionY, danmaku.motionZ).normalize
      danmaku.prevRotationPitch = danmaku.rotationPitch
      danmaku.prevRotationYaw = danmaku.rotationYaw
      danmaku.rotationPitch = motion.pitch.toFloat
      danmaku.rotationYaw = motion.yaw.toFloat
    }
  }
}
