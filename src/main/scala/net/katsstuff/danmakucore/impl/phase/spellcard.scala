/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.phase

import net.katsstuff.danmakucore.entity.living.phase.{Phase, PhaseManager, PhaseType}
import net.katsstuff.danmakucore.entity.spellcard.Spellcard
import net.katsstuff.danmakucore.item.ItemSpellcard
import net.katsstuff.danmakucore.registry.DanmakuRegistry
import net.katsstuff.danmakucore.scalastuff.TouhouHelper
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
  override def isSpellcard = true
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
