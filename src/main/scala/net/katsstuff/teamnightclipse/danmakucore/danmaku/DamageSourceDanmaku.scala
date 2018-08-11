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
package net.katsstuff.teamnightclipse.danmakucore.danmaku

import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.item.ItemStack
import net.minecraft.util.text.translation.I18n
import net.minecraft.util.text.{ITextComponent, TextComponentString, TextComponentTranslation}
import net.minecraft.util.{DamageSource, EntityDamageSourceIndirect}

object DamageSourceDanmaku {
  def create(danmaku: DanmakuState) = new DamageSourceDanmaku(danmaku)
}

class DamageSourceDanmaku private (danmaku: DanmakuState) extends DamageSource("danmaku") {
  setProjectile()
  setDamageBypassesArmor()
  setDamageIsAbsolute()
  setMagicDamage()

  override def getTrueSource: Entity = danmaku.user.orElse(danmaku.source).orNull

  /**
    * Gets the death message that is displayed when the player dies
    */
  override def getDeathMessage(target: EntityLivingBase): ITextComponent = {
    val indirect       = getTrueSource
    val iTextComponent = if (indirect != null) indirect.getDisplayName else new TextComponentString("Danmaku") //TODO: Localize
    val stack = indirect match {
      case base: EntityLivingBase => base.getHeldItemMainhand
      case _                      => ItemStack.EMPTY
    }
    val s  = s"death.attack.$damageType"
    val s1 = s"$s.item"
    if (!stack.isEmpty && stack.hasDisplayName && I18n.canTranslate(s1))
      new TextComponentTranslation(s1, target.getDisplayName, iTextComponent, stack.getTextComponent)
    else new TextComponentTranslation(s, target.getDisplayName, iTextComponent)
  }
}

object DamageSourceDanmakuChainDeath {
  def create(immediateSource: Entity, trueSource: Entity) =
    new DamageSourceDanmakuChainDeath(immediateSource, trueSource)
}
class DamageSourceDanmakuChainDeath private (immediateSource: Entity, trueSource: Entity)
    extends EntityDamageSourceIndirect("chainExplosion", immediateSource, trueSource) {}
