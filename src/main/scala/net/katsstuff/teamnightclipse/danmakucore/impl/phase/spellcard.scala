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
package net.katsstuff.teamnightclipse.danmakucore.impl.phase

import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.{Phase, PhaseType}
import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.Spellcard
import net.katsstuff.teamnightclipse.danmakucore.item.ItemSpellcard
import net.katsstuff.teamnightclipse.danmakucore.registry.DanmakuRegistry
import net.katsstuff.teamnightclipse.danmakucore.scalastuff.TouhouHelper
import net.katsstuff.teamnightclipse.danmakucore.entity.living.phase.{Phase, PhaseManager, PhaseType}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{DamageSource, ResourceLocation}

class PhaseTypeSpellcard extends PhaseType {
  override def instantiate(manager: PhaseManager) =
    new PhaseSpellcard(manager, this, DanmakuRegistry.getRandomObject(classOf[Spellcard], manager.entity.getRNG))
  def instantiate(manager: PhaseManager, spellcard: Spellcard) =
    new PhaseSpellcard(manager, this, spellcard)
}

class PhaseSpellcard(manager: PhaseManager, val phaseType: PhaseTypeSpellcard, var _spellcard: Spellcard)
    extends Phase(manager) {

  val NbtSpellcard   = "spellcard"
  val NbtFirstAttack = "firstAttack"

  private var firstAttack = false
  override def init(): Unit = {
    if (_spellcard == null) {
      getEntity.setDead()
      return
    }

    super.init()
    interval = _spellcard.endTime
    firstAttack = true
    entity.hurtResistantTime = 40
  }

  override def serverUpdate(): Unit = {
    super.serverUpdate()
    if (_spellcard == null) {
      getEntity.setDead()
      return
    }

    val target = entity.getAttackTarget

    if (!isFrozen && (isCounterStart || firstAttack) && target != null && entity.getEntitySenses.canSee(target)) {
      TouhouHelper.declareSpellcard(entity, Some(target), _spellcard, firstAttack, addSpellcardName = false).foreach {
        declaredSpellcard =>
          firstAttack = false
          declaredSpellcard.spellCardEntity.danmakuLevel = level
      }
    }
  }
  override def isSpellcard                  = true
  override def spellcard: Option[Spellcard] = Some(_spellcard)

  override def serializeNBT: NBTTagCompound = {
    val compound = super.serializeNBT
    compound.setString(NbtSpellcard, _spellcard.fullNameString)
    compound.setBoolean(NbtFirstAttack, firstAttack)
    compound
  }

  override def deserializeNBT(nbt: NBTTagCompound): Unit = {
    super.deserializeNBT(nbt)
    _spellcard = DanmakuRegistry.Spellcard.getValue(new ResourceLocation(nbt.getString(NbtSpellcard)))
    if (_spellcard == null) _spellcard = DanmakuRegistry.getRandomObject(classOf[Spellcard], getEntity.getRNG)
    firstAttack = nbt.getBoolean(NbtFirstAttack)
  }

  override def dropLoot(source: DamageSource): Unit =
    entity.entityDropItem(ItemSpellcard.createStack(_spellcard), 0F)
}
