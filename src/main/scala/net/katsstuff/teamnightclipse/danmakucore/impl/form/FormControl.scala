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
import net.katsstuff.teamnightclipse.danmakucore.data.ShotData
import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.IRenderForm
import net.katsstuff.teamnightclipse.danmakucore.lib.LibFormName
import net.katsstuff.teamnightclipse.mirror.data.Quat
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

private[danmakucore] class FormControl extends FormGeneric(LibFormName.CONTROL) {

  @SideOnly(Side.CLIENT)
  override protected def createRenderer: IRenderForm =
    (_: DanmakuState, _: Double, _: Double, _: Double, _: Quat, _: Float, _: RenderManager) => () //NO-OP

  override def onShotDataChange(oldShot: ShotData, newShot: ShotData): ShotData =
    newShot.setSize(oldShot.sizeX, oldShot.sizeY, oldShot.sizeZ)
}
