/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore

import net.katsstuff.teamnightclipse.danmakucore.lib.data.LibItems
import net.minecraft.item.ItemStack

object DanmakuCreativeTab extends DanCoreCreativeTab("danmaku") {
  override def createIcon: ItemStack = new ItemStack(LibItems.DANMAKU)
}
object SpellcardsCreativeTab extends DanCoreCreativeTab("spellcard") {
  override def createIcon: ItemStack = new ItemStack(LibItems.SPELLCARD)
}

//JAVA API
object DanCoreCreativeTabs {
  def danmaku: DanmakuCreativeTab.type       = DanmakuCreativeTab
  def spellcards: SpellcardsCreativeTab.type = SpellcardsCreativeTab
}