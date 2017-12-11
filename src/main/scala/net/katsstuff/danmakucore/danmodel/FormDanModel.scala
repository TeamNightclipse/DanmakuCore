/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.danmodel

import net.katsstuff.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.data.Quat
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.impl.form.FormGeneric
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.resources.{IResourceManager, IResourceManagerReloadListener}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] class FormDanModel(name: String, resource: ResourceLocation)
    extends FormGeneric(name) {

  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = {
    new IRenderForm with IResourceManagerReloadListener {
      private var danModel: DanModel = _

      DanCoreRenderHelper.registerResourceReloadListener(this)

      override def renderLegacy(
          danmaku: DanmakuState,
          x: Double,
          y: Double,
          z: Double,
          orientation: Quat,
          partialTicks: Float,
          manager: RenderManager
      ): Unit = {
        if (danModel != null) {
          val tes = Tessellator.getInstance
          val vb  = tes.getBuffer

          DanCoreRenderHelper.transformDanmaku(danmaku.shot, orientation)
          danModel.render(vb, tes, danmaku.shot.getColor)
        }
      }

      override def onResourceManagerReload(resourceManager: IResourceManager): Unit = {
        danModel = DanModelReader.readModel(resource).map(_._2).toOption.orNull
      }
    }
  }
}
