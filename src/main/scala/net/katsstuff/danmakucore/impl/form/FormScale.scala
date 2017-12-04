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
import net.katsstuff.danmakucore.entity.danmaku.EntityDanmaku
import net.katsstuff.danmakucore.entity.danmaku.form.IRenderForm
import net.katsstuff.danmakucore.lib.LibFormName
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] class FormScale extends FormGeneric(LibFormName.SCALE) {

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

      val length = 2F
      val alpha  = 0.35F

      RenderHelper.transformEntity(danmaku)

      GL11.glScalef(0.5F, 0.5F, length * 0.4F)
      RenderHelper.drawSphere(0xFFFFFF, 1F)

      GL11.glTranslatef(0F, 0F, -0.7F)
      GL11.glScalef(2F * 1.2F, 2F * 1.2F, length * 1.2F)
      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)

      RenderHelper.drawDropOffSphere(1F, 8, 8, 0.06F, color, alpha)

      GlStateManager.disableBlend()
    }
  }
}
