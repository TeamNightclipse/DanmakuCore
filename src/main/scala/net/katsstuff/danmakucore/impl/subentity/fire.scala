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

private[danmakucore] class SubEntityTypeFire(name: String, multiplier: Float) extends SubEntityType(name) {
  override def instantiate(world: World, entityDanmaku: EntityDanmaku) =
    new SubEntityFire(world, entityDanmaku, multiplier)
}

private[subentity] class SubEntityFire(world: World, danmaku: EntityDanmaku, multiplier: Float)
    extends SubEntityDefault(world, danmaku) {
  override def impactEntity(rayTrace: RayTraceResult): Unit = {
    super.impactEntity(rayTrace)
    rayTrace.entityHit.setFire((danmaku.getShotData.damage * multiplier).toInt)
  }
}
