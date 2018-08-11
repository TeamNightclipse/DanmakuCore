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
package net.katsstuff.teamnightclipse.danmakucore.capability.danmakuhit

import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.minecraft.entity.Entity
import net.minecraft.util.DamageSource

/**
  * A capability that lets a entity overwrite what happens when it's hit by a danmaku.
  */
trait DanmakuHitBehavior {

  def onHit(danmaku: DanmakuState, hitEntity: Entity, damage: Float, source: DamageSource): Unit
}