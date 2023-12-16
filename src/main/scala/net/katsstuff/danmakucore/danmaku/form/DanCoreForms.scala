package net.katsstuff.danmakucore.danmaku.form

import net.katsstuff.danmakucore.DanmakuCore
import net.minecraftforge.registries.{DeferredRegister, RegistryBuilder}

object DanCoreForms {
  final val DanCoreForms = DeferredRegister.create[Form](DanmakuCore.resource("forms"), DanmakuCore.ModId)
  final val Forms = DanCoreForms.makeRegistry(() => new RegistryBuilder[Form].setDefaultKey(DanmakuCore.resource("sphere")))

  final val SphereForm = DanCoreForms.register("sphere", () => new SphereForm)
}
