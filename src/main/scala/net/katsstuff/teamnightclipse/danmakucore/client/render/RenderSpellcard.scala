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
import net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.EntitySpellcard
import net.katsstuff.teamnightclipse.danmakucore.item.ItemSpellcard
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.util.ResourceLocation

class RenderSpellcard(renderManager: RenderManager) extends Render[EntitySpellcard](renderManager) {

  override def doRender(
      entity: EntitySpellcard,
      x: Double,
      y: Double,
      z: Double,
      entityYaw: Float,
      partialTicks: Float
  ): Unit = {
    GL11.glPushMatrix()

    val size = 1.5F

    GL11.glTranslated(x, y, z)
    GL11.glScalef(size, size, size)
    GL11.glRotatef(entity.ticksExisted * 20F, 0.0F, 1.0F, 0.0F)
    GL11.glRotatef(30F, 0.0F, 0.0F, 1.0F)

    Minecraft.getMinecraft.getRenderItem
      .renderItem(ItemSpellcard.createStack(entity.getSpellcardType), ItemCameraTransforms.TransformType.GROUND)

    GL11.glPopMatrix()
  }

  override protected def getEntityTexture(entity: EntitySpellcard): ResourceLocation =
    DanmakuCore.resource("textures/white.png")
}
