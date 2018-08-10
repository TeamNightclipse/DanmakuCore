/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.entity.spellcard

import net.minecraft.entity.EntityLivingBase

object SpellcardDummy extends Spellcard {

  def instance: SpellcardDummy.type = this

  override def instantiate(card: EntitySpellcard, target: Option[EntityLivingBase]): SpellcardEntity =
    new SpellcardEntity(this, card, target) {
      override def onSpellcardUpdate(): Unit = {}
    }

  override def level = 0

  override def removeTime = 0

  override def endTime = 0

  override def touhouUser = throw new IllegalStateException("Tried to get user of dummy spellcard")
}
