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
