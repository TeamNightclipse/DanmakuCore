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

import net.katsstuff.danmakucore.DanmakuCore
import net.katsstuff.danmakucore.client.helper.RenderHelper
import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.data.Quat
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.lib.LibFormName
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] class FormKunai extends FormGeneric(LibFormName.KUNAI) {

  private val texture = DanmakuCore.resource("textures/entity/danmaku/kunai.png")
  override def getTexture(danmaku: DanmakuState): ResourceLocation = texture

  //noinspection ConvertExpressionToSAM
  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = new IRenderForm() {
    @SideOnly(Side.CLIENT)
    override def renderForm(danmaku: DanmakuState, x: Double, y: Double, z: Double, orientation: Quat, partialTicks: Float, manager: RenderManager): Unit = {
      val tes      = Tessellator.getInstance
      val bb       = tes.getBuffer
      val color    = danmaku.shot.color

      val red      = (color >> 16 & 255) / 255.0F
      val green    = (color >> 8 & 255) / 255.0F
      val blue     = (color & 255) / 255.0F
      val alpha    = 1.0F

      val u1       = 0F
      val u2       = 1F
      val v1       = 0F
      val v2       = 1F

      val width    = 1.0D
      val length   = 2.0D

      RenderHelper.transformDanmaku(danmaku.shot, orientation)

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
      for(_ <- 0 until 4) {
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
  }
}
