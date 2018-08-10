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
import net.katsstuff.teamnightclipse.mirror.client.helper.MirrorRenderHelper
import net.katsstuff.teamnightclipse.mirror.data.Quat
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] class FormScale extends FormGeneric(LibFormName.SCALE) {

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
      val length = 2F
      val alpha  = 0.35F

      DanCoreRenderHelper.transformDanmaku(danmaku.shot, orientation)

      val dist = x * x + y * y + z * z
      GL11.glScalef(0.5F, 0.5F, length * 0.4F)
      MirrorRenderHelper.drawSphere(danmaku.shot.coreColor, 1F, dist)

      GL11.glTranslatef(0F, 0F, -0.7F)
      GL11.glScalef(2F * 1.2F, 2F * 1.2F, length * 1.2F)
      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)

      MirrorRenderHelper.renderDropOffSphere(1F, 8, 8, 0.06F, danmaku.shot.edgeColor, alpha)

      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      GlStateManager.disableBlend()
    }
  }
}
