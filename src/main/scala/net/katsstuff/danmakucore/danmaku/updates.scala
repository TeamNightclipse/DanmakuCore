/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmaku

import net.katsstuff.danmakucore.data.{MovementData, Quat, RotationData, ShotData, Vector3}

sealed trait DanmakuUpdateSignal
object DanmakuUpdateSignal {
  case class ChangedPos(pos: Vector3)                        extends DanmakuUpdateSignal
  case class ChangedMotion(motion: Vector3)                  extends DanmakuUpdateSignal
  case class ChangedDirection(direction: Vector3)            extends DanmakuUpdateSignal
  case class ChangedOrientation(orientation: Quat)           extends DanmakuUpdateSignal
  case class ChangedShotData(shotData: ShotData)             extends DanmakuUpdateSignal
  case class ChangedMovementData(movementData: MovementData) extends DanmakuUpdateSignal
  case class ChangedRotationData(rotationData: RotationData) extends DanmakuUpdateSignal
  case object SetDead                                        extends DanmakuUpdateSignal
  case object Finish                                         extends DanmakuUpdateSignal
}
case class DanmakuChanges(id: Int, signals: Seq[DanmakuUpdateSignal])
