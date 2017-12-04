/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku

import javax.annotation.Nullable

import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.item.ItemStack
import net.minecraft.util.EntityDamageSourceIndirect
import net.minecraft.util.text.translation.I18n
import net.minecraft.util.text.{ITextComponent, TextComponentTranslation}

object DamageSourceDanmaku {
  def causeDanmakuDamage(entity: Entity, @Nullable indirectEntityIn: Entity) =
    new DamageSourceDanmaku(entity, indirectEntityIn)

  def causeDanmakuDamage(entity: Entity, indirectEntityIn: Option[Entity]) =
    new DamageSourceDanmaku(entity, indirectEntityIn)
}

class DamageSourceDanmaku private (val entity: Entity, @Nullable val indirectEntityIn: Entity)
    extends EntityDamageSourceIndirect("danmaku", entity, indirectEntityIn) {
  def this(entity: Entity, indirectEntityIn: Option[Entity]) = this(entity, indirectEntityIn.orNull)

  setProjectile()
  setDamageBypassesArmor()
  setDamageIsAbsolute()
  setMagicDamage()

  /**
    * Gets the death message that is displayed when the player dies
    */
  override def getDeathMessage(target: EntityLivingBase): ITextComponent = {
    val indirect       = getImmediateSource
    val iTextComponent = if (indirect == null) getTrueSource.getDisplayName else indirect.getDisplayName
    val stack = indirect match {
      case base: EntityLivingBase => base.getHeldItemMainhand
      case _                      => ItemStack.EMPTY
    }
    val s  = "death.attack." + damageType
    val s1 = s + ".item"
    if (!stack.isEmpty && stack.hasDisplayName && I18n.canTranslate(s1))
      new TextComponentTranslation(s1, target.getDisplayName, iTextComponent, stack.getTextComponent)
    else new TextComponentTranslation(s, target.getDisplayName, iTextComponent)
  }
}
