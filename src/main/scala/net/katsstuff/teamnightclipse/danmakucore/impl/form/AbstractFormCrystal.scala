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

import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.IRenderForm
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

import net.katsstuff.teamnightclipse.mirror.data.Quat

abstract private[danmakucore] class AbstractFormCrystal(name: String) extends FormGeneric(name) {

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
      val sizeX = shot.sizeX
      val sizeY = shot.sizeY
      val sizeZ = shot.sizeZ
      val dist  = x * x + y * y + z * z

      GlStateManager.rotate(orientation.toQuaternion)
      GL11.glScalef((sizeX / 3) * 2, (sizeY / 3) * 2, sizeZ)

      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE)
      GlStateManager.depthMask(false)
      GlStateManager.scale(1.2F, 1.2F, 1.2F)

      createCrystal(shot.edgeColor, 0.3F, dist)

      GlStateManager.depthMask(true)
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      GlStateManager.disableBlend()

      GlStateManager.scale(1 / 1.2F, 1 / 1.2F, 1 / 1.2F)

      createCrystal(shot.coreColor, 1F, dist)
    }
  }

  @SideOnly(Side.CLIENT)
  protected def createCrystal(color: Int, alpha: Float, dist: Double): Unit
}
