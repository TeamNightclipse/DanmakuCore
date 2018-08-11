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
package net.katsstuff.teamnightclipse.danmakucore.scalastuff

import net.katsstuff.teamnightclipse.danmakucore.EnumDanmakuLevel
import net.katsstuff.teamnightclipse.danmakucore.handler.ConfigHandler
import net.katsstuff.teamnightclipse.danmakucore.helper.TDanmakuHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}

object DanmakuHelper extends TDanmakuHelper {

  /**
    * Adjust shot damage according to difficulty
    */
  def adjustDamageToDifficulty(base: Float, user: Option[EntityLivingBase], level: EnumDanmakuLevel): Float = {
    if (user.exists(_.isInstanceOf[EntityPlayer])) base
    else if (ConfigHandler.danmaku.oneHitKill) 999999F
    else {
      level match {
        case EnumDanmakuLevel.PEACEFUL =>
          base
        case EnumDanmakuLevel.EASY =>
          base * 0.7F
        case EnumDanmakuLevel.NORMAL =>
          base
        case EnumDanmakuLevel.HARD =>
          base * 1.3F
        case EnumDanmakuLevel.LUNATIC =>
          base * 1.5F
        case EnumDanmakuLevel.EXTRA =>
          base * 2F
        case EnumDanmakuLevel.LAST_SPELL =>
          base * 2.5F
        case EnumDanmakuLevel.LAST_WORD =>
          base * 3F
      }
    }
  }

  def adjustDamageCoreData(user: Option[EntityLivingBase], base: Float): Float = {
    user
      .flatMap { usr =>
        TouhouHelper.getDanmakuCoreData(usr).map { data =>
          val full  = 4F
          val power = data.getPower
          val bonus = power / full
          base + ((base / 2) * bonus)
        }
      }
      .getOrElse(base)
  }

  def adjustDanmakuDamage(
      user: Option[EntityLivingBase],
      target: Entity,
      base: Float,
      level: EnumDanmakuLevel
  ): Float = {
    val withData      = adjustDamageCoreData(user, base)
    val againstTarget = adjustDamageTarget(withData, target)
    adjustDamageToDifficulty(againstTarget, user, level)
  }
}
