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
