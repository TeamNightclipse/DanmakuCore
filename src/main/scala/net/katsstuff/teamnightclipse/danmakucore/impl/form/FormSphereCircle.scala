/*
 * Copyright (C) 2018  Katrix
 * This file is part of DanmakuCore.
 *
 * DanmakuCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DanmakuCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DanmakuCore.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.katsstuff.teamnightclipse.danmakucore.impl.form

import org.lwjgl.opengl.GL11

import net.katsstuff.teamnightclipse.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.IRenderForm
import net.katsstuff.teamnightclipse.danmakucore.lib.LibFormName
import net.katsstuff.teamnightclipse.mirror.client.helper.MirrorRenderHelper
import net.katsstuff.teamnightclipse.mirror.data.Quat
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
      val shot  = danmaku.shot
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
