/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.danmodel

import org.lwjgl.opengl.GL11

import net.katsstuff.teamnightclipse.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.IRenderForm
import net.katsstuff.teamnightclipse.danmakucore.impl.form.FormGeneric
import net.katsstuff.teamnightclipse.mirror.client.helper.MirrorRenderHelper
import net.katsstuff.teamnightclipse.mirror.data.Quat
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.{GLAllocation, GlStateManager, OpenGlHelper, Tessellator}
import net.minecraft.client.resources.{IResourceManager, IResourceManagerReloadListener}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] class FormDanModel(name: String, resource: ResourceLocation) extends FormGeneric(name) {

  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = {
    new IRenderForm with IResourceManagerReloadListener {
      private var danModel: DanModel = _
      private var modelList          = -1

      MirrorRenderHelper.registerResourceReloadListener(this)

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
          DanCoreRenderHelper.transformDanmaku(danmaku.shot, orientation)

          //Using VBOs here break for some models
          /*if (OpenGlHelper.useVbo()) {
            danModel.drawVBOs()
          } else*/
          if (modelList != -1) {
            GlStateManager.callList(modelList)
          } else {
            val tes = Tessellator.getInstance
            val vb  = tes.getBuffer

            modelList = GLAllocation.generateDisplayLists(1)

            GlStateManager.glNewList(modelList, GL11.GL_COMPILE_AND_EXECUTE)
            danModel.render(vb, DanCoreRenderHelper.OverwriteColorEdge)
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.glEndList()
          }
        }
      }

      override def onResourceManagerReload(resourceManager: IResourceManager): Unit = {
        if (danModel != null) {
          danModel.deleteVBOs()
        }

        if (modelList != -1) {
          GlStateManager.glDeleteLists(modelList, 1)
          modelList = -1
        }

        danModel = DanModelReader.readModel(resource).map(_._2).toOption.orNull

        if (OpenGlHelper.vboSupported && danModel != null) {
          danModel.generateVBOs()
        }
      }
    }
  }
}
