package net.katsstuff.danmakucore.items

import net.katsstuff.danmakucore.DanmakuCore
import net.minecraft.client.color.item.ItemColor
import net.minecraft.world.item.ItemStack
import net.minecraftforge.client.event.ColorHandlerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.{DeferredRegister, ForgeRegistries}

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = DanmakuCore.ModId)
object DanCoreItems {
  final val DanCoreItems = DeferredRegister.create(ForgeRegistries.ITEMS, DanmakuCore.ModId)
  final val DanmakuItem = DanCoreItems.register("danmaku", () => new DanmakuItem(() => ???))


  @SubscribeEvent
  def registerBlockColors(event: ColorHandlerEvent.Item): Unit = {
    //TODO
    //event.getItemColors.register(new ItemColor {
    //  override def getColor(p_92672_ : ItemStack, p_92673_ : Int): Int = ???
    //}, ???)
  }
}
