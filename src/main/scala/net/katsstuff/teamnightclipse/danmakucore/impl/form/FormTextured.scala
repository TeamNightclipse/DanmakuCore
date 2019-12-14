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
package net.katsstuff.teamnightclipse.danmakucore.impl.form

import net.katsstuff.teamnightclipse.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.IRenderForm
import net.katsstuff.teamnightclipse.mirror.client.shaders.MirrorShaderProgram
import net.katsstuff.teamnightclipse.mirror.data.Quat
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

abstract class FormTextured(name: String) extends FormGeneric(name) {

  def quadWidth(danmaku: DanmakuState): Double
  def quadLength(danmaku: DanmakuState): Double

  //noinspection ConvertExpressionToSAM
  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = new IRenderForm() {
    @SideOnly(Side.CLIENT)
    override def renderLegacy(
        danmaku: DanmakuState,
        x: Double,
        y: Double,
        z: Double,
        orientation: Quat,
        partialTicks: Float,
        manager: RenderManager
    ): Unit = {
      val tes   = Tessellator.getInstance
      val bb    = tes.getBuffer
      val color = danmaku.shot.edgeColor

      val red   = (color >> 16 & 255) / 255.0F
      val green = (color >> 8 & 255) / 255.0F
      val blue  = (color & 255) / 255.0F
      val alpha = 1.0F

      val u1 = 0F
      val u2 = 1F
      val v1 = 0F
      val v2 = 1F

      val width  = quadWidth(danmaku)
      val length = quadLength(danmaku)

      DanCoreRenderHelper.transformDanmaku(danmaku.shot, orientation)

      GlStateManager.disableCull()
      bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
      bb.pos(width, 0.0F, length).tex(u2, v1).endVertex()
      bb.pos(-width, 0.0F, length).tex(u1, v1).endVertex()
      bb.pos(-width, 0.0F, -length).tex(u1, v2).endVertex()
      bb.pos(width, 0.0F, -length).tex(u2, v2).endVertex()
      tes.draw()

      //What we really want here is to use the luminance as the saturation, and set the luminance to 1 for the texture
      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      for (_ <- 0 until 4) {
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        bb.pos(width, 0D, length).tex(u2, v1).color(red, green, blue, alpha).endVertex()
        bb.pos(-width, 0D, length).tex(u1, v1).color(red, green, blue, alpha).endVertex()
        bb.pos(-width, 0D, -length).tex(u1, v2).color(red, green, blue, alpha).endVertex()
        bb.pos(width, 0D, -length).tex(u2, v2).color(red, green, blue, alpha).endVertex()
        tes.draw()
      }

      GlStateManager.disableBlend()
      GlStateManager.enableCull()
    }

    override def renderShaders(
        danmaku: DanmakuState,
        x: Double,
        y: Double,
        z: Double,
        orientation: Quat,
        partialTicks: Float,
        manager: RenderManager,
        shaderProgram: MirrorShaderProgram
    ): Unit = {
      val tes  = Tessellator.getInstance
      val bb   = tes.getBuffer
      val shot = danmaku.shot

      val u1 = 0F
      val u2 = 1F
      val v1 = 0F
      val v2 = 1F

      val width  = quadWidth(danmaku)
      val length = quadLength(danmaku)

      DanCoreRenderHelper.updateDanmakuShaderAttributes(shaderProgram, this, shot)
      DanCoreRenderHelper.transformDanmaku(shot, orientation)

      GlStateManager.disableCull()
      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
      bb.pos(width, 0D, length).tex(u2, v1).endVertex()
      bb.pos(-width, 0D, length).tex(u1, v1).endVertex()
      bb.pos(-width, 0D, -length).tex(u1, v2).endVertex()
      bb.pos(width, 0D, -length).tex(u2, v2).endVertex()
      tes.draw()
      GlStateManager.disableBlend()
      GlStateManager.enableCull()
    }

    override def shader(state: DanmakuState): ResourceLocation = DanCoreRenderHelper.texturedDanmakuShaderLoc
  }
}
