/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmaku

import net.katsstuff.danmakucore.data.{MovementData, OrientedBoundingBox, Quat, RotationData, ShotData, Vector3}
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntity
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World

import net.katsstuff.danmakucore.helper.MathUtil._

object DanmakuState {
  private var id: Int = 0

  def nextId(): Int = {
    val next = id
    id += 1
    next
  }
}
case class DanmakuState(
    id: Int,
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

  lazy val orientedBoundingBox = OrientedBoundingBox(roughScaledBoundingBox(rotate = false), pos, orientation)

  lazy val roughBoundingBox: AxisAlignedBB = roughScaledBoundingBox(rotate = true)

  private def roughScaledBoundingBox(rotate: Boolean) = {
    val x = pos.x
    val y = pos.y
    val z = pos.z
    if (rotate) {
      val size  = new Vector3(shot.sizeX, shot.sizeY, shot.sizeZ).rotate(orientation)
      val xSize = size.x / 2F
      val ySize = size.y / 2F
      val zSize = size.z / 2F
      new AxisAlignedBB(x - xSize, y - ySize, z - zSize, x + xSize, y + ySize, z + zSize)
    } else {
      val xSize = shot.sizeX / 2F
      val ySize = shot.sizeY / 2F
      val zSize = shot.sizeZ / 2F
      new AxisAlignedBB(x - xSize, y - ySize, z - zSize, x + xSize, y + ySize, z + zSize)
    }
  }

  //noinspection MutatorLikeMethodIsParameterless
  def updateForm: Option[DanmakuUpdate] =
    shot.form.onTick(this)

  //noinspection MutatorLikeMethodIsParameterless
  def updateSubEntity: Option[DanmakuUpdate] = subEntity.subEntityTick(this)

  def update: Option[DanmakuUpdate] = {
    if (ticksExisted > shot.end) None
    else DanmakuUpdate.andThen(updateSubEntity)(_.updateForm)
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