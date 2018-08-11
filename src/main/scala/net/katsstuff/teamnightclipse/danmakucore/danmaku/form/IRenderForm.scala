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
package net.katsstuff.teamnightclipse.danmakucore.danmaku.form

import net.katsstuff.teamnightclipse.danmakucore.client.helper.DanCoreRenderHelper
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.mirror.client.shaders.MirrorShaderProgram
import net.katsstuff.teamnightclipse.mirror.data.Quat
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import net.minecraft.util.ResourceLocation

/**
  * A interface used to render danmaku forms.
  * Only exists on the client to be safe.
  */
@SideOnly(Side.CLIENT)
trait IRenderForm {

  /**
    * Do your rendering like normal in here. Note that the texture is already
    * applied, lighting is disabled, and the entity is translated to it's
    * position. You do not need to call glPushMatrix or glPopMatrix.
    */
  def renderLegacy(
      danmaku: DanmakuState,
      x: Double,
      y: Double,
      z: Double,
      orientation: Quat,
      partialTicks: Float,
      manager: RenderManager
  ): Unit

  /**
    * Do more fancy and performant rendering using shaders and other good stuff.
    */
  def renderShaders(
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
    DanCoreRenderHelper.updateDanmakuShaderAttributes(shaderProgram, this, shot)
    renderLegacy(
      danmaku.copy(
        extra = danmaku.extra.copy(
          shot = shot.copy(
            edgeColor = DanCoreRenderHelper.OverwriteColorEdge,
            coreColor = DanCoreRenderHelper.OverwriteColorCore
          )
        )
      ),
      x,
      y,
      z,
      orientation,
      partialTicks,
      manager
    )
  }

  /**
    * The shader to use for renderShaders. The danmaku renderer will handle beginning and ending the shader program.
    */
  def shader(state: DanmakuState): ResourceLocation = DanCoreRenderHelper.baseDanmakuShaderLoc

  def defaultAttributeValues: Map[String, RenderingProperty] = Map.empty
}
