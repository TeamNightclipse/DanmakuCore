/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.handler

import scala.collection.mutable.ArrayBuffer

import net.katsstuff.danmakucore.data.{OrientedBoundingBox, Quat, ShotData, Vector3}
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.gameevent.TickEvent

case class DanmakuState(
    dimension: Int,
    pos: Vector3,
    prevPos: Vector3,
    direction: Vector3,
    orientation: Quat,
    prevOrientation: Quat,
    shot: ShotData,
    ticksExisted: Int,
    renderBrightness: Float
) {

  private def aabb = {
    val xSize = shot.sizeX / 2F
    val ySize = shot.sizeY / 2F
    val zSize = shot.sizeZ / 2F
    new AxisAlignedBB(pos.x - xSize, pos.y - ySize, pos.z - zSize, pos.x + xSize, pos.y + ySize, pos.z + zSize)
  }

  lazy val boundingBox = OrientedBoundingBox(aabb, pos, orientation)

  def update: Option[DanmakuState] = if (ticksExisted < shot.end) Some(copy(ticksExisted = ticksExisted + 1)) else None
}

class DanmakuHandler {

  private var _danmaku   = Vector.empty[DanmakuState]
  private var newDanmaku = new ArrayBuffer[DanmakuState]

  def danmaku: Vector[DanmakuState] = _danmaku

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  def onTick(event: TickEvent.ServerTickEvent): Unit = {
    _danmaku = _danmaku.flatMap(_.update) ++ newDanmaku.flatMap(_.update)
    newDanmaku.clear()
  }

  def spawnDanmaku(state: DanmakuState): Unit = newDanmaku += state
}
