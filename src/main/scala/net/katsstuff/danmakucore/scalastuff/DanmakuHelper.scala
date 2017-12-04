/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.scalastuff

import net.katsstuff.danmakucore.EnumDanmakuLevel
import net.katsstuff.danmakucore.handler.ConfigHandler
import net.katsstuff.danmakucore.helper.TDanmakuHelper
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
