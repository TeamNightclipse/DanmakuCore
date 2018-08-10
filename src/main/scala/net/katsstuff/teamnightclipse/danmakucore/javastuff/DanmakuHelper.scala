/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
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
