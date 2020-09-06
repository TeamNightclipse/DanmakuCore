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
import net.minecraft.entity.Entity

private[danmakucore] class SubEntityTypeFire(name: String, multiplier: Float) extends SubEntityType(name) {
  override def instantiate: SubEntity =
    new SubEntityFire(multiplier)
}

private[subentity] class SubEntityFire(multiplier: Float) extends SubEntityDefault {
  override protected def impactEntity(danmaku: DanmakuState, entity: Entity): DanmakuUpdate =
    super.impactEntity(danmaku, entity).addCallbackIf(!danmaku.world.isRemote) {
      val realMultiplier = danmaku.shot.getSubEntityProperty("fire_multiplier", multiplier)

      entity.setFire((danmaku.shot.damage * realMultiplier).toInt)
    }
}
