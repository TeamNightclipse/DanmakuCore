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
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World

private[danmakucore] class SubEntityTypeTeleport(name: String) extends SubEntityType(name) {
  override def instantiate(world: World, entityDanmaku: EntityDanmaku) =
    new SubEntityTeleport(world, entityDanmaku)
}

private[subentity] class SubEntityTeleport(world: World, danmaku: EntityDanmaku) extends SubEntityDefault(world, danmaku) {
  override def impact(rayTrace: RayTraceResult): Unit = {
    danmaku.user.foreach { usr =>
      usr.rotationYaw = danmaku.rotationYaw
      usr.rotationPitch = danmaku.rotationPitch
      usr.setPositionAndUpdate(danmaku.posX, danmaku.posY, danmaku.posZ)
    }
    super.impact(rayTrace)
  }
}
