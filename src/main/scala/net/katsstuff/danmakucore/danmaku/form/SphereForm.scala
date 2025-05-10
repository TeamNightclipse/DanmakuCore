package net.katsstuff.danmakucore.danmaku.form
import net.katsstuff.danmakucore.client.form.{ClientForm, SphereClientForm}
import net.minecraft.Util
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

import java.util.function

class SphereForm extends Form {
  val getClientForm: function.Function[Unit, SphereClientForm] = Util.memoize((_: Unit) => new SphereClientForm)

  override def texture: ResourceLocation = ???

  override def clientForm: ClientForm = getClientForm(())

  override def name: Component = Component.translatable("danmakucore.form.sphere.name")
}
