package net.katsstuff.danmakucore.danmaku

import net.katsstuff.danmakucore.DanmakuCore
import net.minecraft.core.{Registry, RegistryAccess}
import net.minecraft.resources.ResourceKey

object DanmakuSystems {

  val registryKey: ResourceKey[Registry[DanmakuSystem]] =
    ResourceKey.createRegistryKey(DanmakuCore.resource("danmaku_systems"))

  def registry(access: RegistryAccess): Registry[DanmakuSystem] = access.registryOrThrow(registryKey)

}
