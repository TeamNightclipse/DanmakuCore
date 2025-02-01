package net.katsstuff.danmakucore.danmaku.form

import java.util.function.Supplier

import net.katsstuff.danmakucore.DanmakuCore
import net.minecraftforge.registries.{DeferredRegister, IForgeRegistry, RegistryBuilder}

object DanCoreForms {
  final val registry: DeferredRegister[Form] =
    DeferredRegister.create[Form](DanmakuCore.resource("forms"), DanmakuCore.ModId)
  final val Forms: Supplier[IForgeRegistry[Form]] =
    registry.makeRegistry(() => new RegistryBuilder[Form].setDefaultKey(DanmakuCore.resource("sphere")))
    
  final val SphereForm = registry.register("sphere", () => new SphereForm)
}
