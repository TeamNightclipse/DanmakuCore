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

import net.katsstuff.danmakucore.client.helper.RenderHelper
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.lib.LibFormName
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] class FormPellet extends FormGeneric(LibFormName.PELLET) {

  //noinspection ConvertExpressionToSAM
  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm = new IRenderForm() {
    @SideOnly(Side.CLIENT)
    override def renderForm(
        danmaku: EntityDanmaku,
        x: Double,
        y: Double,
        z: Double,
        entityYaw: Float,
        partialTicks: Float,
        rendermanager: RenderManager
    ): Unit = {
      val color = danmaku.shotData.getColor
      val alpha = 0.3F

      val colorObj = new Color(color)
      val hsb      = new Array[Float](3)
      Color.RGBtoHSB(colorObj.getRed, colorObj.getGreen, colorObj.getBlue, hsb)
      hsb(1) = 0.15F
      hsb(2) = 1.0F

      RenderHelper.transformEntity(danmaku)

      RenderHelper.drawSphere(Color.getHSBColor(hsb(0), hsb(1), hsb(2)).getRGB, 1F)
      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.075F, 1.075F, 1.075F)
      RenderHelper.drawSphere(color, alpha)
      GlStateManager.depthMask(true)
      GlStateManager.disableBlend()
    }
  }
}
