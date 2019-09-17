/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.katsstuff.teamnightclipse.danmakucore.danmodel

import java.util.function.Predicate

import net.katsstuff.teamnightclipse.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.IRenderForm
import net.katsstuff.teamnightclipse.danmakucore.impl.form.FormGeneric
import net.katsstuff.teamnightclipse.mirror.client.helper.MirrorRenderHelper
import net.katsstuff.teamnightclipse.mirror.data.Quat
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.{GLAllocation, GlStateManager, OpenGlHelper, Tessellator}
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.resource.{IResourceType, ISelectiveResourceReloadListener, VanillaResourceType}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

private[danmakucore] class FormDanModel(name: String, resource: ResourceLocation) extends FormGeneric(name) {

  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = {
    new IRenderForm with ISelectiveResourceReloadListener {
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

      override def onResourceManagerReload(resourceManager: IResourceManager, resourcePredicate: Predicate[IResourceType]): Unit = {
        if(resourcePredicate.test(VanillaResourceType.MODELS)) {
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
}
