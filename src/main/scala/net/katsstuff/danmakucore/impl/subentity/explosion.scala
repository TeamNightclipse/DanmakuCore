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

private[danmakucore] class SubEntityTypeExplosion(name: String, strength: Float) extends SubEntityType(name) {
  override def instantiate(world: World, entityDanmaku: EntityDanmaku) =
    new SubEntityExplosion(world, entityDanmaku, strength)
}

private class SubEntityExplosion(world: World, danmaku: EntityDanmaku, strength: Float)
    extends SubEntityTypeDefault.SubEntityDefault(world, danmaku) {

  override protected def impact(rayTrace: RayTraceResult): Unit = {
    super.impact(rayTrace)
    if (!world.isRemote) {
      val cause = danmaku.user.orElse(danmaku.source).orNull
      world.createExplosion(cause, rayTrace.hitVec.x, rayTrace.hitVec.y, rayTrace.hitVec.z, strength, false)
    }
  }
}
