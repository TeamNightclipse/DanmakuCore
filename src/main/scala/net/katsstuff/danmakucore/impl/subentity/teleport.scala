/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.subentity

import net.katsstuff.danmakucore.danmaku.{DanmakuState, DanmakuUpdate, DanmakuUpdateSignal}
import net.katsstuff.danmakucore.entity.danmaku.subentity.{SubEntity, SubEntityType}
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.fml.common.FMLCommonHandler

private[danmakucore] class SubEntityTypeTeleport(name: String) extends SubEntityType(name) {
  override def instantiate: SubEntity =
    new SubEntityTeleport
}

private[subentity] class SubEntityTeleport extends SubEntityDefault {
  protected override def impact(danmaku: DanmakuState, rayTrace: RayTraceResult): Option[DanmakuUpdate] = {
    danmaku.user.foreach { usr =>
      FMLCommonHandler
        .instance()
        .getMinecraftServerInstance
        .addScheduledTask(() => {
          usr.rotationYaw = danmaku.orientation.yaw.toFloat
          usr.rotationPitch = danmaku.orientation.pitch.toFloat
          usr.setPositionAndUpdate(danmaku.pos.x, danmaku.pos.y, danmaku.pos.z)
        })
    }
    super.impact(danmaku, rayTrace)
  }
}
