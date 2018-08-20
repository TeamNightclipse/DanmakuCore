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
package danmaku

final case class ShotData(
    edgeColor: Int = 0xFF0000,
    coreColor: Int = 0xFFFFFF,
    damage: Float = 0.5F,
    sizeX: Float = 0.5F,
    sizeY: Float = 0.5F,
    sizeZ: Float = 0.5F,
    startDelay: Int = 0,
    end: Int = 80
)

final case class MovementData(
    speedOriginal: Double,
    lowerSpeedLimit: Double,
    upperSpeedLimit: Double,
    speedAcceleration: Double,
    gravity: Vector3
)


final case class RotationData(
    enabled: Boolean,
    rotationQuat: Quat,
    endTime: Int
)

case class Danmaku(
    shot: ShotData,
    pos: Vector3,
    prevPos: Vector3,
    direction: Vector3,
    motion: Vector3,
    orientation: Quat,
    movement: MovementData,
    rotation: Quat,
    ticksExisted: Int,
    currentDelay: Int
) {

  final def accelerate: Vector3 = {
    import MathUtil._
    val currentSpeed    = motion.length
    val speedAccel      = movement.speedAcceleration
    val upperSpeedLimit = movement.upperSpeedLimit
    val lowerSpeedLimit = movement.lowerSpeedLimit

    if (currentSpeed >=~ upperSpeedLimit && speedAccel >= 0D) direction * upperSpeedLimit
    else if (currentSpeed <=~ lowerSpeedLimit && speedAccel <= 0D) direction * lowerSpeedLimit
    else {
      val newMotion       = motion.offset(direction, speedAccel)
      val newCurrentSpeed = newMotion.length
      if (newCurrentSpeed >~ upperSpeedLimit) direction * upperSpeedLimit
      else if (newCurrentSpeed <~ lowerSpeedLimit) direction * lowerSpeedLimit
      else newMotion
    }
  }

  def update: DanmakuUpdate = {
    if (currentDelay > 0) {
      if (currentDelay - 1 == 0) {
        if (shot.end == 1) {
          DanmakuUpdate.empty
        } else {
          //Think we can get away with not sending an update here
          DanmakuUpdate.noUpdates(
            copy(
              motion = direction * movement.speedOriginal,
              currentDelay = currentDelay - 1
            )
          )
        }
      } else DanmakuUpdate.noUpdates(copy(currentDelay = currentDelay - 1))
    } else {
      val newDirection = rotation.rotate(direction)

      val newMotion = accelerate + movement.gravity
      val updated = copy(
        motion = newMotion,
        pos = pos + newMotion,
        prevPos = pos,
        direction = newDirection,
        ticksExisted = ticksExisted + 1
      )

      DanmakuUpdate.noUpdates(updated)
    }
  }
}

object DanmakuUpdate {
  val empty: DanmakuUpdate = DanmakuUpdate(None, Nil)

  def noUpdates(state: Danmaku): DanmakuUpdate = DanmakuUpdate(Some(state), Nil)
}
case class DanmakuUpdate(state: Option[Danmaku], callbacks: Seq[() => Unit]) {

  @inline def isEmpty: Boolean  = state.isEmpty
  @inline def nonEmpty: Boolean = state.nonEmpty

  def addCallbackFunc(f: () => Unit): DanmakuUpdate = copy(callbacks = callbacks :+ f)
  def addCallback(f: => Unit): DanmakuUpdate        = addCallbackFunc(() => f)
  def addCallback(f: Runnable): DanmakuUpdate       = addCallbackFunc(() => f.run())

  def addCallbackIfFunc(cond: Boolean)(f: () => Unit): DanmakuUpdate =
    if (cond) copy(callbacks = callbacks :+ f) else this

  def addCallbackIf(cond: Boolean)(f: => Unit): DanmakuUpdate  = addCallbackIfFunc(cond)(() => f)
  def addCallbackIf(cond: Boolean, f: Runnable): DanmakuUpdate = addCallbackIfFunc(cond)(() => f.run())

  def andThen(f: Danmaku => DanmakuUpdate): DanmakuUpdate = state match {
    case Some(danState) =>
      val newUpdate = f(danState)
      newUpdate.copy(callbacks = callbacks ++ newUpdate.callbacks)
    case None => this
  }

  def andThenWithCallbacks(defaultState: Danmaku)(f: Danmaku => DanmakuUpdate): DanmakuUpdate = state match {
    case Some(danState) =>
      val newUpdate = f(danState)
      newUpdate.copy(callbacks = callbacks ++ newUpdate.callbacks)
    case None =>
      val callbackUpdate = f(defaultState)
      copy(callbacks = callbacks ++ callbackUpdate.callbacks)
  }
}
