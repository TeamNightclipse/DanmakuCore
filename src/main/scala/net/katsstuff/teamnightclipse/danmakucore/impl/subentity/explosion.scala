/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.impl.subentity

import net.katsstuff.teamnightclipse.danmakucore.danmaku.{DanmakuState, DanmakuUpdate, DanmakuUpdateSignal}
import net.katsstuff.teamnightclipse.danmakucore.danmaku.subentity.{SubEntity, SubEntityType}
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.fml.common.FMLCommonHandler

private[danmakucore] class SubEntityTypeExplosion(name: String, strength: Float) extends SubEntityType(name) {
  override def instantiate: SubEntity =
    new SubEntityExplosion(strength)
}

private[subentity] class SubEntityExplosion(strength: Float) extends SubEntityDefault {

  override protected def impact(danmaku: DanmakuState, rayTrace: RayTraceResult): DanmakuUpdate =
    super.impact(danmaku, rayTrace).addCallbackIf(!danmaku.world.isRemote) {
      val cause = danmaku.user.orElse(danmaku.source).orNull
      danmaku.world.createExplosion(cause, rayTrace.hitVec.x, rayTrace.hitVec.y, rayTrace.hitVec.z, strength, false)
    }
}
