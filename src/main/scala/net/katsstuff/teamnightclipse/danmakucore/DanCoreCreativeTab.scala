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

import net.katsstuff.teamnightclipse.danmakucore.lib.LibMod
import net.minecraft.creativetab.CreativeTabs

abstract class DanCoreCreativeTab private[danmakucore] (val label: String)
    extends CreativeTabs(s"${LibMod.Id}.$label") {
  setNoTitle()
  setBackgroundImageName("item_search.png")
  override def hasSearchBar = true
}
