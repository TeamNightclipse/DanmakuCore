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
import net.katsstuff.teamnightclipse.danmakucore.lib.LibFormName
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{BufferBuilder, GlStateManager, Tessellator}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

import net.katsstuff.teamnightclipse.mirror.data.Quat

private[danmakucore] class FormStar extends FormGeneric(LibFormName.STAR) {

  // format: OFF
  private val points = Array(
    Array(1F, 1F, 1F), 
    Array(-1F, -1F, 1F), 
    Array(-1F, 1F, -1F), 
    Array(1F, -1F, -1)
  )
  // format: ON

  private val tetraIndicies = Array(0, 1, 2, 3, 0, 1)

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
      val tes  = Tessellator.getInstance
      val buf  = tes.getBuffer
      val shot = danmaku.shot

      GlStateManager.rotate((danmaku.ticksExisted + partialTicks) * 5F, 1F, 1F, 1F)

      DanCoreRenderHelper.transformDanmaku(shot, orientation)

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.2F, 1.2F, 1.2F)

      renderTetrahedron(tes, buf, shot.edgeColor, 0.3F)
      GlStateManager.rotate(90F, 1F, 0F, 0F)
      renderTetrahedron(tes, buf, shot.edgeColor, 0.3F)

      GlStateManager.depthMask(true)
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      GlStateManager.disableBlend()
      GlStateManager.scale(1F / 1.2F, 1F / 1.2F, 1F / 1.2F)

      renderTetrahedron(tes, buf, shot.coreColor, 1F)
      GlStateManager.rotate(90F, 1F, 0F, 0F)
      renderTetrahedron(tes, buf, shot.coreColor, 1F)
      GlStateManager.rotate(-90F, 1F, 0F, 0F)
    }

    @SideOnly(Side.CLIENT)
    private def renderTetrahedron(
        tes: Tessellator,
        buf: BufferBuilder,
        color: Int,
        a: Float
    ): Unit = {
      val r = (color >> 16 & 255) / 255.0F
      val g = (color >> 8 & 255) / 255.0F
      val b = (color & 255) / 255.0F

      buf.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR)
      for (i <- 0 until 6) {
        buf
          .pos(points(tetraIndicies(i))(0), points(tetraIndicies(i))(1), points(tetraIndicies(i))(2))
          .color(r, g, b, a)
          .endVertex()
      }
      tes.draw()
    }
  }
}
