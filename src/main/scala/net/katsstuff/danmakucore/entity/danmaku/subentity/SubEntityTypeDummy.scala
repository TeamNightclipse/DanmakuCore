/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku.subentity

import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.minecraft.world.World

object SubEntityTypeDummy extends SubEntityType {
  def instance: SubEntityTypeDummy.type = this

  override def instantiate(world: World, entityDanmaku: EntityDanmaku) = new SubEntityDummy(world, entityDanmaku)
}

private[subentity] class SubEntityDummy(world: World, danmaku: EntityDanmaku) extends SubEntity(world, danmaku) {
  override def subEntityTick(): Unit = {}
}
