package net.katsstuff.danmakucore.danmaku.form

import net.katsstuff.danmakucore.client.form.ClientForm
import net.minecraft.resources.ResourceLocation

abstract class Form {

  def texture: ResourceLocation

  def clientForm: ClientForm
}
