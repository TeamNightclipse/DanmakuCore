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
package net.katsstuff.teamnightclipse.danmakucore.entity.spellcard

import java.util.{Optional, Random}

import net.katsstuff.teamnightclipse.danmakucore.EnumDanmakuLevel
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.DanCoreImplicits._
import net.katsstuff.teamnightclipse.mirror.data.Vector3
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World

/**
  * Java API for [[SpellcardEntity]].
  */
abstract class AbstractSpellcardEntity(
    spellcard: Spellcard,
    cardEntity: EntitySpellcard,
    target: Option[EntityLivingBase]
) extends SpellcardEntity(spellcard, cardEntity, target) {

  def getCard: EntitySpellcard = cardEntity

  def getUser: EntityLivingBase = user

  def getTarget: Optional[EntityLivingBase] = target.toOptional

  def getTime: Int = time

  def getRNG: Random = rng

  def getSpellcard: Spellcard = spellcard

  def getName: TextComponentTranslation = name

  def getWorld: World = world

  def getDanmakuLevel: EnumDanmakuLevel                     = danmakuLevel
  def setDanmakuLevel(danmakuLevel: EnumDanmakuLevel): Unit = this.danmakuLevel = danmakuLevel

  def posTargetJ: Optional[Vector3]             = posTarget.toOptional
  def directionUserToTargetJ: Optional[Vector3] = directionUserToTarget.toOptional
}
