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
package net.katsstuff.teamnightclipse.danmakucore.entity.spellcard.spellcardbar

import java.util.UUID

import scala.beans.BeanProperty

import net.minecraft.util.text.ITextComponent

/**
  * Represents the text that appears when a spellcard is declared.
  * @param uuid A [[UUID]] that identifies this info.
  * @param name The name shown when rendering this info.
  * @param mirrorText If the rendered text should be mirrored.
  */
abstract class SpellcardInfo(
    @BeanProperty val uuid: UUID,
    private var name: ITextComponent,
    private var mirrorText: Boolean
) {

  /**
    * The name shown when rendering this info.
    */
  def getName: ITextComponent = name

  /**
    * Sets the name shown when rendering this info.
    */
  def setName(name: ITextComponent): Unit = this.name = name

  /**
    * If the rendered text should be mirrored.
    */
  def shouldMirrorText: Boolean = mirrorText

  /**
    * Sets if the rendered text should be mirrored.
    */
  def setMirrorText(mirrorText: Boolean): Unit = this.mirrorText = mirrorText

  /**
    * Get the X position where to render this.
    */
  def getPosX: Float

  /**
    * Get the Y position where to render this.
    */
  def getPosY: Float
}
