/*
 * This class was created by <Katrix>. It's distributed as
 * part of the DanmakuCore Mod. Get the Source Code in github:
 * https://github.com/Katrix-/DanmakuCore
 *
 * DanmakuCore is Open Source and distributed under the
 * the DanmakuCore license: https://github.com/Katrix-/DanmakuCore/blob/master/LICENSE.md
 */
package net.katsstuff.danmakucore.entity.spellcard.spellcardbar

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
