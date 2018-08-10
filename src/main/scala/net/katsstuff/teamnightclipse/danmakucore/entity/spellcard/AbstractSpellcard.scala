/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.entity.spellcard

import javax.annotation.Nullable

import net.minecraft.entity.EntityLivingBase

abstract class AbstractSpellcard extends Spellcard {

  def this(name: String) {
    this()
    setRegistryName(name)
  }

  def instantiate(card: EntitySpellcard, @Nullable target: EntityLivingBase): SpellcardEntity

  def instantiate(card: EntitySpellcard, target: Option[EntityLivingBase]): SpellcardEntity =
    instantiate(card, target.orNull)

}
