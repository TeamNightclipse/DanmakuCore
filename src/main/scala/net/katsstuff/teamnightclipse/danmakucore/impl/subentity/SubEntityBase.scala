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
package net.katsstuff.teamnightclipse.danmakucore.impl.subentity

import java.util.Random
import java.util.function.Predicate

import net.katsstuff.teamnightclipse.danmakucore.capability.danmakuhit.CapabilityDanmakuHitBehaviorJ
import net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity.SubEntity
import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DamageSourceDanmaku, DanmakuState, DanmakuUpdate}
import net.katsstuff.teamnightclipse.danmakucore.handler.ConfigHandler
import net.katsstuff.teamnightclipse.danmakucore.lib.LibSounds
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanmakuHelper
import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.{Entity, EntityLivingBase, MultiPartEntityPart}
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}

import scala.annotation.tailrec
import scala.collection.JavaConverters._

abstract class SubEntityBase extends SubEntity {
  protected val rand = new Random

  /**
    * Called when the danmaku hits a block.
    */
  protected def impactBlock(danmaku: DanmakuState, aabb: AxisAlignedBB, block: IBlockState): DanmakuUpdate = {
    val shot = danmaku.shot
    if (shot.sizeZ <= 1F || shot.sizeZ / shot.sizeX <= 3 || shot.sizeZ / shot.sizeY <= 3) DanmakuUpdate.empty
    else DanmakuUpdate.noUpdates(danmaku)
  }

  /**
    * Called when the danmaku hits an entity.
    */
  protected def impactEntity(danmaku: DanmakuState, entity: Entity): DanmakuUpdate =
    attackEntity(danmaku, entity)

  protected def attackEntity(danmaku: DanmakuState, entity: Entity): DanmakuUpdate = {
    val shot        = danmaku.shot
    val averageSize = (shot.sizeY + shot.sizeX + shot.sizeZ) / 3

    val res = if (averageSize < 0.7F) DanmakuUpdate.empty else DanmakuUpdate.noUpdates(danmaku)

    res.addCallbackIf(!danmaku.world.isRemote) {
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
    }
  }

  /**
    * Add the gravity to the motion.
    */
  protected def updateMotionWithGravity(danmaku: DanmakuState, motion: Vector3): Vector3 =
    motion + danmaku.movement.gravity * ((danmaku.entity.ticksExisted * danmaku.entity.ticksExisted) / 2D)

  protected def hitCheck(danmaku: DanmakuState, exclude: Predicate[Entity]): DanmakuUpdate =
    hitCheck(danmaku, exclude.asScala)

  protected def hitCheck(danmaku: DanmakuState, exclude: Entity => Boolean): DanmakuUpdate = {
    val boundingBoxes = danmaku.boundingBoxes

    val bb = danmaku.encompassingAABB
    val potentialHits = danmaku.world.collectEntitiesWithinAABB[Entity, Entity](bb) {
      case e if e.canBeCollidedWith && !e.noClip && exclude(e) => e
    }

    val afterEntityImpact =
      potentialHits.foldLeft[DanmakuUpdate](DanmakuUpdate.noUpdates(danmaku)) { (update, potentialHit) =>
        update.state match {
          case Some(_) =>
            val entityAabb = potentialHit.getEntityBoundingBox
            if (boundingBoxes.exists(_.intersects(entityAabb)))
              update.andThen(impactEntity(_, potentialHit))
            else
              update
          case None => update
        }
      }

    val blockBoundingBoxes = (danmaku: DanmakuState) =>
      danmaku.world
        .getCollisionBoxes(null, bb)
        .asScala
        .sortBy(_.getCenter.distanceTo(danmaku.pos))
        .foldLeft(afterEntityImpact) { (update, hitBB) =>
          update.state match {
            case Some(_) =>
              if (boundingBoxes.exists(_.intersects(hitBB)))
                update.andThen(impactBlock(_, hitBB, danmaku.world.getBlockState(new BlockPos(hitBB.getCenter))))
              else
                update
            case None => update
          }
      }

    afterEntityImpact.andThen(blockBoundingBoxes)
  }

  /**
    * Creates a direction based on the rotation.
    */
  protected def rotate(danmaku: DanmakuState): Vector3 =
    danmaku.direction.rotate(danmaku.rotation.rotationQuat)
}
