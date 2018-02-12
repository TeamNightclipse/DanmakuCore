/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.impl.form

import java.awt.Color

import org.lwjgl.opengl.GL11

import net.katsstuff.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.danmakucore.client.shader.DanCoreShaderProgram
import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.data.Quat
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.lib.LibFormName
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] class FormPellet extends FormGeneric(LibFormName.PELLET) {

  //noinspection ConvertExpressionToSAM
  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = new IRenderForm() {
    @SideOnly(Side.CLIENT)
    override def renderLegacy(danmaku: DanmakuState, x: Double, y: Double, z: Double, orientation: Quat, partialTicks: Float, manager: RenderManager): Unit = {
      val color = danmaku.shot.color
      val alpha = 0.3F

      val colorObj = new Color(color)
      val hsb      = new Array[Float](3)
      Color.RGBtoHSB(colorObj.getRed, colorObj.getGreen, colorObj.getBlue, hsb)
      hsb(1) = 0.15F
      hsb(2) = 1.0F
      val rimColor = Color.getHSBColor(hsb(0), hsb(1), hsb(2)).getRGB

      DanCoreRenderHelper.transformDanmaku(danmaku.shot, orientation)

      val dist = x * x + y * y + z * z
      DanCoreRenderHelper.drawSphere(rimColor, 1F, dist)
      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.075F, 1.075F, 1.075F)
      DanCoreRenderHelper.drawSphere(color, alpha, dist)
      GlStateManager.depthMask(true)
      GlStateManager.disableBlend()
    }

    override def renderShaders(danmaku: DanmakuState, x: Double, y: Double,
        z: Double, orientation: Quat, partialTicks: Float,
        manager: RenderManager,
        shaderProgram: DanCoreShaderProgram): Unit = {
      val color = danmaku.shot.color
      val alpha = 0.3F

      val colorObj = new Color(color)
      val hsb      = new Array[Float](3)
      Color.RGBtoHSB(colorObj.getRed, colorObj.getGreen, colorObj.getBlue, hsb)
      hsb(1) = 0.15F
      hsb(2) = 1.0F
      val rimColor = Color.getHSBColor(hsb(0), hsb(1), hsb(2)).getRGB

      DanCoreRenderHelper.updateDanmakuShaderAttributes(shaderProgram, color)
      DanCoreRenderHelper.transformDanmaku(danmaku.shot, orientation)

      val dist = x * x + y * y + z * z
      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      DanCoreRenderHelper.drawSphere(DanCoreRenderHelper.OverwriteColor, 1F, dist)
      GlStateManager.disableBlend()
    }

    override def shader(state: DanmakuState): ResourceLocation = DanCoreRenderHelper.fancyPelletDanmakuShaderLoc
  }
}
