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
package net.katsstuff.teamnightclipse.danmakucore.client.render

import org.lwjgl.opengl.GL11

import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
import net.katsstuff.teamnightclipse.danmakucore.entity.EntityFallingData
import net.katsstuff.teamnightclipse.danmakucore.entity.EntityFallingData.DataType._
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, RenderHelper, Tessellator}
import net.minecraft.util.ResourceLocation

object RenderFallingData {
  final val ScoreGreenLocation = DanmakuCore.resource("textures/entity/falling/score_green.png")
  final val ScoreBlueLocation  = DanmakuCore.resource("textures/entity/falling/point_blue.png")
  final val PowerLocation      = DanmakuCore.resource("textures/entity/falling/power_small.png")
  final val BigPowerLocation   = DanmakuCore.resource("textures/entity/falling/power_big.png")
  final val LifeLocation       = DanmakuCore.resource("textures/entity/falling/life.png")
  final val BombLocation       = DanmakuCore.resource("textures/entity/falling/bomb.png")
}
class RenderFallingData(renderManager: RenderManager) extends Render[EntityFallingData](renderManager) {
  override def doRender(
      entity: EntityFallingData,
      x: Double,
      y: Double,
      z: Double,
      entityYaw: Float,
      partialTicks: Float
  ): Unit = {
    super.doRender(entity, x, y, z, entityYaw, partialTicks)

    GlStateManager.pushMatrix()
    bindEntityTexture(entity)
    GlStateManager.translate(x, y, z)

    RenderHelper.enableStandardItemLighting()
    GlStateManager.rotate(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F)
    GlStateManager.rotate(
      (if (renderManager.options.thirdPersonView == 2) -1
       else 1) * -renderManager.playerViewX,
      1.0F,
      0.0F,
      0.0F
    )

    val upperV = 0F
    val upperU = 1F
    val lowerV = 1F
    val lowerU = 0F
    val size   = 0.35F

    val alpha = entity.dataType == ScoreGreen

    if (alpha) {
      GlStateManager.enableBlend()
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
    }

    val tes = Tessellator.getInstance
    val bb  = tes.getBuffer
    bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL)
    bb.pos(size, size, 0D).tex(upperU, upperV).normal(0F, 1F, 0F).endVertex()
    bb.pos(-size, size, 0D).tex(lowerU, upperV).normal(0F, 1F, 0F).endVertex()
    bb.pos(-size, -size, 0D).tex(lowerU, lowerV).normal(0F, 1F, 0F).endVertex()
    bb.pos(size, -size, 0D).tex(upperU, lowerV).normal(0F, 1F, 0F).endVertex()
    tes.draw()

    if (alpha) GlStateManager.disableBlend()

    GlStateManager.popMatrix()
  }

  override protected def getEntityTexture(entity: EntityFallingData): ResourceLocation = entity.dataType match {
    case ScoreGreen => RenderFallingData.ScoreGreenLocation
    case ScoreBlue  => RenderFallingData.ScoreBlueLocation
    case Power      => RenderFallingData.PowerLocation
    case BigPower   => RenderFallingData.BigPowerLocation
    case Life       => RenderFallingData.LifeLocation
    case Bomb       => RenderFallingData.BombLocation
  }
}
