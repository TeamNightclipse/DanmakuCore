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
package net.katsstuff.teamnightclipse.danmakucore.javastuff

import javax.annotation.Nullable

import net.katsstuff.teamnightclipse.danmakucore.EnumDanmakuLevel
import net.katsstuff.teamnightclipse.danmakucore.helper.TDanmakuHelper
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.{DanmakuHelper => ScalaDanmakuHelper}
import net.minecraft.entity.{Entity, EntityLivingBase}

object DanmakuHelper extends TDanmakuHelper {

  /**
    * Adjust shot damage according to difficulty
    */
  def adjustDamageToDifficulty(base: Float, @Nullable user: EntityLivingBase, level: EnumDanmakuLevel): Float =
    ScalaDanmakuHelper.adjustDamageToDifficulty(base, Option(user), level)

  def adjustDamageCoreData(@Nullable user: EntityLivingBase, base: Float): Float =
    ScalaDanmakuHelper.adjustDamageCoreData(Option(user), base)

  def adjustDanmakuDamage(
      @Nullable user: EntityLivingBase,
      target: Entity,
      base: Float,
      level: EnumDanmakuLevel
  ): Float = ScalaDanmakuHelper.adjustDanmakuDamage(Option(user), target, base, level)
}
