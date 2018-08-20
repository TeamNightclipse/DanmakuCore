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
package net.katsstuff.teamnightclipse.danmakucore.danmaku.vectorization

import java.util

import scala.collection.mutable.ArrayBuffer

import net.katsstuff.teamnightclipse.danmakucore.danmkau.vectorization.DanmakuCluster
import net.katsstuff.teamnightclipse.danmakucore.data.OrientedBoundingBox
import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.minecraft.entity.Entity
import net.minecraft.util.math.RayTraceResult
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanCoreImplicits._

abstract class VectorizedSubEntity {

  def updateMovement(cluster: DanmakuCluster): Unit

  def hitCheck(cluster: DanmakuCluster): Unit

  def handleImpacts(
      cluster: DanmakuCluster,
      impacts: Array[ArrayBuffer[RayTraceResult]],
      groundHits: Array[RayTraceResult]
  ): Unit

}
object VectorizedSubEntity {

  private[this] val MaxPoolSize = 16

  private[this] val impactsPool = new ArrayBuffer[Array[ArrayBuffer[RayTraceResult]]]()
  private[this] val groundHitsPool = new ArrayBuffer[Array[RayTraceResult]]()

  private def useArrays(f: (Array[ArrayBuffer[RayTraceResult]], Array[RayTraceResult]) => Unit): Unit = {
    val impactsArr = impactsPool.synchronized {
      if(impactsPool.nonEmpty) {
        val impacts = impactsPool.remove(impactsPool.size - 1)
        util.Arrays.fill(impacts.asInstanceOf[Array[Object]], null.asInstanceOf[Object])
        impacts
      }
      else new Array[ArrayBuffer[RayTraceResult]](DanmakuCluster.DANMAKUS_PER_CLUSTER)
    }

    val groundHitsArr = groundHitsPool.synchronized {
      if(groundHitsPool.nonEmpty) {
        val impacts = groundHitsPool.remove(groundHitsPool.size - 1)
        util.Arrays.fill(impacts.asInstanceOf[Array[Object]], null.asInstanceOf[Object])
        impacts
      }
      else new Array[RayTraceResult](DanmakuCluster.DANMAKUS_PER_CLUSTER)
    }

    f(impactsArr, groundHitsArr)

    impactsPool.synchronized {
      if(impactsPool.size < MaxPoolSize) {
        impactsPool += impactsArr
      }
    }

    groundHitsPool.synchronized {
      if(groundHitsPool.size < MaxPoolSize) {
        groundHitsPool += groundHitsArr
      }
    }
  }

  def defaultHitCheck(
      cluster: DanmakuCluster,
      subEntity: VectorizedSubEntity,
      exclude: Entity => java.lang.Boolean
  ): Unit = {
    var i        = 0
    val hitCheck = cluster.hitCheck

    useArrays { (impacts, groundHits) =>
      while (i < cluster.size) {
        if(!cluster.isDead(hitCheck)(i)) {
          val pos       = Vector3(cluster.posX(hitCheck)(i), cluster.posY(hitCheck)(i), cluster.posZ(hitCheck)(i))
          val direction = Vector3(cluster.dirX(hitCheck)(i), cluster.dirY(hitCheck)(i), cluster.dirZ(hitCheck)(i))
          val motion    = Vector3(cluster.motX(hitCheck)(i), cluster.motY(hitCheck)(i), cluster.motZ(hitCheck)(i))

          val shot = cluster.shot

          val rawBoundingBoxes = cluster.rawBoundingBoxes(i)
          val boundingBoxes    = new Array[OrientedBoundingBox](rawBoundingBoxes.length)

          var j = 0
          while (j < rawBoundingBoxes.length) {
            boundingBoxes(j) = rawBoundingBoxes(j).toWorldSpace(pos, cluster.orient(hitCheck)(i))
            j += 1
          }

          val start = pos.offset(direction, -shot.sizeZ / 2)
          val end   = start.offset(direction, shot.sizeZ).add(motion)

          val bb = cluster.rawEncompassingAABB(i).offset(pos.x, pos.y, pos.z).expand(motion.x, motion.y, motion.z).grow(1D)
          val potentialHits = cluster.world.collectEntitiesWithinAABB[Entity, Entity](bb) {
            case e if e.canBeCollidedWith && !e.noClip && exclude(e) => e
          }

          var groundRay: RayTraceResult = null

          potentialHits.foreach { potentialHit =>
            val entityAabb = potentialHit.getEntityBoundingBox
            if (boundingBoxes.exists(_.intersects(entityAabb))) {
              val rayTraceResult = entityAabb.calculateIntercept(start.toVec3d, end.toVec3d)
              if (rayTraceResult != null) {
                val rayToHit = cluster.world.rayTraceBlocks(start.toVec3d, rayTraceResult.hitVec, false, true, false)
                if (rayToHit != null && (rayToHit.typeOfHit == RayTraceResult.Type.BLOCK)) {
                  groundRay = rayToHit
                } else {
                  val rayHit = new RayTraceResult(potentialHit)
                  if (impacts(i) == null) {
                    impacts(i) = new ArrayBuffer[RayTraceResult]
                  }

                  impacts(i) += rayHit
                }
              }
            }
          }

          if (groundRay == null) {
            val ray = cluster.world.rayTraceBlocks(start.toVec3d, end.toVec3d, false, true, false)
            if (ray != null && (ray.typeOfHit == RayTraceResult.Type.BLOCK)) groundRay = ray
          }

          if (groundRay != null) {
            groundHits(i) = groundRay
          }

          subEntity.handleImpacts(cluster, impacts, groundHits)

          i += 1
        }
      }
    }
  }
}
