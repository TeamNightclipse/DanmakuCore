/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.subentity

import net.katsstuff.danmakucore.entity.danmaku.subentity.{SubEntity, SubEntityType}
import net.katsstuff.danmakucore.handler.DanmakuState

class SubEntityTypeDefault(name: String) extends SubEntityType(name) {
  override def instantiate: SubEntity = new SubEntityDefault
}
class SubEntityDefault extends SubEntityBase {

  override def subEntityTick(danmaku: DanmakuState): Option[DanmakuState] = {
    val shot  = danmaku.shot
    val delay = shot.delay

    if (delay > 0) {
      if (delay - 1 == 0) {
        if (shot.end == 1) {
          None
        } else {
          val (newMotion, newOrientation) = danmaku.resetMotion

          Some(
            danmaku.copy(
              shot = shot.copy(delay = delay - 1),
              motion = newMotion,
              orientation = newOrientation,
              prevOrientation = danmaku.orientation
            )
          )
        }
      } else Some(danmaku.copy(shot = shot.copy(delay = delay - 1)))
    } else {
      val rotation = danmaku.rotation
      val newDirection =
        if (rotation.isEnabled && danmaku.ticksExisted < rotation.getEndTime) rotate(danmaku) else danmaku.direction

      val newMotion = updateMotionWithGravity(danmaku, danmaku.accelerate)
      val updated = danmaku.copy(
        motion = newMotion,
        pos = danmaku.pos + newMotion,
        prevPos = danmaku.pos,
        direction = newDirection,
        ticksExisted = danmaku.ticksExisted + 1
      )

      hitCheck(updated, entity => !danmaku.user.contains(entity) && !danmaku.source.contains(entity))
    }
  }
}
