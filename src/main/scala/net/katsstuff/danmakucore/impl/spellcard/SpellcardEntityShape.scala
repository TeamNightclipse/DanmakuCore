/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.spellcard

import net.katsstuff.danmakucore.entity.spellcard.{EntitySpellcard, Spellcard, SpellcardEntity}
import net.katsstuff.danmakucore.shape.{Shape, ShapeHandler}
import net.minecraft.entity.EntityLivingBase

class SpellcardEntityShape(spellcard: Spellcard, card: EntitySpellcard, target: Option[EntityLivingBase], shape: Shape)
    extends SpellcardEntity(spellcard, card, target) {

  private var sent = false

  override def onSpellcardUpdate(): Unit = if (!sent) {
    ShapeHandler.createShape(shape, card)
    sent = true
  }
}
