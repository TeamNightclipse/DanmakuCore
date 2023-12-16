package net.katsstuff.danmakucore.danmaku.form
import net.katsstuff.danmakucore.client.form.{ClientForm, SphereClientForm}
import net.minecraft.Util
import net.minecraft.resources.ResourceLocation

class SphereForm extends Form {
  val getClientForm = Util.memoize((u: Unit) => new SphereClientForm)

  override def texture: ResourceLocation = ???

  override def clientForm: ClientForm = getClientForm(())
}
