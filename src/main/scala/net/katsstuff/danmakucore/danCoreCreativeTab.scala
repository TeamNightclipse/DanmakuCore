package net.katsstuff.danmakucore

import net.katsstuff.danmakucore.items.DanCoreItems
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.{CreativeModeTab, ItemStack}

object DanmakuCreativeTab extends CreativeModeTab("danmaku") {
  hideTitle()
  setBackgroundImage(new ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png"))

  override def hasSearchBar: Boolean = true

  override def makeIcon(): ItemStack = new ItemStack(DanCoreItems.DanmakuItem.get())
}
object SpellcardCreativeTab extends CreativeModeTab("danmaku") {
  hideTitle()
  setBackgroundImage(new ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png"))

  override def hasSearchBar: Boolean = true

  override def makeIcon(): ItemStack = ???
}
