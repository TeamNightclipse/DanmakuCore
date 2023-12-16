package net.katsstuff.danmakucore.client.form

import net.minecraft.util.Mth

case class RenderingProperty(default: Float, max: Float, min: Float) {
  def asValue(opt: Option[Float]): Float = opt.fold(default)(Mth.clamp(_, min, max))
}
