/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.subentity

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.katsstuff.danmakucore.entity.danmaku.subentity.SubEntityType
import net.minecraft.world.World

class SubEntityTypeDefault(name: String) extends SubEntityType(name) {
  override def instantiate(world: World, entityDanmaku: EntityDanmaku) = new SubEntityDefault(world, entityDanmaku)
}
class SubEntityDefault(world: World, danmaku: EntityDanmaku)
  extends SubEntityBase(world, danmaku) {

  override def subEntityTick(): Unit = {
    val shot = danmaku.getShotData
    val delay = shot.delay

    if (delay > 0) {
      danmaku.ticksExisted -= 1
      if (delay - 1 == 0) if (shot.end == 1) {
        danmaku.delete()
        return
      }
      else if (!danmaku.world.isRemote) danmaku.resetMotion()
      else {
        danmaku.motionX = 0
        danmaku.motionY = 0
        danmaku.motionZ = 0
      }

      danmaku.setShotData(shot.setDelay(delay - 1))
    }
    else {
      if (!world.isRemote) {
        val rotation = danmaku.rotation
        if (rotation.isEnabled && danmaku.ticksExisted < rotation.getEndTime) rotate()

        danmaku.accelerate(danmaku.getCurrentSpeed)
        updateMotionWithGravity()
        hitCheck(entity => !danmaku.user.contains(entity) && !danmaku.source.contains(entity))
      }

      rotateTowardsMovement()

      if (danmaku.isInWater) waterMovement()
    }
  }
}