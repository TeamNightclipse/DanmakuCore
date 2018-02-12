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
import net.katsstuff.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.danmakucore.client.shader._
import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.data.Quat
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.lib.LibFormName
import net.minecraft.client.renderer.{GlStateManager, OpenGlHelper}
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

object FormBubble {
  //Yes, don't ask. This needs to be here because initialization order
  private val shaderLoc = DanmakuCore.resource("shaders/danmaku_bubble")
}
private[danmakucore] class FormBubble extends FormGeneric(LibFormName.BUBBLE) {

  @SideOnly(Side.CLIENT)
  override def initClient(): Unit = {
    if (OpenGlHelper.shadersSupported) {
      ShaderManager.initShader(
        FormBubble.shaderLoc,
        Seq(ShaderType.Vertex, ShaderType.Fragment),
        Seq(UniformBase("overwriteColor", UniformType.Vec3, 1), UniformBase("realColor", UniformType.Vec3, 1)),
        shader => {
          shader.begin()
          val ocr = (DanCoreRenderHelper.OverwriteColor >> 16 & 255) / 255F
          val ocg = (DanCoreRenderHelper.OverwriteColor >> 8 & 255) / 255F
          val ocb = (DanCoreRenderHelper.OverwriteColor & 255) / 255F

          shader.getUniform("overwriteColor").foreach { uniform =>
            uniform.set(ocr, ocg, ocb)
            uniform.upload()
          }
          shader.end()
        }
      )
    }
    super.initClient()
  }

  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = new IRenderForm {

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
      val shot  = danmaku.shot
      val color = shot.color
      val alpha = 0.3F

      DanCoreRenderHelper.transformDanmaku(shot, orientation)

      val dist = x * x + y * y + z * z

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      DanCoreRenderHelper.drawSphere(color, alpha, dist)
      GlStateManager.depthMask(true)
      GlStateManager.disableBlend()
    }

    override def renderShaders(
        danmaku: DanmakuState,
        x: Double,
        y: Double,
        z: Double,
        orientation: Quat,
        partialTicks: Float,
        manager: RenderManager,
        shaderProgram: DanCoreShaderProgram
    ): Unit = {
      val shot  = danmaku.shot
      val color = shot.color
      val dist  = x * x + y * y + z * z

      DanCoreRenderHelper.updateDanmakuShaderAttributes(shaderProgram, color)
      DanCoreRenderHelper.transformDanmaku(shot, orientation)

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      DanCoreRenderHelper.drawSphere(DanCoreRenderHelper.OverwriteColor, 1F, dist)
      GlStateManager.disableBlend()
    }

    override def shader(state: DanmakuState): ResourceLocation = FormBubble.shaderLoc
  }
}
