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
