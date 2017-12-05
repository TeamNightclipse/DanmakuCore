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

import scala.annotation.tailrec

import net.katsstuff.danmakucore.capability.danmakuhit.CapabilityDanmakuHitBehaviorJ
import net.katsstuff.danmakucore.data.Vector3
import net.katsstuff.danmakucore.entity.danmaku.DamageSourceDanmaku
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity
import net.katsstuff.danmakucore.handler.{ConfigHandler, DanmakuState}
import net.katsstuff.danmakucore.lib.LibSounds
import net.katsstuff.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.danmakucore.scalastuff.DanmakuHelper
import net.minecraft.entity.{Entity, EntityLivingBase, MultiPartEntityPart}
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.fml.common.FMLCommonHandler

abstract class SubEntityBase extends SubEntity {
  protected val rand = new Random

  /**
    * Called when the danmaku hits a block.
    */
  protected def impactBlock(danmaku: DanmakuState, raytrace: RayTraceResult): Option[DanmakuState] = {
    val shot = danmaku.shot
    if (shot.sizeZ <= 1F || shot.sizeZ / shot.sizeX <= 3 || shot.sizeZ / shot.sizeY <= 3) None else Some(danmaku)
  }

  /**
    * Called when the danmaku hits an entity.
    */
  protected def impactEntity(danmaku: DanmakuState, raytrace: RayTraceResult): Option[DanmakuState] =
    attackEntity(danmaku, raytrace.entityHit)

  protected def attackEntity(danmaku: DanmakuState, entity: Entity): Option[DanmakuState] = {
    val shot        = danmaku.shot
    val averageSize = (shot.sizeY + shot.sizeX + shot.sizeZ) / 3

    FMLCommonHandler
      .instance()
      .getMinecraftServerInstance
      .addScheduledTask(() => {
        val source = DamageSourceDanmaku.create(danmaku)
        val damage =
          DanmakuHelper.adjustDanmakuDamage(danmaku.user, entity, shot.damage, ConfigHandler.danmaku.danmakuLevel)

        if (entity.hasCapability(CapabilityDanmakuHitBehaviorJ.HIT_BEHAVIOR, null)) {
          entity
            .getCapability(CapabilityDanmakuHitBehaviorJ.HIT_BEHAVIOR, null)
            .onHit(danmaku, entity, damage, source)
        } else {
          entity.attackEntityFrom(source, damage)
        }

        @tailrec
        def hasLittleHealth(entity: Entity): Boolean = {
          entity match {
            case living: EntityLivingBase => living.getHealth / living.getMaxHealth < 0.1
            case multiPart: MultiPartEntityPart =>
              multiPart.parent match {
                case parent: EntityLivingBase => hasLittleHealth(parent)
                case _                        => false
              }
            case _ => false
          }
        }

        if (hasLittleHealth(entity)) {
          entity.playSound(LibSounds.DAMAGE_LOW, 1F, 1F)
        } else {
          entity.playSound(LibSounds.DAMAGE, 1F, 1F)
        }
      })

    if (averageSize < 0.7F) None else Some(danmaku)
  }

  /**
    * Called on any impact. Called after [[impactBlock]] and [[impactEntity]].
    */
  protected def impact(danmaku: DanmakuState, raytrace: RayTraceResult): Option[DanmakuState] = Some(danmaku)

  /**
    * Add the gravity to the motion.
    */
  protected def updateMotionWithGravity(danmaku: DanmakuState, motion: Vector3): Vector3 =
    motion + danmaku.movement.getGravity

  protected def hitCheck(danmaku: DanmakuState, exclude: Predicate[Entity]): Unit = hitCheck(danmaku, exclude.asScala)

  protected def hitCheck(danmaku: DanmakuState, exclude: Entity => Boolean): Option[DanmakuState] = {
    val direction   = danmaku.direction
    val shot        = danmaku.shot
    val motion      = danmaku.motion
    val boundingBox = danmaku.boundingBox

    val start = danmaku.pos.offset(direction, -shot.sizeZ / 2)
    val end   = start.offset(direction, shot.sizeZ).add(motion)

    val bb = boundingBox.aabb.expand(motion.x, motion.y, motion.z).grow(1D)
    val potentialHits = danmaku.world.collectEntitiesWithinAABB[Entity, Entity](bb) {
      case e if e.canBeCollidedWith && !e.noClip && exclude(e) => e
    }

    var groundRay: RayTraceResult = null
    val afterEntityImpact = potentialHits.foldLeft[Option[DanmakuState]](Some(danmaku)) { (optState, potentialHit) =>
      optState match {
        case Some(state) =>
          val entityAabb = potentialHit.getEntityBoundingBox

          if (boundingBox.intersects(entityAabb)) {
            val rayTraceResult = entityAabb.calculateIntercept(start.toVec3d, end.toVec3d)
            if (rayTraceResult != null) {
              val rayToHit = danmaku.world.rayTraceBlocks(start.toVec3d, rayTraceResult.hitVec, false, true, false)
              if (rayToHit != null && (rayToHit.typeOfHit == RayTraceResult.Type.BLOCK)) {
                groundRay = rayToHit
                optState
              } else {
                val rayHit = new RayTraceResult(potentialHit)
                impactEntity(state, rayHit).flatMap(impact(_, rayHit))
              }
            } else optState
          } else optState
        case None => None
      }
    }

    if (groundRay == null) {
      val ray = danmaku.world.rayTraceBlocks(start.toVec3d, end.toVec3d, false, true, false)
      if (ray != null && (ray.typeOfHit == RayTraceResult.Type.BLOCK)) groundRay = ray
    }

    if (groundRay != null) {
      afterEntityImpact.flatMap(impactBlock(_, groundRay)).flatMap(impact(_, groundRay))
    } else afterEntityImpact
  }

  /**
    * Creates a direction based on the rotation.
    */
  protected def rotate(danmaku: DanmakuState): Vector3 =
    danmaku.direction.rotate(danmaku.rotation.rotationQuat)
}
