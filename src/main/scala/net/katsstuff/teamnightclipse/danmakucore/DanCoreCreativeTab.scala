/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
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