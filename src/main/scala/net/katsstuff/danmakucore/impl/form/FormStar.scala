/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form

import org.lwjgl.opengl.GL11

import net.katsstuff.danmakucore.client.helper.RenderHelper
import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.data.Quat
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.lib.LibFormName
import net.minecraft.client.renderer.{BufferBuilder, GlStateManager, Tessellator}
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

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
    override def renderForm(danmaku: DanmakuState, x: Double, y: Double, z: Double, orientation: Quat, partialTicks: Float, manager: RenderManager): Unit = {
      val tes   = Tessellator.getInstance
      val buf   = tes.getBuffer
      val color = danmaku.shot.color

      GlStateManager.rotate((danmaku.ticksExisted + partialTicks) * 5F, 1F, 1F, 1F)

      RenderHelper.transformDanmaku(danmaku.shot, orientation)

      var red   = 1F
      var green = 1F
      var blue  = 1F
      var alpha = 1F

      renderTetrahedron(tes, buf, red, green, blue, alpha)
      GlStateManager.rotate(90F, 1F, 0F, 0F)
      renderTetrahedron(tes, buf, red, green, blue, alpha)
      GlStateManager.rotate(-90F, 1F, 0F, 0F)

      red = (color >> 16 & 255) / 255.0F
      green = (color >> 8 & 255) / 255.0F
      blue = (color & 255) / 255.0F
      alpha = 0.3F

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.2F, 1.2F, 1.2F)

      renderTetrahedron(tes, buf, red, green, blue, alpha)
      GlStateManager.rotate(90F, 1F, 0F, 0F)
      renderTetrahedron(tes, buf, red, green, blue, alpha)

      GlStateManager.depthMask(true)
      GlStateManager.disableBlend()
    }

    @SideOnly(Side.CLIENT)
    private def renderTetrahedron(
        tes: Tessellator,
        buf: BufferBuilder,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ): Unit = {
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
