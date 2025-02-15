package net.katsstuff.danmakucore.danmaku

import net.katsstuff.danmakucore.DanmakuCore
import net.minecraft.core.{Registry, RegistryAccess}
import net.minecraft.resources.ResourceKey

object DanmakuInstantiations {

  val registryKey: ResourceKey[Registry[DanmakuInstantiation]] =
    ResourceKey.createRegistryKey(DanmakuCore.resource("danmaku_instantiations"))

  def registry(access: RegistryAccess): Registry[DanmakuInstantiation] = access.registryOrThrow(registryKey)
}
