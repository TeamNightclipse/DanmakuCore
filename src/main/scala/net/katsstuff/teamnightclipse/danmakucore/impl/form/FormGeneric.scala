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

import net.katsstuff.teamnightclipse.danmakucore.DanmakuCore
import net.katsstuff.teamnightclipse.danmakucore.danmaku.DanmakuState
import net.katsstuff.teamnightclipse.danmakucore.danmaku.form.{Form, IRenderForm}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

abstract class FormGeneric extends Form {

  final private val texture = DanmakuCore.resource("textures/white.png")

  @SideOnly(Side.CLIENT)
  protected var renderer: IRenderForm = _

  def this(name: String) {
    this()
    setRegistryName(name)
    DanmakuCore.proxy.initForm(this)
  }

  override def getTexture(danmaku: DanmakuState): ResourceLocation = texture

  @SideOnly(Side.CLIENT)
  override def initClient(): Unit = renderer = createRenderer

  @SideOnly(Side.CLIENT)
  override def getRenderer(danmaku: DanmakuState): IRenderForm = renderer

  @SideOnly(Side.CLIENT)
  protected def createRenderer: IRenderForm
}
