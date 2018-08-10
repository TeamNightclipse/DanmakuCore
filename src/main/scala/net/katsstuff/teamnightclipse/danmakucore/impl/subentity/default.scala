/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.impl.subentity

import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DanmakuState, DanmakuUpdate}
import net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity.{SubEntity, SubEntityType}

class SubEntityTypeDefault(name: String) extends SubEntityType(name) {
  override def instantiate: SubEntity = new SubEntityDefault
}
class SubEntityDefault extends SubEntityBase {

  override def subEntityTick(danmaku: DanmakuState): DanmakuUpdate = {
    val shot  = danmaku.shot
    val delay = shot.delay

    if (delay > 0) {
      if (delay - 1 == 0) {
        if (shot.end == 1) {
          DanmakuUpdate.empty
        } else {
          //Think we can get away with not sending an update here
          DanmakuUpdate.noUpdates(
            danmaku.copy(
              entity = danmaku.entity.copy(motion = danmaku.resetMotion),
              extra = danmaku.extra.copy(shot = shot.copy(delay = delay - 1))
            )
          )
        }
      } else DanmakuUpdate.noUpdates(danmaku.copy(extra = danmaku.extra.copy(shot = shot.copy(delay = delay - 1))))
    } else {
      val rotation = danmaku.rotation
      val newDirection =
        if (rotation.isEnabled && danmaku.ticksExisted < rotation.getEndTime) rotate(danmaku) else danmaku.direction

      val newMotion = updateMotionWithGravity(danmaku, danmaku.accelerate)
      val updated = danmaku.copy(
        entity = danmaku.entity.copy(
          motion = newMotion,
          pos = danmaku.pos + newMotion,
          prevPos = danmaku.pos,
          direction = newDirection,
          ticksExisted = danmaku.ticksExisted + 1
        )
      )

      hitCheck(updated, entity => !danmaku.user.contains(entity) && !danmaku.source.contains(entity))
    }
  }
}
