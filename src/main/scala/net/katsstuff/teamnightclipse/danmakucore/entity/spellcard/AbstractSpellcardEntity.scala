/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
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
