/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.danmaku

import net.katsstuff.danmakucore.handler.DanmakuState
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.item.ItemStack
import net.minecraft.util.{DamageSource, EntityDamageSourceIndirect}
import net.minecraft.util.text.translation.I18n
import net.minecraft.util.text.{ITextComponent, TextComponentString, TextComponentTranslation}

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
    val s  = "death.attack." + damageType
    val s1 = s + ".item"
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
