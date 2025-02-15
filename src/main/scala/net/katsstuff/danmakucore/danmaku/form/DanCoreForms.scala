package net.katsstuff.danmakucore.danmaku.form

import java.util.function.Supplier
import net.katsstuff.danmakucore.DanmakuCore
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraftforge.registries.{DeferredRegister, ForgeRegistry, IForgeRegistry, RegistryBuilder, RegistryManager}

object DanCoreForms {
  val formsRegistryKey: ResourceKey[Registry[Form]] = ResourceKey.createRegistryKey(DanmakuCore.resource("forms"))
  lazy val registry: IForgeRegistry[Form] = RegistryManager.ACTIVE.getRegistry(formsRegistryKey)

  final val defRegistry: DeferredRegister[Form] = DeferredRegister.create(formsRegistryKey, DanmakuCore.ModId)

  final val SphereForm = defRegistry.register("sphere", () => new SphereForm)
}
