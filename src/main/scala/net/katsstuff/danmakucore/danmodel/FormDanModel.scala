/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmodel

import net.katsstuff.danmakucore.client.helper.RenderHelper
import net.katsstuff.danmakucore.data.Quat
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.handler.DanmakuState
import net.katsstuff.danmakucore.impl.form.FormGeneric
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] class FormDanModel(name: String, model: DanModel) extends FormGeneric(name) {
  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = {
    (
        danmaku: DanmakuState,
        _: Double,
        _: Double,
        _: Double,
        orientation: Quat,
        _: Float,
        _: RenderManager
    ) =>
      val tes = Tessellator.getInstance
      val vb  = tes.getBuffer

      RenderHelper.transformDanmaku(danmaku.shot, orientation)
      model.render(vb, tes, danmaku.shot.getColor)
  }
}
