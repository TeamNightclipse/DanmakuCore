package net.katsstuff.danmakucore.items

import net.katsstuff.danmakucore.DanmakuCreativeTab
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.item.{Item, ItemStack}

class SpellcardItem(todo: Nothing) extends Item(new Item.Properties().tab(DanmakuCreativeTab).stacksTo(1)) {
  override def getDescriptionId(p_41455_ : ItemStack): String =
    s"${getDescriptionId()}.${todo}"

  override def onItemUseFirst(stack: ItemStack, context: UseOnContext): InteractionResult = super.onItemUseFirst(stack, context)

  override def getHighlightTip(item: ItemStack, displayName: Component): Component = super.getHighlightTip(item, displayName)
}
