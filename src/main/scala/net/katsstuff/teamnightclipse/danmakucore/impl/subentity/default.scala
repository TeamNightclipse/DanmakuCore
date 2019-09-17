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
      val rotation     = danmaku.rotation
      val shouldRotate = rotation.isEnabled && danmaku.ticksExisted < rotation.getEndTime
      val newDirection = if (shouldRotate) rotate(danmaku) else danmaku.direction
      val (newPrevOrientation, newOrientation) =
        if (shouldRotate) (danmaku.orientation, danmaku.orientation * rotation.rotationQuat)
        else (danmaku.prevOrientation, danmaku.orientation)

      val newMotion = updateMotionWithGravity(danmaku, danmaku.accelerate)
      val updated = danmaku.copy(
        entity = danmaku.entity.copy(
          motion = newMotion,
          pos = danmaku.pos + newMotion,
          prevPos = danmaku.pos,
          direction = newDirection,
          orientation = newOrientation,
          prevOrientation = newPrevOrientation,
          ticksExisted = danmaku.ticksExisted + 1
        )
      )

      hitCheck(updated, entity => !danmaku.user.contains(entity) && !danmaku.source.contains(entity))
    }
  }
}
