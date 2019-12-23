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

import scala.annotation.tailrec
import net.katsstuff.teamnightclipse.danmakucore.helper.DebugHelper
import net.katsstuff.teamnightclipse.danmakucore.entity.living.TEntityDanmakuCreature
import net.katsstuff.teamnightclipse.danmakucore.handler.ConfigHandler
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.pathfinding.{Path, PathNavigateFlying}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class PathNavigateHover(flyingMob: TEntityDanmakuCreature, _world: World)
    extends PathNavigateFlying(flyingMob, _world) {

  override def debugPathFinding(): Unit = {
    if (currentPath != null && ConfigHandler.client.entities.debugPathfinding) {
      val path = currentPath
      DebugHelper.setPathfinding(true)
      Minecraft.getMinecraft.addScheduledTask(new Runnable {
        override def run(): Unit = DebugHelper.renderPath(path, flyingMob.getEntityId)
      })
    }
  }

  override def getPathToEntityLiving(entityIn: Entity): Path = getPathToPos(new BlockPos(entityIn).up())

  override protected def pathFollow(): Unit = {
    if (flyingMob.isFlying) {
      val vec3d = getEntityPosition
      val f     = entity.width * entity.width

      if (vec3d.squareDistanceTo(currentPath.getVectorFromIndex(entity, currentPath.getCurrentPathIndex)) < f) {
        currentPath.incrementPathIndex()
      }

      @tailrec
      def inner(j: Int): Unit = {
        if (j > currentPath.getCurrentPathIndex) {
          val vec3d1 = currentPath.getVectorFromIndex(entity, j)
          if (vec3d1.squareDistanceTo(vec3d) <= 36.0D && isDirectPathBetweenPoints(vec3d, vec3d1, 0, 0, 0)) {
            currentPath.setCurrentPathIndex(j)
          } else inner(j - 1)
        }
      }

      inner(Math.min(currentPath.getCurrentPathIndex + 6, currentPath.getCurrentPathLength - 1))
      checkForStuck(vec3d)
    } else super.pathFollow()
  }
}
