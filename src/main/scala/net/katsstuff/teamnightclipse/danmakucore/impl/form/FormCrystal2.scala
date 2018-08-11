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

import net.katsstuff.teamnightclipse.danmakucore.lib.LibFormName
import net.katsstuff.teamnightclipse.mirror.client.helper.MirrorRenderHelper
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * A One sided crystal. Pointy on one side.
  */
private[danmakucore] class FormCrystal2 extends AbstractFormCrystal(LibFormName.CRYSTAL2) {

  @SideOnly(Side.CLIENT)
  override protected def createCrystal(color: Int, alpha: Float, dist: Double): Unit = {
    GL11.glPushMatrix()

    GL11.glTranslatef(0F, 0F, 1F)
    MirrorRenderHelper.drawCone(color, alpha, dist)
    GL11.glTranslatef(0F, 0F, -1F)
    MirrorRenderHelper.drawCylinder(color, alpha, dist)
    GL11.glTranslatef(0F, 0F, -0.5F)
    MirrorRenderHelper.drawDisk(color, alpha, dist)

    GL11.glPopMatrix()
  }
}
