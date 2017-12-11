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
import net.katsstuff.danmakucore.client.shader.{ShaderManager, UniformBase, UniformType}
import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.data.Quat
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.lib.LibFormName
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

//Name parameter for adding special effects to sphere
private[danmakucore] class FormSphere(name: String = LibFormName.DEFAULT) extends FormGeneric(name) {

  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = new IRenderForm {

    private val shaderLoc = DanmakuCore.resource("shaders/danmaku")
    ShaderManager
      .initShader(
        shaderLoc,
        Seq(
          UniformBase("realColor", UniformType.Vec3, 3),
          UniformBase("overwriteColor", UniformType.Vec3, 3)
        )
      )

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

      DanCoreRenderHelper.drawSphere(0xFFFFFF, 1F)

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.2F, 1.2F, 1.2F)
      DanCoreRenderHelper.drawSphere(color, alpha)
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
        manager: RenderManager
    ): Unit = {
      ShaderManager.getShader(shaderLoc).foreach { shader =>
        val shot = danmaku.shot
        val color          = shot.color
        val overwriteColor = 0xFF0000
        val alpha          = 0.3F
        val r              = (color >> 16 & 255) / 255F
        val g              = (color >> 8 & 255) / 255F
        val b              = (color & 255) / 255F

        val or = (overwriteColor >> 16 & 255) / 255F
        val og = (overwriteColor >> 8 & 255) / 255F
        val ob = (overwriteColor & 255) / 255F

        shader.begin()
        shader.getUniform("realColor").foreach { uniform =>
          uniform.set(r, g, b)
          uniform.upload()
        }
        shader.getUniform("overwriteColor").foreach { uniform =>
          uniform.set(or, og, ob)
          uniform.upload()
        }

        DanCoreRenderHelper.transformDanmaku(shot, orientation)

        DanCoreRenderHelper.drawSphere(0xFFFFFF, 1F)

        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
        GlStateManager.depthMask(false)
        GlStateManager.scale(1.2F, 1.2F, 1.2F)
        DanCoreRenderHelper.drawSphere(overwriteColor, alpha)
        GlStateManager.depthMask(true)
        GlStateManager.disableBlend()

        shader.end()
      }
    }
  }
}
