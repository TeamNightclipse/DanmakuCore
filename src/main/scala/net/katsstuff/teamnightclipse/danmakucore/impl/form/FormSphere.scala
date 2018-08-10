/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.teamnightclipse.danmakucore.impl.form

import org.lwjgl.opengl.GL11

import net.katsstuff.teamnightclipse.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.{IRenderForm, RenderingProperty}
import net.katsstuff.teamnightclipse.danmakucore.lib.LibFormName
import net.katsstuff.teamnightclipse.mirror.client.helper.MirrorRenderHelper
import net.katsstuff.teamnightclipse.mirror.client.shaders.MirrorShaderProgram
import net.katsstuff.teamnightclipse.mirror.data.Quat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

//Name parameter for adding special effects to sphere
private[danmakucore] class FormSphere(name: String = LibFormName.DEFAULT) extends FormGeneric(name) {

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
      val alpha = 0.3F

      DanCoreRenderHelper.transformDanmaku(shot, orientation)

      val dist = x * x + y * y + z * z
      MirrorRenderHelper.drawSphere(shot.coreColor, 1F, dist)

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.2F, 1.2F, 1.2F)
      MirrorRenderHelper.drawSphere(shot.edgeColor, alpha, dist)
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
        shaderProgram: MirrorShaderProgram
    ): Unit = {
      val shot = danmaku.shot
      val dist = x * x + y * y + z * z

      DanCoreRenderHelper.updateDanmakuShaderAttributes(shaderProgram, this, shot)
      DanCoreRenderHelper.transformDanmaku(shot, orientation)

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      MirrorRenderHelper.drawSphere(DanCoreRenderHelper.OverwriteColorEdge, 1F, dist)
      GlStateManager.disableBlend()
    }

    override def shader(state: DanmakuState): ResourceLocation = DanCoreRenderHelper.fancyDanmakuShaderLoc
    override val defaultAttributeValues: Map[String, RenderingProperty] = Map(
      "coreSize"     -> RenderingProperty(1.1F, 0.5F, 10F),
      "coreHardness" -> RenderingProperty(2.5F, 0.5F, 10F),
      "edgeHardness" -> RenderingProperty(3F, 0.5F, 10F),
      "edgeGlow"     -> RenderingProperty(3F, 0.5F, 10F)
    )
  }
}
