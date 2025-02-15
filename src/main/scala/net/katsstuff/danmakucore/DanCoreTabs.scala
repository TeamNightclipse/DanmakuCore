package net.katsstuff.danmakucore

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraftforge.registries.{DeferredRegister, RegistryObject}

object DanCoreTabs {
  val defRegistry: DeferredRegister[CreativeModeTab] =
    DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DanmakuCore.ModId)

  /*
  val danmaku: RegistryObject[CreativeModeTab] =
    registry.register(
      "danmaku_tab",
      () =>
        CreativeModeTab
          .builder()
          .withSearchBar()
          .hideTitle()
          .icon(() => ???)
          .displayItems { (params, b) =>
            // b.accept(???)
          }
          .build()
    )
  val spellcards: RegistryObject[CreativeModeTab] =
    registry.register(
      "spellcard_tab",
      () =>
        CreativeModeTab
          .builder()
          .withSearchBar()
          .hideTitle()
          .icon(() => ???)
          .displayItems { (params, b) =>
            // b.accept(???)
          }
          .build()
    )
    */
}
