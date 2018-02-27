package net.katsstuff.danmakucore.entity.living.ai

import scala.annotation.tailrec

import net.katsstuff.danmakucore.entity.living.TEntityDanmakuCreature
import net.katsstuff.danmakucore.helper.DebugHelper
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.pathfinding.{Path, PathNavigateFlying}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class PathNavigateHover(flyingMob: TEntityDanmakuCreature, _world: World)
    extends PathNavigateFlying(flyingMob, _world) {

  override def debugPathFinding(): Unit = {
    if (currentPath != null) {
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
