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

private[danmakucore] class FormSphereDark extends FormGeneric(LibFormName.SPHERE_DARK) {

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
      val dist  = x * x + y * y + z * z

      DanCoreRenderHelper.transformDanmaku(shot, orientation)

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.2F, 1.2F, 1.2F)
      DanCoreRenderHelper.drawSphere(color, alpha, dist)
      GlStateManager.depthMask(true)
      GlStateManager.disableBlend()
      GlStateManager.scale(1F / 1.2F, 1F / 1.2F, 1F / 1.2F)
      DanCoreRenderHelper.drawSphere(0x000000, 1F, dist)
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

    override def shader(state: DanmakuState): ResourceLocation = DanCoreRenderHelper.fancyDarkDanmakuShaderLoc
  }
}
