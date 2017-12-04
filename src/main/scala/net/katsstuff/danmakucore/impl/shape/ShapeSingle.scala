/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.shape

import net.katsstuff.danmakucore.data.{Quat, Vector3}
import net.katsstuff.danmakucore.entity.danmaku.{DanmakuTemplate, EntityDanmaku}
import net.katsstuff.danmakucore.shape.{Shape, ShapeResult}

class ShapeSingle(danmaku: DanmakuTemplate) extends Shape {
  override def draw(pos: Vector3, orientation: Quat, tick: Int): ShapeResult =
    if (!danmaku.world.isRemote) {
      val created = danmaku.asEntity
      danmaku.world.spawnEntity(created)
      ShapeResult.done(Set(created))
    } else ShapeResult.done(Set.empty[EntityDanmaku])
}
