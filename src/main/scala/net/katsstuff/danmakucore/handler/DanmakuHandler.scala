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
import scala.collection.parallel.immutable.ParVector

import net.katsstuff.danmakucore.data.{MovementData, OrientedBoundingBox, Quat, RotationData, ShotData, Vector3}
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity
import net.katsstuff.danmakucore.helper.MathUtil._
import net.minecraft.client.Minecraft
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

case class DanmakuState(
    world: World,
    isRemote: Boolean,
    pos: Vector3,
    prevPos: Vector3,
    motion: Vector3,
    direction: Vector3,
    orientation: Quat,
    prevOrientation: Quat,
    user: Option[EntityLivingBase],
    source: Option[Entity],
    shot: ShotData,
    subEntity: SubEntity,
    movement: MovementData,
    rotation: RotationData,
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

  //noinspection MutatorLikeMethodIsParameterless
  def updateForm: Option[DanmakuState] =
    shot.form.onTick(this)

  //noinspection MutatorLikeMethodIsParameterless
  def updateSubEntity: Option[DanmakuState] = subEntity.subEntityTick(this)

  def update: Option[DanmakuState] = {
    if (ticksExisted > shot.end) None
    else {
      updateSubEntity.flatMap(_.updateForm)
    }
  }

  def currentSpeed: Double = motion.length

  def accelerate: Vector3 = {
    val speedAccel      = movement.speedAcceleration
    val upperSpeedLimit = movement.upperSpeedLimit
    val lowerSpeedLimit = movement.lowerSpeedLimit
    if (currentSpeed >=~ upperSpeedLimit && speedAccel >= 0D) setSpeed(upperSpeedLimit)
    else if (currentSpeed <=~ lowerSpeedLimit && speedAccel <= 0D) setSpeed(lowerSpeedLimit)
    else {
      val newMotion       = addSpeed(speedAccel)
      val newCurrentSpeed = newMotion.length
      if (newCurrentSpeed >~ upperSpeedLimit) setSpeed(upperSpeedLimit)
      else if (newCurrentSpeed <~ lowerSpeedLimit) setSpeed(lowerSpeedLimit)
      else newMotion
    }
  }

  def setSpeed(speed: Double): Vector3 = direction * speed
  def addSpeed(speed: Double): Vector3 = motion + direction * speed

  def resetMotion: (Vector3, Quat) = {
    (
      setSpeed(movement.getSpeedOriginal),
      Quat.fromEuler(direction.yaw.toFloat, direction.pitch.toFloat, orientation.roll.toFloat)
    )
  }
}

trait DanmakuHandler {
  private var _danmaku   = Vector.empty[DanmakuState]
  private var newDanmaku = new ArrayBuffer[DanmakuState]
  var working: ParVector[DanmakuState] = _

  def danmaku: Vector[DanmakuState] = _danmaku

  def start(): Unit = {
    working = _danmaku.par.flatMap(_.update) ++ newDanmaku.par.flatMap(_.update)
    newDanmaku.clear()
  }

  def stop(): Unit = {
    _danmaku = working.seq
    working = null
  }

  def spawnDanmaku(state: DanmakuState): Unit = newDanmaku += state
}

@SideOnly(Side.SERVER)
class ServerDanmakuHandler extends DanmakuHandler {

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  def onTick(event: TickEvent.ServerTickEvent): Unit = {
    if (event.phase == Phase.START) {
      start()
    } else if (event.phase == Phase.END) {
      stop()
    }
  }
}

@SideOnly(Side.CLIENT)
class ClientDanmakuHandler extends DanmakuHandler {

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  def onTick(event: TickEvent.ClientTickEvent): Unit = {
    if (!Minecraft.getMinecraft.isGamePaused) {
      if (event.phase == Phase.START) {
        start()
      } else if (event.phase == Phase.END) {
        stop()
      }
    }
  }
}
