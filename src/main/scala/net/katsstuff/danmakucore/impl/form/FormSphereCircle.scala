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
import net.katsstuff.danmakucore.danmaku.DanmakuState
import net.katsstuff.danmakucore.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.lib.LibFormName
import net.katsstuff.mirror.client.helper.MirrorRenderHelper
import net.katsstuff.mirror.data.Quat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] class FormSphereCircle extends FormGeneric(LibFormName.CIRCLE) {

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
      val shot = danmaku.shot
      val alpha = 0.3F
      val dist  = x * x + y * y + z * z

      DanCoreRenderHelper.transformDanmaku(shot, orientation)

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.2F, 1.2F, 1.2F)

      MirrorRenderHelper.drawSphere(shot.edgeColor, alpha, dist)

      GlStateManager.scale(1.3F, 1.3F, 1.3F)

      MirrorRenderHelper.drawSphere(shot.edgeColor, alpha * 0.4F, dist)

      GlStateManager.depthMask(true)
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      GlStateManager.disableBlend()
      GlStateManager.scale(1F / (1.2F * 1.3F), 1F / (1.2F * 1.3F), 1F / (1.2F * 1.3F))

      MirrorRenderHelper.drawSphere(shot.coreColor, 1F, dist)
    }
  }
}
