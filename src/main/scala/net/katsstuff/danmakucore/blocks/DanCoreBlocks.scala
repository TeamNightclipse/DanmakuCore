package net.katsstuff.danmakucore.blocks

import net.katsstuff.danmakucore.DanmakuCore.ModId
import net.minecraftforge.registries.{DeferredRegister, ForgeRegistries}

object DanCoreBlocks {
  final val defRegistry = DeferredRegister.create(ForgeRegistries.BLOCKS, ModId)
  
  val DanmakuCraftingTableBlock = defRegistry.register("danmaku_crafting_table", () => new BlockDanmakuCraftingTable)
}
