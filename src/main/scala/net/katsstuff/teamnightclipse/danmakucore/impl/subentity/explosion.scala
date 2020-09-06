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
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.util.math.AxisAlignedBB

private[danmakucore] class SubEntityTypeExplosion(name: String, strength: Float) extends SubEntityType(name) {
  override def instantiate: SubEntity =
    new SubEntityExplosion(strength)
}

private[subentity] class SubEntityExplosion(strength: Float) extends SubEntityDefault {

  override protected def impactBlock(danmaku: DanmakuState, aabb: AxisAlignedBB, block: IBlockState): DanmakuUpdate =
    super.impactBlock(danmaku, aabb, block).addCallbackIf(!danmaku.world.isRemote) {
      val cause        = danmaku.user.orElse(danmaku.source).orNull
      val realStrength = danmaku.shot.getSubEntityProperty("explosion_strength", strength)
      val center       = aabb.getCenter

      danmaku.world.createExplosion(cause, center.x, center.y, center.z, realStrength.toFloat, false)
    }

  override protected def impactEntity(danmaku: DanmakuState, entity: Entity): DanmakuUpdate =
    super.impactEntity(danmaku, entity).addCallbackIf(!danmaku.world.isRemote) {
      val cause        = danmaku.user.orElse(danmaku.source).orNull
      val realStrength = danmaku.shot.getSubEntityProperty("explosion_strength", strength)

      danmaku.world.createExplosion(cause, entity.posX, entity.posY, entity.posZ, realStrength.toFloat, false)
    }
}
