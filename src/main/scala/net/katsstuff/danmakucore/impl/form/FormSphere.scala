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
import net.katsstuff.danmakucore.data.Quat
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.handler.DanmakuState
import net.katsstuff.danmakucore.lib.LibFormName
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

//Name parameter for adding special effects to sphere
private[danmakucore] class FormSphere(name: String = LibFormName.DEFAULT) extends FormGeneric(name) {

  //noinspection ConvertExpressionToSAM
  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = new IRenderForm() {
    @SideOnly(Side.CLIENT)
    override def renderForm(danmaku: DanmakuState, x: Double, y: Double, z: Double, orientation: Quat, partialTicks: Float, manager: RenderManager): Unit = {
      val color = danmaku.shot.color
      val alpha = 0.3F

      RenderHelper.transformDanmaku(danmaku.shot, orientation)

      RenderHelper.drawSphere(0xFFFFFF, 1F)

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.2F, 1.2F, 1.2F)
      RenderHelper.drawSphere(color, alpha)
      GlStateManager.depthMask(true)
      GlStateManager.disableBlend()
    }
  }
}
