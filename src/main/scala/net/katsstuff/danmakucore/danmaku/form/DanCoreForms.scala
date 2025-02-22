package net.katsstuff.danmakucore.danmaku.form

import net.katsstuff.danmakucore.DanmakuCore
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraftforge.registries.{DeferredRegister, IForgeRegistry}

import java.util.function.Supplier
import scala.compiletime.uninitialized

object DanCoreForms {
  val formsRegistryKey: ResourceKey[Registry[Form]] = ResourceKey.createRegistryKey(DanmakuCore.resource("forms"))
  private[danmakucore] var _registry: Supplier[IForgeRegistry[Form]] = uninitialized
  def registry: IForgeRegistry[Form] = _registry.get

  final val defRegistry: DeferredRegister[Form] = DeferredRegister.create(formsRegistryKey, DanmakuCore.ModId)

  final val SphereForm = defRegistry.register("sphere", () => new SphereForm)
}
