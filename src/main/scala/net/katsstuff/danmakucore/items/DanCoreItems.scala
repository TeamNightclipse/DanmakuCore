package net.katsstuff.danmakucore.items

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.blocks.DanCoreBlocks
import net.minecraft.client.color.item.ItemColor
import net.minecraft.world.item.{BlockItem, Item, ItemStack}
import net.minecraftforge.client.event.RegisterColorHandlersEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.{DeferredRegister, ForgeRegistries}

object DanCoreItems {
  final val defRegistry = DeferredRegister.create(ForgeRegistries.ITEMS, DanmakuCore.ModId)
  // final val DanmakuItem = registry.register("danmaku", () => new DanmakuItem(() => ???))

  final val DanmakuCraftingTableBlockItem = defRegistry.register(
    "danmaku_crafting_table",
    () => new BlockItem(DanCoreBlocks.DanmakuCraftingTableBlock.get(), new Item.Properties())
  )

  @SubscribeEvent
  def registerBlockColors(event: RegisterColorHandlersEvent.Item): Unit = {
    // TODO
    // event.getItemColors.register((stack, tintIndex) => {
    //  ???
    // }, ???)
  }
}
